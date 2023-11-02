package org.valkyrienskies.create_interactive.mixin;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.behaviour.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.trains.entity.CarriageContraption;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.Train;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.create_interactive.mixin_logic.MixinAbstractContraptionEntityLogic;
import org.valkyrienskies.create_interactive.mixinducks.AbstractContraptionEntityDuck;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

@Mixin(AbstractContraptionEntity.class)
public abstract class MixinAbstractContraptionEntity extends Entity implements AbstractContraptionEntityDuck {
    @Unique
    private Long vs$shadowShipId = null;

    @Shadow(remap = false)
    protected Contraption contraption;

    @Shadow(remap = false)
    @Final
    private static EntityDataAccessor<Boolean> STALLED;

    @Shadow(remap = false)
    protected abstract void onContraptionStalled();

    public MixinAbstractContraptionEntity(final EntityType<?> entityType, final Level level) {
        super(entityType, level);
    }

    @Override
    public void ci$setShadowShipId(final Long shadowShipId) {
        vs$shadowShipId = MixinAbstractContraptionEntityLogic.INSTANCE.setShadowShipId$create_interactive(
            AbstractContraptionEntity.class.cast(this), vs$shadowShipId, shadowShipId
        );
    }

    @Override
    public Long ci$getShadowShipId() {
        return vs$shadowShipId;
    }

    @Inject(method = "readAdditional", at = @At("RETURN"))
    private void preReadAdditional(final CompoundTag compound, final boolean spawnData, final CallbackInfo ci) {
        vs$shadowShipId = MixinAbstractContraptionEntityLogic.INSTANCE.preReadAdditional$create_interactive(
            AbstractContraptionEntity.class.cast(this), vs$shadowShipId, compound, spawnData, ci
        );
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void postTick(final CallbackInfo ci) {
        vs$shadowShipId = MixinAbstractContraptionEntityLogic.INSTANCE.postTick$create_interactive(
            AbstractContraptionEntity.class.cast(this), vs$shadowShipId
        );
    }

    @Inject(method = "writeAdditional", at = @At("HEAD"))
    private void writeAdditional(final CompoundTag compound, final boolean spawnPacket, final CallbackInfo ci) {
        MixinAbstractContraptionEntityLogic.INSTANCE.writeAdditional$create_interactive(compound, vs$shadowShipId);
    }

    @Inject(method = "disassemble", at = @At("RETURN"), remap = false)
    private void postDisassemble(final CallbackInfo ci) {
        MixinAbstractContraptionEntityLogic.INSTANCE.postDisassemble$create_interactive(level, vs$shadowShipId);
    }

    @Shadow
    public abstract Vec3 reverseRotation(Vec3 localPos, float partialTicks);

    // Fix drills on sub-contraptions not triggering
    @Inject(method = "shouldActorTrigger", at = @At("HEAD"), cancellable = true, remap = false)
    protected void shouldActorTrigger(MovementContext context, StructureTemplate.StructureBlockInfo blockInfo, MovementBehaviour actor, Vec3 actorPosition, BlockPos gridPosition, CallbackInfoReturnable<Boolean> cir) {
        Vec3 previousPosition = context.position;
        if (previousPosition == null) {
            cir.setReturnValue(false);
            return;
        }

        final Ship ship = VSGameUtilsKt.getShipManagingPos(context.world, actorPosition);

        if (ship == null) {
            context.motion = actorPosition.subtract(previousPosition);
        } else {
            final Vector3dc prevPos = ship.getPrevTickTransform().getShipToWorld().transformPosition(VectorConversionsMCKt.toJOML(previousPosition));
            final Vector3dc curPos = ship.getTransform().getShipToWorld().transformPosition(VectorConversionsMCKt.toJOML(actorPosition));
            context.motion = VectorConversionsMCKt.toMinecraft(curPos.sub(prevPos, new Vector3d()));
            // TODO: Should I scale this to be the magnitude of the relative speed of the sub-contraption?
        }

        if (!level.isClientSide() && context.contraption.entity instanceof CarriageContraptionEntity cce
            && cce.getCarriage() != null) {
            Train train = cce.getCarriage().train;
            double actualSpeed = train.speedBeforeStall != null ? train.speedBeforeStall : train.speed;
            context.motion = context.motion.normalize()
                .scale(Math.abs(actualSpeed));
        }

        Vec3 relativeMotion = context.motion;
        relativeMotion = reverseRotation(relativeMotion, 1);
        context.relativeMotion = relativeMotion;

        final boolean result = !new BlockPos(previousPosition).equals(gridPosition)
            || (context.relativeMotion.length() > 0 || context.contraption instanceof CarriageContraption)
            && context.firstMovement;
        cir.setReturnValue(result);
    }
}
