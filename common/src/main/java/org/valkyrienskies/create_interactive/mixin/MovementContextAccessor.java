package org.valkyrienskies.create_interactive.mixin;

import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * This accessor exists because proguard is having trouble accessing MovementContext fields in kotlin
 */
@Mixin(MovementContext.class)
public interface MovementContextAccessor {
    @Accessor("motion")
    Vec3 getMotion();

    @Accessor("motion")
    void setMotion(Vec3 motion);

    @Accessor("relativeMotion")
    Vec3 getRelativeMotion();

    @Accessor("relativeMotion")
    void setRelativeMotion(Vec3 relativeMotion);

    @Accessor(value = "firstMovement",remap = false)
    boolean getFirstMovement();

    @Accessor(value = "contraption",remap = false)
    Contraption getContraption();
}
