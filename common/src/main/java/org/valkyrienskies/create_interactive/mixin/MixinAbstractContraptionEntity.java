package org.valkyrienskies.create_interactive.mixin;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.create_interactive.mixin_logic.MixinAbstractContraptionEntityLogic;
import org.valkyrienskies.create_interactive.mixinducks.AbstractContraptionEntityDuck;

@Mixin(AbstractContraptionEntity.class)
public abstract class MixinAbstractContraptionEntity extends Entity implements AbstractContraptionEntityDuck {
    @Unique
    private Long vs$shadowShipId = null;
    @Unique
    private Boolean ci$forceStall = null;
    @Unique
    private boolean ci$stalledPreviously = false;

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

    @Override
    public void ci$setForceStall(final Boolean forceStall) {
        ci$forceStall = forceStall;
    }

    @Inject(method = "tickActors", at = @At("HEAD"), remap = false)
    private void preTickActors(final CallbackInfo ci) {
        ci$stalledPreviously = contraption.stalled;
    }

    @Inject(method = "tickActors", at = @At("RETURN"), remap = false)
    private void postTickActors(final CallbackInfo ci) {
        if (level.isClientSide || ci$forceStall == null) return;
        contraption.stalled = ci$forceStall;
        if (!ci$stalledPreviously && contraption.stalled)
            onContraptionStalled();
        entityData.set(STALLED, contraption.stalled);
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
}
