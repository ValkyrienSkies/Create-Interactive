package org.valkyrienskies.create_interactive.mixin.deployer;

import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.kinetics.deployer.DeployerBlockEntity;
import com.simibubi.create.content.kinetics.deployer.DeployerRenderer;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.create_interactive.CreateInteractiveUtil;

import static com.simibubi.create.content.kinetics.base.DirectionalKineticBlock.FACING;

@Mixin(DeployerRenderer.class)
public class MixinDeployerRenderer {
    @Inject(method = "getHandOffset", at = @At("HEAD"), cancellable = true, remap = false)
    private void preGetHandOffset(final DeployerBlockEntity be, final float partialTicks, final BlockState blockState, final CallbackInfoReturnable<Vec3> cir) {
        final Pair<StructureTemplate.StructureBlockInfo, MovementContext> actorAtPos = CreateInteractiveUtil.INSTANCE.getActorAtPos(be.getLevel(), be.getBlockPos());
        if (actorAtPos != null) {
            final MovementContext context = actorAtPos.getRight();
            double factor;
            if (context.contraption.stalled || context.position == null || context.data.contains("StationaryTimer")) {
                factor = Mth.sin(AnimationTickHolder.getRenderTime() * .5f) * .25f + .25f;
            } else {
                Vec3 center = VecHelper.getCenterOf(new BlockPos(context.position));
                double distance = context.position.distanceTo(center);
                double nextDistance = context.position.add(context.motion)
                    .distanceTo(center);
                factor = .5f - Mth.clamp(Mth.lerp(AnimationTickHolder.getPartialTicks(), distance, nextDistance), 0, 1);
            }

            Vec3 offset = Vec3.atLowerCornerOf(blockState.getValue(FACING)
                .getNormal()).scale(factor);
            cir.setReturnValue(offset);
        }
    }
}
