package org.valkyrienskies.create_interactive.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ClientContraption;
import com.simibubi.create.content.contraptions.render.ContraptionVisual;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import dev.engine_room.flywheel.api.task.Plan;
import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visual.LightUpdatedVisual;
import dev.engine_room.flywheel.api.visual.ShaderLightVisual;
import dev.engine_room.flywheel.api.visual.TickableVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.visual.AbstractEntityVisual;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.apache.commons.lang3.tuple.MutablePair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.create_interactive.mixin_logic.client.MixinFlwContraptionLogic;

@Mixin(ContraptionVisual.class)
public abstract class MixinFlwContraption <E extends AbstractContraptionEntity> extends AbstractEntityVisual<E> implements DynamicVisual, TickableVisual, LightUpdatedVisual, ShaderLightVisual {

    public MixinFlwContraption(VisualizationContext ctx, E entity, float partialTick) {
        super(ctx, entity, partialTick);
    }

    /**
     * Completely disable contraption block rendering
     */
    @WrapOperation(method = "setupStructure", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/contraptions/render/ClientContraption;getRenderedBlocks()Lcom/simibubi/create/content/contraptions/render/ClientContraption$RenderedBlocks;"), remap = false)
    private ClientContraption.RenderedBlocks redirectBuildLayersGetRenderedBlocks(ClientContraption instance, Operation<ClientContraption.RenderedBlocks> operation) {
        return MixinFlwContraptionLogic.INSTANCE.redirectBuildLayersGetRenderedBlocks$create_interactive(entity.getContraption(), operation);
    }

    /**
     * Update actor rendering
     */
//    @Inject(method = "planTick", at = @At("HEAD"), remap = false)
//    private void preTick(CallbackInfoReturnable<Plan<TickableVisual.Context>> cir) {
//        MixinFlwContraptionLogic.INSTANCE.preTick$create_interactive(instanceWorld, ci$actorToInstanceMap, contraption);
//    }
//
//    @Inject(method = "setupActor", at = @At("HEAD"), cancellable = true, remap = false)
//    private void preBuildActors(MutablePair<StructureTemplate.StructureBlockInfo, MovementContext> actor, VirtualRenderWorld renderLevel, CallbackInfo ci) {
//        MixinFlwContraptionLogic.INSTANCE.preBuildActors$create_interactive(renderLevel, actor, ContraptionVisual.class.cast(this), ci);
//    }
//
//    @Inject(method = "renderStructureLayer", at = @At("HEAD"), cancellable = true, remap = false)
//    private void preRenderStructureLayer(final RenderType layer, final ContraptionProgram shader, final CallbackInfo ci) {
//        MixinFlwContraptionLogic.INSTANCE.preRenderStructureLayer$create_interactive(contraption, ci);
//    }
//
//    @Inject(method = "buildInstancedBlockEntities", at = @At("HEAD"), cancellable = true, remap = false)
//    private void preBuildInstancedBlockEntities(final CallbackInfo ci) {
//        MixinFlwContraptionLogic.INSTANCE.preBuildInstancedBlockEntities$create_interactive(contraption, ci);
//    }
}
