package org.valkyrienskies.create_interactive.mixin;

import com.simibubi.create.AllInteractionBehaviours;
import com.simibubi.create.api.behaviour.interaction.MovingInteractionBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.create_interactive.mixin_logic.MixinAllInteractionBehavioursLogic;

@Mixin(MovingInteractionBehaviour.class)
public class MixinAllInteractionBehaviours {
    /**
     * @author Triode
     * @reason Disable the dumb ones
     */
//    @Inject(method = "getBehaviour", at = @At("RETURN"), cancellable = true)
//    private static void postGetBehaviour(final CallbackInfoReturnable<MovingInteractionBehaviour> cir) {
//        cir.setReturnValue(MixinAllInteractionBehavioursLogic.INSTANCE.postGetBehavior$create_interactive(cir.getReturnValue()));
//    }
}
