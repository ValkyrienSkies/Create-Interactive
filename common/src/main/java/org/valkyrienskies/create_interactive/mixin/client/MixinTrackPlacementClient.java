package org.valkyrienskies.create_interactive.mixin.client;

import com.simibubi.create.content.trains.track.TrackBlockItem;
import com.simibubi.create.content.trains.track.TrackPlacement;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.valkyrienskies.create_interactive.mixin_logic.client.MixinTrackPlacementClientLogic;

@Mixin(TrackPlacement.class)
public class MixinTrackPlacementClient {
    // TODO: Replace this @WrapOperation
    @Redirect(method = "clientTick", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/trains/track/TrackBlockItem;getPlacementState(Lnet/minecraft/world/item/context/UseOnContext;)Lnet/minecraft/world/level/block/state/BlockState;"))
    private static BlockState redirectClientTickInvokeGetPlacementState(
        final TrackBlockItem instance,
        final UseOnContext pContext
    ) {
        return MixinTrackPlacementClientLogic.INSTANCE.redirectClientTickInvokeGetPlacementState$create_interactive(instance, pContext);
    }
}
