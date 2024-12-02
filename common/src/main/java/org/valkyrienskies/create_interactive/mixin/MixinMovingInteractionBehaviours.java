package org.valkyrienskies.create_interactive.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.actors.seat.SeatInteractionBehaviour;
import com.simibubi.create.content.contraptions.behaviour.SimpleBlockMovingInteraction;
import com.simibubi.create.content.kinetics.deployer.DeployerMovingInteraction;
import com.simibubi.create.content.processing.burner.BlazeBurnerInteractionBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.valkyrienskies.create_interactive.mixinducks.AbstractContraptionEntityDuck;

@Mixin(value = {
        BlazeBurnerInteractionBehaviour.class,
        DeployerMovingInteraction.class,
        SeatInteractionBehaviour.class,
        SimpleBlockMovingInteraction.class
})
public abstract class MixinMovingInteractionBehaviours {
    @WrapMethod(
            method = "handlePlayerInteraction"
    )
    private boolean preHandlePlayerInteraction(Player player, InteractionHand activeHand, BlockPos localPos, AbstractContraptionEntity contraptionEntity, Operation<Boolean> original) {
        if (contraptionEntity instanceof AbstractContraptionEntityDuck duck && duck.ci$getShadowShipId() != null) {
            return false;
        } else {
            return original.call(player, activeHand, localPos, contraptionEntity);
        }
    }
}
