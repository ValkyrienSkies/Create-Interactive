package org.valkyrienskies.create_interactive.mixin_logic

import com.simibubi.create.content.trains.entity.Carriage
import com.simibubi.create.content.trains.entity.Train
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import org.valkyrienskies.create_interactive.mixin.TrainAccessor

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

    internal fun tickOnEndOfTrack(train: Train) {
        // TODO: Only derail if the next block isn't a buffer stop
        (train as TrainAccessor).migratingPoints.clear()
        train.navigation.cancelNavigation()
        train.graph = null
        train.derailed = true
    }
}
