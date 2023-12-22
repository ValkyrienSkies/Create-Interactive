package org.valkyrienskies.create_interactive.mixin_logic

import com.simibubi.create.Create
import com.simibubi.create.content.trains.entity.Carriage
import com.simibubi.create.content.trains.entity.Train
import com.simibubi.create.content.trains.graph.TrackNodeLocation
import com.simibubi.create.foundation.utility.Iterate
import com.simibubi.create.foundation.utility.Pair
import com.simibubi.create.foundation.utility.VecHelper
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.resources.ResourceKey
import net.minecraft.util.Mth
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import org.joml.Vector3d
import org.joml.Vector3dc
import org.joml.Vector3i
import org.joml.Vector3ic
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import org.valkyrienskies.create_interactive.GameContent
import org.valkyrienskies.create_interactive.mixin.TrainAccessor
import org.valkyrienskies.mod.common.ValkyrienSkiesMod

internal object MixinTrainLogic {
    internal fun preCanDisassemble(train: Train, cir: CallbackInfoReturnable<Boolean?>) {
        for (carriage in train.carriages) {
            val entity = carriage.anyAvailableEntity() ?: return
            if (entity.contraption.blocks.isNotEmpty()) {
                return
            }
        }
        cir.setReturnValue(true)
    }

    internal fun splitOrDisassemble(train: Train) {
        val carriages = train.carriages
        val carriageSpacing = train.carriageSpacing
        for (i in carriages.indices) {
            val carriage: Carriage = carriages[i]
            val entity = carriage.anyAvailableEntity() ?: return
            if (entity.contraption.blocks.isEmpty()) {
                if (carriages.size == 1) {
                    val pos = BlockPos(entity.position())
                    train.disassemble(Direction.NORTH, pos)
                } else if (i == 0) {
                    // Remove first car
                    val firstCar: Carriage = carriages.removeAt(0)
                    carriageSpacing.removeAt(0)
                    firstCar.setTrain(null)
                    entity.kill()
                } else if (i == carriages.size - 1) {
                    // Remove last car
                    val lastCar: Carriage = carriages.removeAt(carriages.size - 1)
                    carriageSpacing.removeAt(carriageSpacing.size - 1)
                    lastCar.setTrain(null)
                    entity.kill()
                } else {
                    /* Disabled because this is broken
                    // Split train

                    final Train thisAs = Train.class.cast(this);

                    final List<Carriage> newCarriages = new ArrayList<>();
                    final List<Integer> newSpacing = new ArrayList<>();

                    for (int j = i + 1; j < carriages.size(); j++) {
                        newCarriages.add(carriages.get(j));
                    }

                    if (carriages.size() > i + 1) {
                        carriages.subList(i + 1, carriages.size()).clear();
                    }

                    for (int j = i + 1; j < carriageSpacing.size(); j++) {
                        newSpacing.add(carriageSpacing.get(j));
                    }
                    if (carriageSpacing.size() > i + 1) {
                        carriageSpacing.subList(i + 1, carriageSpacing.size()).clear();
                    }

                    carriageSpacing.remove(i);
                    carriageSpacing.remove(i - 1);

                    final Carriage deadCar = carriages.remove(i);

                    deadCar.setTrain(null);
                    final Level level = entity.level;
                    entity.kill();

                    final Train newTrain = new Train(UUID.randomUUID(), thisAs.owner, thisAs.graph, newCarriages, newSpacing, thisAs.doubleEnded);
                    newTrain.collectInitiallyOccupiedSignalBlocks();
                    Create.RAILWAYS.addTrain(newTrain);
                    AllPackets.getChannel().sendToClientsInServer(new TrainPacket(newTrain, true), level.getServer());

                    // Update this train too
                    AllPackets.getChannel().sendToClientsInServer(new TrainPacket(thisAs, true), level.getServer());
                    return;

                     */
                }
                return
            }
        }
    }

    private fun TrackNodeLocation.getLocationVec3i() = Vector3i(x, y, z)

