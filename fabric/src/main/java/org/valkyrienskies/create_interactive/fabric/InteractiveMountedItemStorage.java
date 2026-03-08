package org.valkyrienskies.create_interactive.fabric;

import com.simibubi.create.api.contraption.storage.item.MountedItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InteractiveMountedItemStorage extends MountedItemStorage {
    MountedItemStorage wrapped;

    @NotNull
    BlockEntity blockEntity;

    public InteractiveMountedItemStorage(@NotNull BlockEntity blockEntity, MountedItemStorage storage) {
        super(storage.type);
        this.blockEntity = blockEntity;
        this.wrapped = storage;
    }

    @Override
    public void unmount(Level level, BlockState state, BlockPos pos, @Nullable BlockEntity be) {
        wrapped.unmount(level, state, pos, be);
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        wrapped = wrapped.type.mount(blockEntity.getLevel(), blockEntity.getBlockState(), blockEntity.getBlockPos(), blockEntity);
        return wrapped.getStackInSlot(slot);
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        wrapped = wrapped.type.mount(blockEntity.getLevel(), blockEntity.getBlockState(), blockEntity.getBlockPos(), blockEntity);
        wrapped.setStackInSlot(slot, stack);
        wrapped.unmount(blockEntity.getLevel(), blockEntity.getBlockState(), blockEntity.getBlockPos(), blockEntity);
    }

    @Override
    public int getSlotLimit(int slot) {
        return wrapped.getSlotLimit(slot);
    }

    @Override
    public int getSlotCount() {
        return wrapped.getSlotCount();
    }

    @Override
    public SingleSlotStorage<ItemVariant> getSlot(int slot) {
        return new InteractiveSingleSlotStorage(blockEntity, this, slot);
    }

    @Override
    public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        wrapped = wrapped.type.mount(blockEntity.getLevel(), blockEntity.getBlockState(), blockEntity.getBlockPos(), blockEntity);
        long result = wrapped.insert(resource, maxAmount, transaction);
        wrapped.unmount(blockEntity.getLevel(), blockEntity.getBlockState(), blockEntity.getBlockPos(), blockEntity);
        return result;
    }

    @Override
    public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        wrapped = wrapped.type.mount(blockEntity.getLevel(), blockEntity.getBlockState(), blockEntity.getBlockPos(), blockEntity);
        long result = wrapped.extract(resource, maxAmount, transaction);
        wrapped.unmount(blockEntity.getLevel(), blockEntity.getBlockState(), blockEntity.getBlockPos(), blockEntity);
        return result;
    }

    @Override
    public long insertSlot(int slot, ItemVariant resource, long maxAmount, TransactionContext transaction) {
        wrapped = wrapped.type.mount(blockEntity.getLevel(), blockEntity.getBlockState(), blockEntity.getBlockPos(), blockEntity);
        long result = wrapped.insertSlot(slot, resource, maxAmount, transaction);
        wrapped.unmount(blockEntity.getLevel(), blockEntity.getBlockState(), blockEntity.getBlockPos(), blockEntity);
        return result;
    }

    @Override
    public long extractSlot(int slot, ItemVariant resource, long maxAmount, TransactionContext transaction) {
        wrapped = wrapped.type.mount(blockEntity.getLevel(), blockEntity.getBlockState(), blockEntity.getBlockPos(), blockEntity);
        long result = wrapped.extractSlot(slot, resource, maxAmount, transaction);
        wrapped.unmount(blockEntity.getLevel(), blockEntity.getBlockState(), blockEntity.getBlockPos(), blockEntity);
        return result;
    }

    @Override
    public String toString() {
        return "Interactive Storage : " + wrapped.toString();
    }

    public class InteractiveSingleSlotStorage implements SingleSlotStorage<ItemVariant> {
        BlockEntity blockEntity;
        InteractiveMountedItemStorage outer;
        int slot;

        public InteractiveSingleSlotStorage(BlockEntity be, InteractiveMountedItemStorage outer, int slot) {
            this.blockEntity = be;
            this.outer = outer;
            this.slot = slot;
        }

        @Override
        public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
            wrapped = wrapped.type.mount(blockEntity.getLevel(), blockEntity.getBlockState(), blockEntity.getBlockPos(), blockEntity);
            long result = wrapped.insert(resource, maxAmount, transaction);
            outer.unmount(blockEntity.getLevel(), blockEntity.getBlockState(), blockEntity.getBlockPos(), blockEntity);
            return result;
        }

        @Override
        public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
            wrapped = wrapped.type.mount(blockEntity.getLevel(), blockEntity.getBlockState(), blockEntity.getBlockPos(), blockEntity);
            long result = wrapped.extract(resource, maxAmount, transaction);
            outer.unmount(blockEntity.getLevel(), blockEntity.getBlockState(), blockEntity.getBlockPos(), blockEntity);
            return result;
        }

        @Override
        public boolean isResourceBlank() {
            return wrapped.getSlot(slot).isResourceBlank();
        }

        @Override
        public ItemVariant getResource() {
            return wrapped.getSlot(slot).getResource();
        }

        @Override
        public long getAmount() {
            return wrapped.getSlot(slot).getAmount();
        }

        @Override
        public long getCapacity() {
            return wrapped.getSlot(slot).getCapacity();
        }
    }
}
