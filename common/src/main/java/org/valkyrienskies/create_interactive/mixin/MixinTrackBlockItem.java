package org.valkyrienskies.create_interactive.mixin;

import com.simibubi.create.content.trains.track.TrackBlockItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.valkyrienskies.create_interactive.mixin_logic.client.MixinTrackPlacementClientLogic;

@Mixin(TrackBlockItem.class)
public class MixinTrackBlockItem {
    // TODO: Replace this @WrapOperation
    /**
     * Fix placing tracks on rotated ships not working properly
     */
    @Redirect(method = "useOn", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/trains/track/TrackBlockItem;getPlacementState(Lnet/minecraft/world/item/context/UseOnContext;)Lnet/minecraft/world/level/block/state/BlockState;"))
    private BlockState redirectUseOnInvokeGetPlacementState(
        final TrackBlockItem instance,
        final UseOnContext pContext
    ) {
        return MixinTrackPlacementClientLogic.INSTANCE.redirectClientTickInvokeGetPlacementState$create_interactive(instance, pContext);
    }
}
