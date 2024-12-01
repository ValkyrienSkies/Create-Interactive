package org.valkyrienskies.create_interactive.mixin.deployer;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.core.materials.oriented.OrientedData;
import com.simibubi.create.content.kinetics.base.ShaftInstance;
import com.simibubi.create.content.kinetics.deployer.DeployerBlockEntity;
import com.simibubi.create.content.kinetics.deployer.DeployerInstance;
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

@Mixin(DeployerInstance.class)
public abstract class MixinDeployerInstance extends ShaftInstance<DeployerBlockEntity> {
    @Shadow(remap = false)
    float progress;
    @Shadow
    @Final
    Direction facing;
    @Shadow(remap = false)
    @Final
    protected OrientedData pole;
    @Shadow(remap = false)
    protected OrientedData hand;

    public MixinDeployerInstance(MaterialManager materialManager, DeployerBlockEntity blockEntity) {
        super(materialManager, blockEntity);
    }

    /**
     * @author Triode
     * @reason Get the hand offset from the actor when this is rendering in a contraption with a ship shadow
     */
    @Inject(method = "beginFrame", at = @At("HEAD"), cancellable = true, remap = false)
    private void preBeginFrame(final CallbackInfo ci) {
        final Double actorDistance = MixinDeployerRendererLogic.INSTANCE.preGetHandOffset$create_interactive(blockEntity);
        if (actorDistance == null) {
            return;
        }
        progress = actorDistance.floatValue();

        final BlockPos blockPos = getInstancePosition();
        final Vec3i facingVec = facing.getNormal();

        final float x = blockPos.getX() + ((float) facingVec.getX()) * actorDistance.floatValue();
        final float y = blockPos.getY() + ((float) facingVec.getY()) * actorDistance.floatValue();
        final float z = blockPos.getZ() + ((float) facingVec.getZ()) * actorDistance.floatValue();

        pole.setPosition(x, y, z);
        hand.setPosition(x, y, z);

        ci.cancel();
    }
}
