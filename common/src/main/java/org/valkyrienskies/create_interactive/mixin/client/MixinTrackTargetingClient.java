package org.valkyrienskies.create_interactive.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.trains.graph.EdgePointType;
import com.simibubi.create.content.trains.graph.TrackGraphLocation;
import com.simibubi.create.content.trains.track.BezierTrackPointLocation;
import com.simibubi.create.content.trains.track.TrackTargetingBlockItem;
import com.simibubi.create.content.trains.track.TrackTargetingClient;
import com.simibubi.create.foundation.render.SuperRenderTypeBuffer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.create_interactive.mixin_logic.client.MixinTrackTargetingClientLogic;

@Mixin(TrackTargetingClient.class)
public class MixinTrackTargetingClient {
    @Shadow
    static BlockPos lastHovered;
    @Shadow
    static boolean lastDirection;
    @Shadow
    static EdgePointType<?> lastType;
    @Shadow
    static BezierTrackPointLocation lastHoveredBezierSegment;
    @Shadow
    static TrackTargetingBlockItem.OverlapResult lastResult;
    @Shadow
    static TrackGraphLocation lastLocation;

    /**
     * Fixed train station overlay not rendering on ships
     */
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private static void preRender(
        final PoseStack ms,
        final SuperRenderTypeBuffer buffer,
        final Vec3 camera,
        final CallbackInfo ci
    ) {
        MixinTrackTargetingClientLogic.INSTANCE.render$create_interactive(
            lastHovered,
            lastDirection,
            lastType,
            lastHoveredBezierSegment,
            lastResult,
            lastLocation,
            ms,
            buffer,
            camera,
            ci
        );
    }
}
