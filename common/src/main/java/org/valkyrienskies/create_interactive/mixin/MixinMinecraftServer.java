package org.valkyrienskies.create_interactive.mixin;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.create_interactive.mixin_logic.MixinMinecraftServerLogic;

@Mixin(value = MinecraftServer.class, priority = 999)
public abstract class MixinMinecraftServer {
    @Shadow
    public abstract Iterable<ServerLevel> getAllLevels();

    @Inject(
        method = "tickServer",
        at = @At("RETURN")
    )
    private void postTick(final CallbackInfo ci) {
        MixinMinecraftServerLogic.INSTANCE.postTick(getAllLevels());
    }
}
