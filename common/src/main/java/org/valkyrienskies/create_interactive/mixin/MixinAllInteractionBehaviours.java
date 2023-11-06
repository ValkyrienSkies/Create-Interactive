package org.valkyrienskies.create_interactive.mixin;

import com.simibubi.create.AllInteractionBehaviours;
import com.simibubi.create.content.contraptions.behaviour.MovingInteractionBehaviour;
import com.simibubi.create.foundation.utility.AttachedRegistry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.valkyrienskies.create_interactive.mixin_logic.MixinAllInteractionBehavioursLogic;

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
        return MixinAllInteractionBehavioursLogic.INSTANCE.getBehaviour$create_interactive(state, BLOCK_BEHAVIOURS, GLOBAL_BEHAVIOURS);
    }
}
