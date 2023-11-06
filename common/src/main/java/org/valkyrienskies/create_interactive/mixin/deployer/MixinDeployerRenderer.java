package org.valkyrienskies.create_interactive.mixin.deployer;

import com.simibubi.create.content.kinetics.deployer.DeployerBlockEntity;
import com.simibubi.create.content.kinetics.deployer.DeployerRenderer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.create_interactive.mixin_logic.deployer.MixinDeployerRendererLogic;

@Mixin(DeployerRenderer.class)
public class MixinDeployerRenderer {
    /**
     * Get the hand offset from the actor when this is rendering in a contraption with a ship shadow
     */
    @Inject(method = "getHandOffset", at = @At("HEAD"), cancellable = true, remap = false)
    private void preGetHandOffset(final DeployerBlockEntity be, final float partialTicks, final BlockState blockState, final CallbackInfoReturnable<Vec3> cir) {
        MixinDeployerRendererLogic.INSTANCE.preGetHandOffset$create_interactive(be, partialTicks, blockState, cir);
    }
}
