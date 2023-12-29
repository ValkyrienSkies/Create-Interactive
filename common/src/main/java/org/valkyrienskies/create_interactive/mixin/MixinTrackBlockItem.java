package org.valkyrienskies.create_interactive.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.trains.track.TrackBlockItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.valkyrienskies.create_interactive.mixin_logic.client.MixinTrackPlacementClientLogic;

@Mixin(TrackBlockItem.class)
public class MixinTrackBlockItem {
    /**
     * Fix placing tracks on rotated ships not working properly
     */
    @WrapOperation(method = "useOn", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/trains/track/TrackBlockItem;getPlacementState(Lnet/minecraft/world/item/context/UseOnContext;)Lnet/minecraft/world/level/block/state/BlockState;"))
    private BlockState redirectUseOnInvokeGetPlacementState(
        final TrackBlockItem instance,
        final UseOnContext pContext,
        final Operation<BlockState> operation
    ) {
        return MixinTrackPlacementClientLogic.INSTANCE.redirectClientTickInvokeGetPlacementState$create_interactive(instance, pContext, operation);
    }
}
