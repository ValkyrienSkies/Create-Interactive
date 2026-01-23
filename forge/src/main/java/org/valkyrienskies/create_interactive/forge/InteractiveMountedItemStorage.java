package org.valkyrienskies.create_interactive.forge;

import com.simibubi.create.api.contraption.storage.item.MountedItemStorage;
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
    public int getSlots() {
        return wrapped.getSlots();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        wrapped = wrapped.type.mount(blockEntity.getLevel(), blockEntity.getBlockState(), blockEntity.getBlockPos(), blockEntity);
        return wrapped.getStackInSlot(slot);
    }

    @Override
    public @NotNull ItemStack insertItem(int i, @NotNull ItemStack arg, boolean bl) {
        wrapped = wrapped.type.mount(blockEntity.getLevel(), blockEntity.getBlockState(), blockEntity.getBlockPos(), blockEntity);
        ItemStack stack = wrapped.insertItem(i, arg, bl);
        wrapped.unmount(blockEntity.getLevel(), blockEntity.getBlockState(), blockEntity.getBlockPos(), blockEntity);
        return stack;
    }

    @Override
    public @NotNull ItemStack extractItem(int i, int j, boolean bl) {
        wrapped = wrapped.type.mount(blockEntity.getLevel(), blockEntity.getBlockState(), blockEntity.getBlockPos(), blockEntity);
        ItemStack stack = wrapped.extractItem(i, j, bl);
        wrapped.unmount(blockEntity.getLevel(), blockEntity.getBlockState(), blockEntity.getBlockPos(), blockEntity);
        return stack;
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
    public boolean isItemValid(int i, @NotNull ItemStack arg) {
        wrapped = wrapped.type.mount(blockEntity.getLevel(), blockEntity.getBlockState(), blockEntity.getBlockPos(), blockEntity);
        return wrapped.isItemValid(i, arg);
    }

    @Override
    public String toString() {
        return "Interactive Storage : " + wrapped.toString();
    }
}
