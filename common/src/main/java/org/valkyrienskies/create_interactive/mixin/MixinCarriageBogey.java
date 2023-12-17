package org.valkyrienskies.create_interactive.mixin;

import com.simibubi.create.content.trains.entity.CarriageBogey;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.create_interactive.mixin_logic.client.MixinCarriageBogeyLogic;

@Mixin(CarriageBogey.class)
public class MixinCarriageBogey {
    @Inject(method = "updateCouplingAnchor", at = @At("HEAD"), cancellable = true)
    private void preUpdateCouplingAnchor(
        final Vec3 entityPos,   // Ignore
        final float entityXRot, // Ignore
        final float entityYRot, // Ignore
        final int bogeySpacing,
        final float partialTicks,
        final boolean leading,
        final CallbackInfo ci
    ) {
        MixinCarriageBogeyLogic.INSTANCE.preUpdateCouplingAnchor$create_interactive(
            CarriageBogey.class.cast(this), bogeySpacing, partialTicks, leading, ci
        );
    }
}
