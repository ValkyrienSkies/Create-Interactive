package org.valkyrienskies.create_interactive.mixin;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
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
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.create_interactive.CreateInteractiveEventsClient;
import org.valkyrienskies.create_interactive.CreateInteractiveUtil;
import org.valkyrienskies.create_interactive.mixinducks.AbstractContraptionEntityDuck;
import org.valkyrienskies.create_interactive.mixinducks.CarriageDuck;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.ShipSettingsKt;

@Mixin(AbstractContraptionEntity.class)
public abstract class MixinAbstractContraptionEntity extends Entity implements AbstractContraptionEntityDuck {
    @Unique
    private Long vs$shadowShipId = null;

    @Shadow(remap = false)
    protected Contraption contraption;

    @Unique
    private static final String SHADOW_SHIP_ID_NBT_KEY = "ShadowShipId";

    public MixinAbstractContraptionEntity(final EntityType<?> entityType, final Level level) {
        super(entityType, level);
    }

    @Override
    public void setShadowShipId(final Long shadowShipId) {
        if (CarriageContraptionEntity.class.isInstance(this)) {
            final CarriageContraptionEntity carriageContraptionEntity = CarriageContraptionEntity.class.cast(this);
            if (!((CarriageDuck) carriageContraptionEntity.getCarriage()).ci$doesCarriageEntityControlShip(carriageContraptionEntity, shadowShipId)) {
                // Do not set this if this entity doesn't control the ship
                return;
            }
        }
        final Long prevShipId = vs$shadowShipId;
        vs$shadowShipId = shadowShipId;
        final AbstractContraptionEntity thisAs = AbstractContraptionEntity.class.cast(this);
        if (shadowShipId != null) {
            CreateInteractiveUtil.INSTANCE.linkShipToContraption(shadowShipId, thisAs);
            final ServerShip serverShip = VSGameUtilsKt.getShipObjectWorld((ServerLevel) level).getAllShips().getById(shadowShipId);
            if (serverShip == null) {
                // How???!
                System.out.println("Absolute giga-sus!!!");
                return;
            }
            final CreateInteractiveUtil.ContraptionPosRot contraptionPosRot = CreateInteractiveUtil.INSTANCE.getContraptionPosRot(thisAs);
            CreateInteractiveUtil.INSTANCE.teleportShipToPosRot(contraptionPosRot, serverShip, (ServerLevel) level);
            // Make the ship static, so it won't be affected by physics
            serverShip.setStatic(true);
            // Don't let the ship teleport through dimensions on its own
            ShipSettingsKt.getSettings(serverShip).setChangeDimensionOnTouchPortals(false);
        } else if (prevShipId != null) {
            CreateInteractiveUtil.INSTANCE.unlinkShipToContraption(prevShipId, thisAs);
        }
    }

    @Override
    public Long getShadowShipId() {
        return vs$shadowShipId;
    }

    @Inject(method = "readAdditional", at = @At("RETURN"))
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
        if (compound.contains(SHADOW_SHIP_ID_NBT_KEY)) {
            vs$shadowShipId = compound.getLong(SHADOW_SHIP_ID_NBT_KEY);
        } else {
            setShadowShipId(CreateInteractiveUtil.INSTANCE.createShipForContraption((ServerLevel) level, contraption, new BlockPos(position())));
        }
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void postTick(final CallbackInfo ci) {
        // TODO: Its sus af that we have to keep linking the ship, but just do it!
        if (vs$shadowShipId != null) {
            if (CarriageContraptionEntity.class.isInstance(this)) {
                final CarriageContraptionEntity carriageContraptionEntity = CarriageContraptionEntity.class.cast(this);
                if (((CarriageDuck) carriageContraptionEntity.getCarriage()).ci$doesCarriageEntityControlShip(carriageContraptionEntity, vs$shadowShipId)) {
                    CreateInteractiveUtil.INSTANCE.linkShipToContraption(vs$shadowShipId, AbstractContraptionEntity.class.cast(this));
                } else {
                    CreateInteractiveUtil.INSTANCE.unlinkShipToContraption(vs$shadowShipId, AbstractContraptionEntity.class.cast(this));
                    vs$shadowShipId = null;
                    return;
                }
            }
        }
        CreateInteractiveUtil.INSTANCE.updateShipShadow(AbstractContraptionEntity.class.cast(this));
    }

    @Inject(method = "writeAdditional", at = @At("HEAD"))
    private void writeAdditional(final CompoundTag compound, final boolean spawnPacket, final CallbackInfo ci) {
        final Long shadowShipIdCopy = vs$shadowShipId;
        if (shadowShipIdCopy != null) {
            compound.putLong(SHADOW_SHIP_ID_NBT_KEY, shadowShipIdCopy);
        }
    }

    @Inject(method = "disassemble", at = @At("RETURN"), remap = false)
    private void postDisassemble(final CallbackInfo ci) {
        final Long shadowShipIdCopy = vs$shadowShipId;
        if (shadowShipIdCopy != null && level instanceof ServerLevel serverLevel) {
            final ServerShip serverShip = VSGameUtilsKt.getShipObjectWorld(serverLevel).getAllShips().getById(shadowShipIdCopy);
            if (serverShip != null) {
                VSGameUtilsKt.getShipObjectWorld((ServerLevel) level).deleteShip(serverShip);
            }
        }
    }
}
