package org.valkyrienskies.create_interactive.mixin.deployer;

import com.simibubi.create.content.kinetics.base.ShaftVisual;
import com.simibubi.create.content.kinetics.deployer.DeployerBlockEntity;
import com.simibubi.create.content.kinetics.deployer.DeployerVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.OrientedInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.create_interactive.mixin_logic.deployer.MixinDeployerRendererLogic;

@Mixin(DeployerVisual.class)
public abstract class MixinDeployerInstance extends ShaftVisual<DeployerBlockEntity> {
    @Shadow(remap = false)
    float progress;
    @Shadow
    @Final
    Direction facing;
    @Shadow(remap = false)
    @Final
    protected OrientedInstance pole;
    @Shadow(remap = false)
    protected OrientedInstance hand;

    public MixinDeployerInstance(VisualizationContext context, DeployerBlockEntity blockEntity, float partialTick) {
        super(context, blockEntity, partialTick);
    }

    /**
     * @author Triode
     * @reason Get the hand offset from the actor when this is rendering in a contraption with a ship shadow
     */
    @Inject(method = "updatePosition", at = @At("HEAD"), cancellable = true, remap = false)
    private void preBeginFrame(final CallbackInfo ci) {
        final Double actorDistance = MixinDeployerRendererLogic.INSTANCE.preGetHandOffset$create_interactive(blockEntity);
        if (actorDistance == null) {
            return;
        }
        progress = actorDistance.floatValue();

        final BlockPos blockPos = getVisualPosition();
        final Vec3i facingVec = facing.getNormal();

        final float x = blockPos.getX() + ((float) facingVec.getX()) * actorDistance.floatValue();
        final float y = blockPos.getY() + ((float) facingVec.getY()) * actorDistance.floatValue();
        final float z = blockPos.getZ() + ((float) facingVec.getZ()) * actorDistance.floatValue();

        pole.position(x, y, z).setChanged();
        hand.position(x, y, z).setChanged();

        ci.cancel();
    }
}
