package org.valkyrienskies.create_interactive.mixin.client;

import com.jozufozu.flywheel.core.virtual.VirtualRenderWorld;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ActorInstance;
import com.simibubi.create.content.contraptions.render.ContraptionRenderInfo;
import com.simibubi.create.content.contraptions.render.FlwContraption;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.apache.commons.lang3.tuple.MutablePair;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.create_interactive.CreateInteractiveUtil;
import org.valkyrienskies.create_interactive.mixinducks.ContraptionDuck;
import org.valkyrienskies.create_interactive.mixinducks.ContraptionInstanceManagerDuck;

import java.util.Collection;
import java.util.Collections;
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
    private final Map<BlockPos, ActorInstance> ci$actorToInstaceMap = new HashMap<>();

    /**
     * Completely disable contraption block rendering
     */
    @WrapOperation(method = "buildLayers", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/contraptions/Contraption;getRenderedBlocks()Ljava/util/Collection;"))
    private Collection<StructureTemplate.StructureBlockInfo> redirectBuildLayersGetRenderedBlocks(final Contraption instance, final Operation<Collection<StructureTemplate.StructureBlockInfo>> operation) {
        // Only disable block rendering if the contraption has a ship
        if (CreateInteractiveUtil.INSTANCE.doesContraptionHaveShipLoaded(instance)) {
            return Collections.EMPTY_LIST;
        } else {
            return operation.call(instance);
        }
    }

    /**
     * Update actor rendering
     */
    @Inject(method = "tick", at = @At("HEAD"), remap = false)
    private void preTick(final CallbackInfo ci) {
        for (final BlockPos blockPos : ((ContraptionDuck) contraption).ci$getChangedActors()) {
            MutablePair<StructureTemplate.StructureBlockInfo, MovementContext> actor = contraption.getActorAt(blockPos);

            // Remove old instance, if one exists
            final ActorInstance oldActorInstance = ci$actorToInstaceMap.remove(blockPos);
            if (oldActorInstance != null) {
                ((ContraptionInstanceManagerDuck) ((ContraptionInstanceWorldAccessor) instanceWorld).getBlockEntityInstanceManager()).deleteActorInstance(oldActorInstance);
            }
            if (actor != null) {
                // Add new instance
                final ActorInstance actorInstance = ((ContraptionInstanceWorldAccessor) instanceWorld).getBlockEntityInstanceManager().createActor(actor);
                ci$actorToInstaceMap.put(actor.getLeft().pos, actorInstance);
            }
        }
        ((ContraptionDuck) contraption).ci$clearChangedActors();
    }

    @Inject(method = "buildActors", at = @At("HEAD"), cancellable = true, remap = false)
    private void preBuildActors(final CallbackInfo ci) {
        for (final MutablePair<StructureTemplate.StructureBlockInfo, MovementContext> actor : contraption.getActors()) {
            final ActorInstance actorInstance = ((ContraptionInstanceWorldAccessor) instanceWorld).getBlockEntityInstanceManager().createActor(actor);
            ci$actorToInstaceMap.put(actor.getLeft().pos, actorInstance);
        }
        ci.cancel();
    }
}
