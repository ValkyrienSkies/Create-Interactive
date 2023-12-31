package org.valkyrienskies.create_interactive.mixin;

import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.create_interactive.mixin_logic.MixinEntityLogic;

@Mixin(Entity.class)
public class MixinEntity {
    /**
     * @reason Fix picking up minecart contraptions leaving ships behind
     */
    @Inject(method = "discard", at = @At("RETURN"))
    private void postDiscard(final CallbackInfo ci) {
        MixinEntityLogic.INSTANCE.postDiscard$create_interactive(Entity.class.cast(this));
    }

    /**
     * Don't kill contraption entities that are out of the world if they have a ship shadow
     */
    @Inject(method = "onBelowWorld", at = @At("HEAD"), cancellable = true)
    private void preOnBelowWorld(CallbackInfo ci) {
        MixinEntityLogic.INSTANCE.preOnBelowWorld$create_interactive(Entity.class.cast(this), ci);
    }
}
