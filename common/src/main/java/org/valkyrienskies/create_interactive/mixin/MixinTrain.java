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

    @Inject(
        method = "tick",
        at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/trains/entity/TrainStatus;endOfTrack()V")
    )
    private void tickOnEndOfTrack(final Level level, final CallbackInfo ci) {
        MixinTrainLogic.INSTANCE.tickOnEndOfTrack$create_interactive(Train.class.cast(this));
    }
}
