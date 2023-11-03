package org.valkyrienskies.create_interactive.mixin;

import com.simibubi.create.AllInteractionBehaviours;
import com.simibubi.create.content.contraptions.behaviour.MovingInteractionBehaviour;
import com.simibubi.create.content.contraptions.behaviour.SimpleBlockMovingInteraction;
import com.simibubi.create.content.kinetics.deployer.DeployerMovingInteraction;
import com.simibubi.create.foundation.utility.AttachedRegistry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin(AllInteractionBehaviours.class)
public class MixinAllInteractionBehaviours {
    @Shadow
    @Final
    private static AttachedRegistry<Block, MovingInteractionBehaviour> BLOCK_BEHAVIOURS;
    @Shadow
    @Final
    private static List<AllInteractionBehaviours.BehaviourProvider> GLOBAL_BEHAVIOURS;

    /**
     * @author Triode
     * @reason Disable the dumb ones
     */
    @Nullable
    @Overwrite
    public static MovingInteractionBehaviour getBehaviour(BlockState state) {
        MovingInteractionBehaviour behaviour = BLOCK_BEHAVIOURS.get(state.getBlock());
        if (behaviour != null) {
            return ci$wrapGetBehavior(behaviour);
        }

        for (AllInteractionBehaviours.BehaviourProvider provider : GLOBAL_BEHAVIOURS) {
            behaviour = provider.getBehaviour(state);
            if (behaviour != null) {
                return ci$wrapGetBehavior(behaviour);
            }
        }

        return null;
    }

    @Unique
    private static MovingInteractionBehaviour ci$wrapGetBehavior(final MovingInteractionBehaviour behaviour) {
        if ((behaviour instanceof SimpleBlockMovingInteraction) || (behaviour instanceof DeployerMovingInteraction)) {
            // Disable the dumb ones
            return null;
        }
        return behaviour;
    }
}
