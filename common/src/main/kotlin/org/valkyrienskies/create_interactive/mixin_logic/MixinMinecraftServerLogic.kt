package org.valkyrienskies.create_interactive.mixin_logic

import com.simibubi.create.content.contraptions.AbstractContraptionEntity
import net.minecraft.server.level.ServerLevel
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.properties.ShipId
import org.valkyrienskies.core.api.ships.properties.ShipTransform
import org.valkyrienskies.create_interactive.CreateInteractiveUtil
import org.valkyrienskies.create_interactive.mixin.AbstractContraptionEntityAccessor
import org.valkyrienskies.create_interactive.mixinducks.AbstractContraptionEntityDuck
import org.valkyrienskies.mod.common.getShipManagingPos
import org.valkyrienskies.mod.common.isBlockInShipyard
import org.valkyrienskies.mod.common.shipObjectWorld
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.mod.mixinducks.mod_compat.create.MixinAbstractContraptionEntityDuck
import java.util.ArrayDeque
import java.util.Queue

internal object MixinMinecraftServerLogic {
    fun postTick(allLevels: Iterable<ServerLevel>) = allLevels.forEach { postTickLevel(it) }

    private fun postTickLevel(serverLevel: ServerLevel) {
        // Make a dag of contraptions
        val shipToNode: MutableMap<ServerShip, DagNode> = HashMap()
        val rootNodes: MutableList<DagNode> = ArrayList()
        val potentialRoots: MutableSet<ServerShip> = HashSet()

        // Create nodes
        serverLevel.allEntities.filterIsInstance<AbstractContraptionEntity>().filter { it.isAlive }.forEach { entity ->
            val shadowShipId: ShipId =
                (entity as AbstractContraptionEntityDuck).`ci$getShadowShipId`() ?: return@forEach
            val serverShip: ServerShip = serverLevel.shipObjectWorld.allShips.getById(shadowShipId) ?: return@forEach
            val dagNode = DagNode(entity, serverShip)
            shipToNode[serverShip] = dagNode
            if (!serverLevel.isBlockInShipyard(entity.position())) {
                rootNodes.add(dagNode)
            } else {
                val parentShip: ServerShip = serverLevel.getShipManagingPos(entity.position().toJOML()) ?: return@forEach
                if (shipToNode.containsKey(parentShip)) return@forEach
                potentialRoots.add(parentShip)
            }
        }

        // TODO: Need to account for moving ships, ideally we want to run this code after the physics frames have been applied
        //       Maybe we need an event for after the physics frames are applied?
        rootNodes.addAll(potentialRoots.filterNot(shipToNode::containsKey)
            .map { DagNode(null, it).apply { shipToNode[it] = this } })

        // Create dag
        shipToNode.values.forEach { dagNode ->
            val entity = dagNode.contraptionEntity ?: return@forEach
            val parentShip: ServerShip = serverLevel.getShipManagingPos(entity.position().toJOML()) ?: return@forEach
            val parentNode: DagNode = shipToNode[parentShip] ?: return@forEach
            if (parentNode.children == null) parentNode.children = ArrayList()
            parentNode.children!!.add(dagNode)
        }

        val parentTransformMap: MutableMap<ServerShip, ShipTransform> = HashMap()

        // Compute positions, start at root ships, then compute child positions
        rootNodes.forEach { rootNode ->
            exploreDag(rootNode) { curNode, parentNode ->
                if (curNode.contraptionEntity == null) {
                    // Root node with no entity, set the transform
                    parentTransformMap[curNode.serverShip] = curNode.serverShip.transform
                    return@exploreDag
                }
                val parentTransform: ShipTransform? = if (parentNode != null)
                    parentTransformMap[parentNode.serverShip]!! else null
                val newPosRot = CreateInteractiveUtil.getContraptionPosRot(curNode.contraptionEntity, parentTransform)
                val newTransform =
                    CreateInteractiveUtil.updateShipShadow(curNode.contraptionEntity, curNode.serverShip, newPosRot)
                parentTransformMap[curNode.serverShip] = newTransform
            }
        }

        fun setStalled(entity: AbstractContraptionEntity, stalled: Boolean) {
            entity as AbstractContraptionEntityAccessor
            val stalledPreviously = entity.contraption.stalled
            entity.contraption.stalled = stalled
            if (!stalledPreviously && entity.contraption.stalled)
                entity.invokeOnContraptionStalled()
            entity.entityData.set(entity.stalled, entity.contraption.stalled)
            (entity as MixinAbstractContraptionEntityDuck).`vs$setForceStall`(true)
        }

        // Propagate isStalled from children to parents
        fun computeIsStalled(rootNode: DagNode): Boolean {
            // Reset force stalled
            (rootNode.contraptionEntity as MixinAbstractContraptionEntityDuck?)?.`vs$setForceStall`(false)

            var isStalled = false
            rootNode.children?.forEach {
                if (computeIsStalled(it)) {
                    isStalled = true
                }
            }
            if (rootNode.contraptionEntity != null && isStalled) {
                setStalled(rootNode.contraptionEntity, true)
            }
            return isStalled or (rootNode.contraptionEntity?.isStalled ?: false)
        }

        rootNodes.forEach { computeIsStalled(it) }

        // TODO: Fix ships colliding with sub-contraptions
    }

    private inline fun exploreDag(rootNode: DagNode, function: (curNode: DagNode, parentNode: DagNode?) -> Unit) {
        val fringe: Queue<Pair<DagNode, DagNode?>> = ArrayDeque()
        fringe.add(rootNode to null)
        while (fringe.isNotEmpty()) {
            val (explored, parent) = fringe.remove()
            function(explored, parent)
            explored.children?.forEach {
                fringe.add(it to explored)
            }
        }
    }

    private class DagNode(
        val contraptionEntity: AbstractContraptionEntity?,
        val serverShip: ServerShip,
        var children: MutableList<DagNode>? = null,
    )
}
