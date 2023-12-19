package org.valkyrienskies.create_interactive.mixin;

import com.simibubi.create.content.trains.entity.Navigation;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.entity.TrainMigration;
import com.simibubi.create.content.trains.graph.TrackGraph;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.create_interactive.mixin_logic.MixinTrainLogic;
import org.valkyrienskies.create_interactive.mixinducks.TrainDuck;

import java.util.List;

@Mixin(Train.class)
public abstract class MixinTrain implements TrainDuck {
    @Shadow
    public boolean derailed;
    @Shadow
    List<TrainMigration> migratingPoints;
    @Shadow
    public Navigation navigation;
    @Shadow
    public TrackGraph graph;

    // Allow trains to disassemble when they have no blocks
    @Inject(method = "canDisassemble", at = @At("HEAD"), cancellable = true, remap = false)
    private void preCanDisassemble(final CallbackInfoReturnable<Boolean> cir) {
        MixinTrainLogic.INSTANCE.preCanDisassemble(Train.class.cast(this), cir);
    }

    @Override
    public void ci$splitOrDisassemble() {
        MixinTrainLogic.INSTANCE.splitOrDisassemble(Train.class.cast(this));
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/trains/entity/TrainStatus;endOfTrack()V"))
    private void idk(final Level level, final CallbackInfo ci) {
        // TODO: Move this to MixinTrainLogic
        System.out.println("Did it!");
        // TODO: Only derail if the next block isn't a buffer stop
        migratingPoints.clear();
        navigation.cancelNavigation();
        graph = null;
        derailed = true;
    }
}
