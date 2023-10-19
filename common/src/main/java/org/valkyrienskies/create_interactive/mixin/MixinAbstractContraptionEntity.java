package org.valkyrienskies.create_interactive.mixin;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.create_interactive.dont_delete.CreateInteractiveEventsClient;
import org.valkyrienskies.create_interactive.dont_delete.CreateInteractiveUtil;
import org.valkyrienskies.create_interactive.mixinducks.AbstractContraptionEntityDuck;

@Mixin(AbstractContraptionEntity.class)
public abstract class MixinAbstractContraptionEntity extends Entity implements AbstractContraptionEntityDuck {
    @Unique
    private Long vs$shadowShipId = null;

    @Shadow
    protected Contraption contraption;

    @Unique
    private static final String SHADOW_SHIP_ID_NBT_KEY = "ShadowShipId";

    public MixinAbstractContraptionEntity(final EntityType<?> entityType, final Level level) {
        super(entityType, level);
    }

    @Override
    public void setShadowShipId(final Long shadowShipId) {
        vs$shadowShipId = shadowShipId;
    }

    @Override
    public Long getShadowShipId() {
        return vs$shadowShipId;
    }

    @Inject(method = "readAdditional", at = @At("TAIL"))
    private void preReadAdditional(final CompoundTag compound, final boolean spawnData, final CallbackInfo ci) {
        if (level.isClientSide) {
            if (spawnData && compound.contains(SHADOW_SHIP_ID_NBT_KEY)) {
                vs$shadowShipId = compound.getLong(SHADOW_SHIP_ID_NBT_KEY);
                CreateInteractiveEventsClient.INSTANCE.addShipToContraptionRef(vs$shadowShipId, AbstractContraptionEntity.class.cast(this));
            }
            return;
        }
        if (contraption == null) {
            return;
        }
        if (getShadowShipId() != null) {
            throw new IllegalStateException("Ship already exists");
        }
        setShadowShipId(CreateInteractiveUtil.INSTANCE.createShipForContraption((ServerLevel) level, contraption, new BlockPos(position())));
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void preTick(final CallbackInfo ci) {
        CreateInteractiveUtil.INSTANCE.updateShipShadow(AbstractContraptionEntity.class.cast(this));
    }

    @Inject(method = "writeAdditional", at = @At("HEAD"))
    private void preWriteSpawnData(final CompoundTag compound, final boolean spawnPacket, final CallbackInfo ci) {
        final Long shadowShipIdCopy = vs$shadowShipId;
        if (spawnPacket && shadowShipIdCopy != null) {
            compound.putLong(SHADOW_SHIP_ID_NBT_KEY, shadowShipIdCopy);
        }
    }
}
