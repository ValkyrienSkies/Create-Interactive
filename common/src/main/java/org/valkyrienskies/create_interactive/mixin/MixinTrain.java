package org.valkyrienskies.create_interactive.mixin;

import com.simibubi.create.content.trains.entity.Train;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.create_interactive.mixin_logic.MixinTrainLogic;
import org.valkyrienskies.create_interactive.mixinducks.TrainDuck;

@Mixin(Train.class)
public abstract class MixinTrain implements TrainDuck {
    // Allow trains to disassemble when they have no blocks
    @Inject(method = "canDisassemble", at = @At("HEAD"), cancellable = true, remap = false)
    private void preCanDisassemble(final CallbackInfoReturnable<Boolean> cir) {
        MixinTrainLogic.INSTANCE.preCanDisassemble$create_interactive(Train.class.cast(this), cir);
    }

    @Override
    public void ci$splitOrDisassemble() {
        MixinTrainLogic.INSTANCE.splitOrDisassemble$create_interactive(Train.class.cast(this));
    }

    /**
     * Derail trains when they hit the end of the track, unless there is a buffer stop to stop them.
     */
    @Inject(
        method = "tick",
        at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/trains/entity/TrainStatus;endOfTrack()V")
    )
    private void tickOnEndOfTrack(final Level level, final CallbackInfo ci) {
        MixinTrainLogic.INSTANCE.tickOnEndOfTrack$create_interactive(Train.class.cast(this));
    }

    /**
     * Re-rail derailed trains when they are nearby rails
     */
    /**
    @Inject(
        method = "tick", at = @At("RETURN")
    )
    private void postTick(final Level level, final CallbackInfo ci) {
        MixinTrainLogic.INSTANCE.postTick$create_interactive(level, Train.class.cast(this));
    }
     */
}
