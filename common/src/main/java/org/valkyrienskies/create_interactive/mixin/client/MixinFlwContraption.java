package org.valkyrienskies.create_interactive.mixin.client;

import com.jozufozu.flywheel.core.virtual.VirtualRenderWorld;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.render.ActorInstance;
import com.simibubi.create.content.contraptions.render.ContraptionProgram;
import com.simibubi.create.content.contraptions.render.ContraptionRenderInfo;
import com.simibubi.create.content.contraptions.render.FlwContraption;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.create_interactive.mixin_logic.client.MixinFlwContraptionLogic;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Mixin(FlwContraption.class)
public abstract class MixinFlwContraption extends ContraptionRenderInfo  {
    @Shadow
    @Final
    private FlwContraption.ContraptionInstanceWorld instanceWorld;

    public MixinFlwContraption(Contraption contraption, VirtualRenderWorld renderWorld) {
        super(contraption, renderWorld);
    }

    @Unique
    private final Map<BlockPos, ActorInstance> ci$actorToInstanceMap = new HashMap<>();

    /**
     * Completely disable contraption block rendering
     */
    @WrapOperation(method = "buildLayers", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/contraptions/Contraption;getRenderedBlocks()Ljava/util/Collection;"), remap = false)
    private Collection<StructureTemplate.StructureBlockInfo> redirectBuildLayersGetRenderedBlocks(final Contraption instance, final Operation<Collection<StructureTemplate.StructureBlockInfo>> operation) {
        return MixinFlwContraptionLogic.INSTANCE.redirectBuildLayersGetRenderedBlocks$create_interactive(instance, operation);
    }

    /**
     * Update actor rendering
     */
    @Inject(method = "tick", at = @At("HEAD"), remap = false)
    private void preTick(final CallbackInfo ci) {
        MixinFlwContraptionLogic.INSTANCE.preTick$create_interactive(instanceWorld, ci$actorToInstanceMap, contraption);
    }

    @Inject(method = "buildActors", at = @At("HEAD"), cancellable = true, remap = false)
    private void preBuildActors(final CallbackInfo ci) {
        // We have to initialize this, it can't be in the function
        ci$actorToInstanceMap.clear();
        MixinFlwContraptionLogic.INSTANCE.preBuildActors$create_interactive(instanceWorld, ci$actorToInstanceMap, contraption, ci);
    }

    @Inject(method = "renderStructureLayer", at = @At("HEAD"), cancellable = true, remap = false)
    private void preRenderStructureLayer(final RenderType layer, final ContraptionProgram shader, final CallbackInfo ci) {
        MixinFlwContraptionLogic.INSTANCE.preRenderStructureLayer$create_interactive(contraption, ci);
    }

    @Inject(method = "buildInstancedBlockEntities", at = @At("HEAD"), cancellable = true, remap = false)
    private void preBuildInstancedBlockEntities(final CallbackInfo ci) {
        MixinFlwContraptionLogic.INSTANCE.preBuildInstancedBlockEntities$create_interactive(contraption, ci);
    }
}
