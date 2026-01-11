package org.valkyrienskies.create_interactive.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.MountedStorageManager;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import kotlin.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.create_interactive.mixin_logic.MixinAbstractContraptionEntityLogic;
import org.valkyrienskies.create_interactive.mixinducks.AbstractContraptionEntityDuck;
import org.valkyrienskies.create_interactive.services.NoOptimize;
import org.valkyrienskies.mod.common.entity.ShipMountedToData;
import org.valkyrienskies.mod.common.entity.ShipMountedToDataProvider;

import java.util.ArrayList;
import java.util.List;

@Mixin(AbstractContraptionEntity.class)
public abstract class MixinAbstractContraptionEntity extends Entity implements AbstractContraptionEntityDuck, ShipMountedToDataProvider {
    @Unique
    private Long ci$shadowShipId = null;

    @Unique
    private boolean ci$hasTickedThisTick = false;

    @Unique
    private AbstractContraptionEntity.ContraptionRotationState ci$prevTickRotationState = null;

    @Unique
    private List<Pair<BlockPos, BlockPos>> ci$propagators = null;

    @Shadow(remap = false)
    protected Contraption contraption;

    @Shadow(remap = false)
    public abstract AbstractContraptionEntity.ContraptionRotationState getRotationState();

    @Unique
    private MixinAbstractContraptionEntityLogic.ExtraData ci$extraData;

    public MixinAbstractContraptionEntity(final EntityType<?> entityType, final Level level) {
        super(entityType, level);
    }

    @Override
    @NotNull
    public List<Pair<BlockPos, BlockPos>> ci$getPropagators() {
        return ci$propagators;
    }

    @Inject(method = "<init>", at = @At("RETURN"), remap = false)
    private void postInit(final CallbackInfo ci) {
        ci$extraData = new MixinAbstractContraptionEntityLogic.ExtraData();
        ci$propagators = new ArrayList<>();
    }

    @Override
    public void ci$setShadowShipId(final Long shadowShipId) {
        ci$shadowShipId = MixinAbstractContraptionEntityLogic.INSTANCE.setShadowShipId$create_interactive(
            AbstractContraptionEntity.class.cast(this), ci$shadowShipId, shadowShipId
        );
    }

    @Override
    public Long ci$getShadowShipId() {
        return ci$shadowShipId;
    }

    @Inject(method = "readAdditional", at = @At("RETURN"))
    private void preReadAdditional(final CompoundTag compound, final boolean spawnData, final CallbackInfo ci) {
        ci$shadowShipId = MixinAbstractContraptionEntityLogic.INSTANCE.preReadAdditional$create_interactive(
            AbstractContraptionEntity.class.cast(this), ci$shadowShipId, compound, spawnData
        );
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void preTick(final CallbackInfo ci) {
        ci$hasTickedThisTick = true;
        ci$prevTickRotationState = getRotationState();
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void postTick(final CallbackInfo ci) {
        ci$shadowShipId = MixinAbstractContraptionEntityLogic.INSTANCE.postTick$create_interactive(
            AbstractContraptionEntity.class.cast(this), ci$shadowShipId, ci$extraData
        );
    }

    @Inject(method = "writeAdditional", at = @At("HEAD"))
    private void writeAdditional(final CompoundTag compound, final boolean spawnPacket, final CallbackInfo ci) {
        MixinAbstractContraptionEntityLogic.INSTANCE.writeAdditional$create_interactive(compound, ci$shadowShipId);
    }

    /**
     * Disassemble subcontractors when disassembling contraptions
     */
    @Inject(method = "disassemble", at = @At("HEAD"), remap = false)
    private void preDissemble(final CallbackInfo ci) {
        MixinAbstractContraptionEntityLogic.INSTANCE.preDisassemble$create_interactive(
            AbstractContraptionEntity.class.cast(this), level(), ci$shadowShipId
        );
    }

    @Inject(method = "disassemble", at = @At("RETURN"), remap = false)
    private void postDisassemble(final CallbackInfo ci) {
        MixinAbstractContraptionEntityLogic.INSTANCE.postDisassemble$create_interactive(level(), ci$shadowShipId);
    }

    /**
     * Fix drills on sub-contraptions not triggering
     */
    @Inject(method = "shouldActorTrigger", at = @At("HEAD"), cancellable = true, remap = false)
    protected void shouldActorTrigger(MovementContext context, StructureTemplate.StructureBlockInfo blockInfo, MovementBehaviour actor, Vec3 actorPosition, BlockPos gridPosition, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(MixinAbstractContraptionEntityLogic.INSTANCE.overwriteShouldActorTrigger$create_interactive(AbstractContraptionEntity.class.cast(this), context, actorPosition, gridPosition));
    }

    @Override
    @NotNull
    public AbstractContraptionEntity.ContraptionRotationState ci$getPrevTickRotationState() {
        if (ci$prevTickRotationState != null) {
            return ci$prevTickRotationState;
        } else {
            return getRotationState();
        }
    }

    @NoOptimize
    @Override
    @Nullable
    public ShipMountedToData provideShipMountedToData(
        @NotNull final Entity passenger,
        @Nullable final Float partialTicks
    ) {
        return MixinAbstractContraptionEntityLogic.INSTANCE.provideShipMountedToData$create_interactive(
            AbstractContraptionEntity.class.cast(this), passenger
        );
    }

    @Inject(method = "getPassengerPosition", at = @At("HEAD"), cancellable = true)
    private void preGetPassengerPosition(final Entity passenger, final float partialTicks, final CallbackInfoReturnable<Vec3> cir) {
        MixinAbstractContraptionEntityLogic.INSTANCE.preGetPassengerPosition$create_interactive(AbstractContraptionEntity.class.cast(this), passenger, partialTicks, cir);
    }

    @WrapOperation(
            method = "handlePlayerInteraction",
            at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/contraptions/MountedStorageManager;handlePlayerStorageInteraction(Lcom/simibubi/create/content/contraptions/Contraption;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/core/BlockPos;)Z", remap = true),
            remap = false
    )
    private boolean preHandleStorageInteraction(MountedStorageManager storageManager, Contraption contraption, Player player, BlockPos blockPos, Operation<Boolean> original){
        return MixinAbstractContraptionEntityLogic.INSTANCE.preStorageInteraction$create_interactive(AbstractContraptionEntity.class.cast(this), storageManager, contraption, player, blockPos, original);
    }

    @Override
    public boolean ci$hasTickedThisTick() {
        return ci$hasTickedThisTick;
    }

    @Override
    public void ci$resetHasTickedThisTick() {
        ci$hasTickedThisTick = false;
    }
}
