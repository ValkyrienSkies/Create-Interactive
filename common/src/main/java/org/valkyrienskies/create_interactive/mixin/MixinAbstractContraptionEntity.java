package org.valkyrienskies.create_interactive.mixin;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.behaviour.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.create_interactive.mixin_logic.MixinAbstractContraptionEntityLogic;
import org.valkyrienskies.create_interactive.mixinducks.AbstractContraptionEntityDuck;

@Mixin(AbstractContraptionEntity.class)
public abstract class MixinAbstractContraptionEntity extends Entity implements AbstractContraptionEntityDuck {
    @Unique
    private Long vs$shadowShipId = null;

    @Shadow(remap = false)
    protected Contraption contraption;

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

    /**
     * Fix drills on sub-contraptions not triggering
     */
    @Inject(method = "shouldActorTrigger", at = @At("HEAD"), cancellable = true, remap = false)
    protected void shouldActorTrigger(MovementContext context, StructureTemplate.StructureBlockInfo blockInfo, MovementBehaviour actor, Vec3 actorPosition, BlockPos gridPosition, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(MixinAbstractContraptionEntityLogic.INSTANCE.overwriteShouldActorTrigger$create_interactive(AbstractContraptionEntity.class.cast(this), context, actorPosition, gridPosition));
    }
}
