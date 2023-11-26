package org.valkyrienskies.create_interactive.mixin.kinetics;

import com.simibubi.create.content.kinetics.RotationPropagator;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.create_interactive.mixin_logic.MixinRotationPropagatorLogic;

import java.util.List;

@Mixin(RotationPropagator.class)
public class MixinRotationPropagator {
    @Inject(method = "getPotentialNeighbourLocations", at = @At("RETURN"), remap = false)
    private static void postGetPotentialNeighbourLocations(
        final KineticBlockEntity be,
        final CallbackInfoReturnable<List<BlockPos>> cir
    ) {
        MixinRotationPropagatorLogic.INSTANCE.postGetPotentialNeighbourLocations$create_interactive(be, cir);
    }
}
