package org.valkyrienskies.create_interactive.mixin;

import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.Train;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.create_interactive.mixinducks.TrainDuck;

import java.util.List;

@Mixin(Train.class)
public abstract class MixinTrain implements TrainDuck {
    @Shadow
    public List<Carriage> carriages;
    @Shadow
    public List<Integer> carriageSpacing;

    @Shadow
    public abstract boolean disassemble(Direction assemblyDirection, BlockPos pos);

    // Allow trains to disassemble when they have no blocks
    @Inject(method = "canDisassemble", at = @At("HEAD"), cancellable = true, remap = false)
    private void preCanDisassemble(final CallbackInfoReturnable<Boolean> cir) {
        for (final Carriage carriage : carriages) {
            final CarriageContraptionEntity entity = carriage.anyAvailableEntity();
            if (entity == null) {
                return;
            }
            if (!entity.getContraption().getBlocks().isEmpty()) {
                return;
            }
        }
        cir.setReturnValue(true);
    }

    @Override
    public void ci$splitOrDisassemble() {
        for (int i = 0; i < carriages.size(); i++) {
            final Carriage carriage = carriages.get(i);
            final CarriageContraptionEntity entity = carriage.anyAvailableEntity();
            if (entity == null) {
                return;
            }
            if (entity.getContraption().getBlocks().isEmpty()) {
                if (carriages.size() == 1) {
                    final BlockPos pos = new BlockPos(entity.position());
                    disassemble(Direction.NORTH, pos);
                } else if (i == 0) {
                    // Remove first car
                    final Carriage firstCar = carriages.remove(0);
                    carriageSpacing.remove(0);
                    firstCar.setTrain(null);
                    entity.kill();
                } else if (i == carriages.size() - 1) {
                    // Remove last car
                    final Carriage lastCar = carriages.remove(carriages.size() - 1);
                    carriageSpacing.remove(carriages.size() - 1);
                    lastCar.setTrain(null);
                    entity.kill();
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
            }
        }
    }
}