    private fun Train.collidingWithBufferStop(): Boolean {
        val isTrainMovingForward = targetSpeed >= 0.0
        val leadingCar = if (isTrainMovingForward) carriages.first() else carriages.last()

        val leading = leadingCar.leadingPoint
        val trailing = leadingCar.trailingPoint

        if (leading.edge == null || trailing.edge == null) return false

        val bufferPoint = if (isTrainMovingForward) leading else trailing
        val position = bufferPoint.getPosition(graph)
        var bufferStopPos = BlockPos(position).offset(0, -1, 0)

        val node1Location: Vector3ic = bufferPoint.node1.location.getLocationVec3i()
        val node2Location: Vector3ic = bufferPoint.node2.location.getLocationVec3i()

        val normal: Vector3dc = Vector3d(node1Location.sub(node2Location, Vector3i())).normalize()
            .apply { if (!isTrainMovingForward) mul(-1.0) }

        // I'm not entirely sure why this works, but create seems to apply an offset in some directions, and this logic
        // handles it
        if (normal.x() > 0.0 || normal.z() > 0.0) {
            bufferStopPos = bufferStopPos.offset(-normal.x(), -normal.y(), -normal.z())
        }

        val dimension = bufferPoint.node1.location.dimension

        // Get the level that position is in
        val level = ValkyrienSkiesMod.currentServer?.allLevels?.first { it.dimension() == dimension }

        return level?.getBlockState(bufferStopPos)?.block == GameContent.BUFFER_STOP_BLOCK.get()
    }

    internal fun tickOnEndOfTrack(train: Train) {
        // Only derail if the train is moving, and the train isn't colliding with a buffer stop
        // (Why check if train.targetSpeed != 0.0? There's a bug where you can derail when colliding with a buffer stop if you mash W and D while driving into it)
        if (train.targetSpeed != 0.0 && train.collidingWithBufferStop()) {
            return
        }
        (train as TrainAccessor).migratingPoints.clear()
        train.navigation.cancelNavigation()
        train.setGraph(null)
        train.setDerailed(true)
    }

    internal fun findCollidingTrain(
        level: Level,
        start: Vec3,
        end: Vec3,
        ignore: Train,
        dimension: ResourceKey<Level>,
    ): Pair<Train, Vec3>? {
        for (train in Create.RAILWAYS.sided(level).trains.values) {
            if (train === ignore) continue
            // Don't collide with derailed trains.
            // TODO: Do this logic with VS2 collision events, eventually...
            if (train.derailed) continue
            val diff = end.subtract(start)
            var lastPoint: Vec3? = null
            for (otherCarriage in train.carriages) {
                for (betweenBits in Iterate.trueAndFalse) {
                    if (betweenBits && lastPoint == null) continue
                    val otherLeading = otherCarriage.leadingPoint
                    val otherTrailing = otherCarriage.trailingPoint
                    if (otherLeading.edge == null || otherTrailing.edge == null) continue
                    val otherDimension = otherLeading.node1.location.dimension
                    if (otherDimension != otherTrailing.node1.location.dimension) continue
                    if (otherDimension != dimension) continue
                    var start2 = otherLeading.getPosition(train.graph)
                    var end2 = otherTrailing.getPosition(train.graph)
                    if (betweenBits) {
                        end2 = start2
                        start2 = lastPoint
                    }
                    lastPoint = end2
                    if ((end.y < end2!!.y - 3 || end2.y < end.y - 3)
                        && (start.y < start2!!.y - 3 || start2.y < start.y - 3)
                    ) continue
                    val diff2 = end2.subtract(start2)
                    val normedDiff = diff.normalize()
                    val normedDiff2 = diff2.normalize()
                    var intersect = VecHelper.intersect(start, start2, normedDiff, normedDiff2, Direction.Axis.Y)
                    if (intersect == null) {
                        val intersectSphere = VecHelper.intersectSphere(start2, normedDiff2, start, .125)
                            ?: continue
                        if (!Mth.equal(
                                normedDiff2.dot(
                                    intersectSphere.subtract(start2)
                                        .normalize()
                                ), 1.0
                            )
                        ) continue
                        intersect = DoubleArray(2)
                        intersect[0] = intersectSphere.distanceTo(start) - .125
                        intersect[1] = intersectSphere.distanceTo(start2) - .125
                    }
                    if (intersect[0] > diff.length()) continue
                    if (intersect[1] > diff2.length()) continue
                    if (intersect[0] < 0) continue
                    if (intersect[1] < 0) continue
                    return Pair.of(train, start.add(normedDiff.scale(intersect[0])))
                }
            }
        }
        return null
    }
}
