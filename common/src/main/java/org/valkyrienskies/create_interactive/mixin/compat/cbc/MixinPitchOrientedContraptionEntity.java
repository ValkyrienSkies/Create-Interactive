package org.valkyrienskies.create_interactive.mixin.compat.cbc;

import com.simibubi.create.content.contraptions.OrientedContraptionEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;
import rbasamoyai.createbigcannons.cannon_control.contraption.PitchOrientedContraptionEntity;

@Mixin(PitchOrientedContraptionEntity.class)
public abstract class MixinPitchOrientedContraptionEntity extends OrientedContraptionEntity {
    @Shadow private BlockPos controllerPos;

    public MixinPitchOrientedContraptionEntity(EntityType<?> type, Level world) {
        super(type, world);
    }

    @Inject(
            method = {"getPassengerPosition"},
            at = {@At("RETURN")},
            cancellable = true,
            remap = false
    )
    protected void interactiveGetPassengerPosition(Entity passenger, float partialTicks, CallbackInfoReturnable<Vec3> cir) {
        System.out.println("ran mixin");
        if (VSGameUtilsKt.getShipObjectManagingPos(passenger.level(), VectorConversionsMCKt.toJOML(this.position())) != null) {
            cir.setReturnValue(((Vec3)cir.getReturnValue()).add((double)0.0F, 0.1, (double)0.0F));
        }

    }
}
