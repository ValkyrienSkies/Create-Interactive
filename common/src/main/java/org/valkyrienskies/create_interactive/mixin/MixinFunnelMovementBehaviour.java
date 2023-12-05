package org.valkyrienskies.create_interactive.mixin;

import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.logistics.funnel.FunnelMovementBehaviour;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.create_interactive.mixin_logic.MixinFunnelMovementBehaviourLogic;

@Mixin(FunnelMovementBehaviour.class)
public class MixinFunnelMovementBehaviour {
    /**
     * Fix this randomly crashing on client
     */
    @Inject(method = "extract", at = @At("HEAD"), cancellable = true)
    private void preExtract(final MovementContext context, final BlockPos pos, final CallbackInfo ci) {
        MixinFunnelMovementBehaviourLogic.INSTANCE.preExtract$create_interactive(context, ci);
    }
}
