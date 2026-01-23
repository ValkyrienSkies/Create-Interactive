package org.valkyrienskies.create_interactive.mixin.client;

import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.render.ClientContraption;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.create_interactive.mixin_logic.client.MixinClientContraptionLogic;

import java.util.BitSet;

@Mixin(ClientContraption.class)
public abstract class MixinClientContraption {

    @Shadow
    @Final
    private Contraption contraption;

    @Inject(method = "getRenderedBlocks", at = @At("RETURN"), remap = false, cancellable = true)
    private void wrapRenderedBlocks(CallbackInfoReturnable<ClientContraption.RenderedBlocks> cir) {
        MixinClientContraptionLogic.INSTANCE.wrapGetRenderedBlocks$create_interactive(contraption, cir);
    }

    @Inject(method = "setupRenderLevelAndRenderedBlockEntities", at = @At(value = "HEAD"), remap = false, cancellable = true)
    private void wrapSetupBlockEntities(CallbackInfo ci){
        MixinClientContraptionLogic.INSTANCE.preSetupStructure$create_interactive(contraption, ci);
    }

    @Inject(method = "getAndAdjustShouldRenderBlockEntities", at = @At(value = "RETURN"), remap = false, cancellable = true)
    private void adjustShouldRender(CallbackInfoReturnable<BitSet> cir) {
        MixinClientContraptionLogic.INSTANCE.postGetShouldRender$create_interactive(contraption, cir);
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
