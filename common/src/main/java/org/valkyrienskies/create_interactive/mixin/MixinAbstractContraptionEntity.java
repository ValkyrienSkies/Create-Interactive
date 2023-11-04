package org.valkyrienskies.create_interactive.mixin;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.behaviour.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.elevator.ElevatorColumn;
import com.simibubi.create.content.contraptions.elevator.ElevatorContraption;
import com.simibubi.create.content.decoration.slidingDoor.DoorControl;
import com.simibubi.create.content.decoration.slidingDoor.DoorControlBehaviour;
import com.simibubi.create.content.decoration.slidingDoor.SlidingDoorBlock;
import com.simibubi.create.content.decoration.slidingDoor.SlidingDoorBlockEntity;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.station.GlobalStation;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.create_interactive.mixin_logic.MixinAbstractContraptionEntityLogic;
import org.valkyrienskies.create_interactive.mixinducks.AbstractContraptionEntityDuck;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.Map;

@Mixin(AbstractContraptionEntity.class)
public abstract class MixinAbstractContraptionEntity extends Entity implements AbstractContraptionEntityDuck {
    @Unique
    private Long vs$shadowShipId = null;

    @Shadow(remap = false)
    protected Contraption contraption;

    @Unique
    private DoorControl ci$forcedDoorControls = null;

    @Unique
    private boolean ci$forcedDoorFinishedFirstTick = false;

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
        if (!level.isClientSide && vs$shadowShipId != null) {
            final ServerShip serverShip = VSGameUtilsKt.getShipObjectWorld((ServerLevel) level).getAllShips().getById(vs$shadowShipId);
            if (serverShip != null) {
                final DoorControl prevControl = ci$forcedDoorControls;
                ci$forcedDoorControls = ci$getCurrentDoorControl();
                // Skip when ci$forcedDoorFinishedFirstTick is false
                if (ci$forcedDoorFinishedFirstTick && prevControl != ci$forcedDoorControls) {
                    final boolean shouldOpen;
                    final DoorControl toUse;

                    if (ci$forcedDoorControls == null) {
                        // Close all doors matching the prev direction
                        shouldOpen = false;
                        toUse = prevControl;
                    } else {
                        // Open all doors matching the direction
                        shouldOpen = true;
                        toUse = ci$forcedDoorControls;
                    }

                    serverShip.getActiveChunksSet().forEach((chunkX, chunkZ) -> {
                        final LevelChunk levelChunk = level.getChunk(chunkX, chunkZ);
                        for (final Map.Entry<BlockPos, BlockEntity> entry : levelChunk.getBlockEntities().entrySet()) {
                            if (!(entry.getValue() instanceof SlidingDoorBlockEntity slidingDoorBlockEntity)) continue;
                            final BlockState blockState = slidingDoorBlockEntity.getBlockState();
                            if (!(blockState.getBlock() instanceof SlidingDoorBlock slidingDoorBlock)) continue;
                            if (toUse != DoorControl.NONE) {
                                slidingDoorBlock.setOpen(null, level, blockState, entry.getKey(), shouldOpen);
                            }
                        }
                    });
                }
                ci$forcedDoorFinishedFirstTick = true;
            }
        }
    }

    @Unique
    private DoorControl ci$getCurrentDoorControl() {
        final Entity entity = Entity.class.cast(this);
        final Vector3dc motion = new Vector3d(entity.position().x - entity.xo, entity.position().y - entity.yo, entity.position().z - entity.zo);
        // Tick sliding doors
        final boolean canOpen = motion.length() < 1 / 128f && !contraption.entity.isStalled()
            || contraption instanceof ElevatorContraption ec && ec.arrived;
        if (!canOpen) return null;

        DoorControlBehaviour doorControlBehaviour = null;
        if (contraption instanceof ElevatorContraption ec)
            doorControlBehaviour = ci$getElevatorDoorControl(ec);
        if (CarriageContraptionEntity.class.isInstance(this))
            doorControlBehaviour = ci$getTrainStationDoorControl(CarriageContraptionEntity.class.cast(this));
        if (doorControlBehaviour != null) {
            return doorControlBehaviour.mode;
        } else {
            return null;
        }
    }

    @Unique
    private DoorControlBehaviour ci$getElevatorDoorControl(ElevatorContraption ec) {
        Integer currentTargetY = ec.getCurrentTargetY(level);
        if (currentTargetY == null)
            return null;
        ElevatorColumn.ColumnCoords columnCoords = ec.getGlobalColumn();
        if (columnCoords == null)
            return null;
        ElevatorColumn elevatorColumn = ElevatorColumn.get(level, columnCoords);
        if (elevatorColumn == null)
            return null;
        return BlockEntityBehaviour.get(level, elevatorColumn.contactAt(currentTargetY),
            DoorControlBehaviour.TYPE);
    }

    @Unique
    private DoorControlBehaviour ci$getTrainStationDoorControl(CarriageContraptionEntity cce) {
        Carriage carriage = cce.getCarriage();
        if (carriage == null || carriage.train == null)
            return null;
        GlobalStation currentStation = carriage.train.getCurrentStation();
        if (currentStation == null)
            return null;

        BlockPos stationPos = currentStation.getBlockEntityPos();
        ResourceKey<Level> stationDim = currentStation.getBlockEntityDimension();
        MinecraftServer server = cce.level.getServer();
        if (server == null)
            return null;
        ServerLevel stationLevel = server.getLevel(stationDim);
        if (stationLevel == null || !stationLevel.isLoaded(stationPos))
            return null;
        return BlockEntityBehaviour.get(stationLevel, stationPos, DoorControlBehaviour.TYPE);
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
