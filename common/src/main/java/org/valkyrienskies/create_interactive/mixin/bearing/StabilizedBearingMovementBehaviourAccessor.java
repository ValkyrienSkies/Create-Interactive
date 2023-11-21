package org.valkyrienskies.create_interactive.mixin.bearing;

import com.simibubi.create.content.contraptions.bearing.StabilizedBearingMovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(StabilizedBearingMovementBehaviour.class)
public interface StabilizedBearingMovementBehaviourAccessor {
    @Invoker(value = "getCounterRotationAngle", remap = false)
    static float invokeGetCounterRotationAngle(MovementContext context, Direction facing, float renderPartialTicks) {
        return 0.0f;
    }
}
