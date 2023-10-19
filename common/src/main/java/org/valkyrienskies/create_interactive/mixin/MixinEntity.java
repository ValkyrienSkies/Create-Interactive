package org.valkyrienskies.create_interactive.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Entity.class)
public abstract class MixinEntity {
    @Shadow
    public Level level;

    @Shadow
    public abstract Vec3 position();

    /*
    @Inject(method = "setLevelCallback", at = @At("HEAD"))
    private void preSetLevelCallback(final EntityInLevelCallback levelCallback, final CallbackInfo ci) {
        if (this.level.isClientSide || !AbstractContraptionEntity.class.isInstance(this)) {
            return;
        }
        final AbstractContraptionEntity contraptionEntity = AbstractContraptionEntity.class.cast(this);
        final AbstractContraptionEntityDuck duck = AbstractContraptionEntityDuck.class.cast(this);
        final Contraption contraption = contraptionEntity.getContraption();
        final boolean isNull = (levelCallback == null) || levelCallback == EntityInLevelCallback.NULL;
        if (!isNull) {
            if (duck.getShadowShipId() != null) {
                throw new IllegalStateException("Ship already exists");
            }
            duck.setShadowShipId(CreateInteractiveUtil.INSTANCE.createShipForContraption((ServerLevel) level, contraption, new BlockPos(position())));
        } else {
            // TODO: Delete the ship
        }
    }

     */
}
