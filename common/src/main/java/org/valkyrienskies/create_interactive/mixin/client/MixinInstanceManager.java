package org.valkyrienskies.create_interactive.mixin.client;

import com.jozufozu.flywheel.backend.instancing.AbstractInstance;
import com.jozufozu.flywheel.backend.instancing.InstanceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.create_interactive.mixin_logic.client.MixinInstanceManagerLogic;

@Mixin(InstanceManager.class)
public class MixinInstanceManager {
    @Inject(method = "createInternal", at = @At("HEAD"), cancellable = true, remap = false)
    private void preCreateInternal(final Object obj, final CallbackInfoReturnable<AbstractInstance> cir) {
        MixinInstanceManagerLogic.INSTANCE.preCreateInternal$create_interactive(obj, cir);
    }
}
