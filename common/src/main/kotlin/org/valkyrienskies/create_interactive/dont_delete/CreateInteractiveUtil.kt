package org.valkyrienskies.create_interactive.dont_delete

import com.simibubi.create.content.contraptions.Contraption
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.Block
import org.joml.Vector3i
import org.joml.Vector3ic
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.properties.ShipId
import org.valkyrienskies.mod.common.dimensionId
import org.valkyrienskies.mod.common.shipObjectWorld
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.mod.common.yRange

object CreateInteractiveUtil {
    fun createShipForContraption(level: ServerLevel, contraption: Contraption, blockPos: BlockPos): ShipId {
        // Try adding the rigid body of this entity from the world
        val serverShip: ServerShip = level.shipObjectWorld.createNewShipAtBlock(blockPos.toJOML(), false, 1.0, level.dimensionId)

        // Anchor at ship center
        val shipCenter: Vector3ic = serverShip.chunkClaim.getCenterBlockCoordinates(level.yRange, Vector3i())

        for ((pos, value) in contraption.blocks) {
            // TODO: Do I need to sub this???
            val localPos = pos // .subtract(contraption.anchor)
            val newPos = localPos.offset(shipCenter.x(), shipCenter.y(), shipCenter.z())
            val flags =
                Block.UPDATE_MOVE_BY_PISTON or Block.UPDATE_SUPPRESS_DROPS or Block.UPDATE_KNOWN_SHAPE or Block.UPDATE_CLIENTS or Block.UPDATE_IMMEDIATE
            level.setBlock(newPos, value.state, flags)
        }

        return serverShip.id
    }
}
