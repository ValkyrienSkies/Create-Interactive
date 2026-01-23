package org.valkyrienskies.create_interactive.mixin.client;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.render.ContraptionVisual;
import dev.engine_room.flywheel.api.task.Plan;
import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.visual.AbstractEntityVisual;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.create_interactive.mixin_logic.client.MixinClientContraptionLogic;
import org.valkyrienskies.create_interactive.mixinducks.ContraptionDuck;
import org.valkyrienskies.create_interactive.mixinducks.ContraptionInstanceManagerDuck;

@Mixin(ContraptionVisual.class)
public abstract class MixinContraptionVisual<T extends AbstractContraptionEntity> extends AbstractEntityVisual<T> implements ContraptionInstanceManagerDuck {

    public MixinContraptionVisual(VisualizationContext ctx, T entity, float partialTick) {
        super(ctx, entity, partialTick);
    }

    @Inject(
            method = "setupVisualizer",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private <B extends BlockEntity> void wrapVisualizer(B be, float partialTicks, CallbackInfo ci){
        MixinClientContraptionLogic.INSTANCE.preSetupVisualizer$create_interactive(entity.getContraption(), ci);
    }

//    @Override
//    public void ci$deleteActorInstance(final ActorVisual actorInstance) {
//        MixinContraptionInstanceManagerLogic.INSTANCE.deleteActorInstance$create_interactive(actors, actorInstance);
//    }
//
//    @Inject(method = "beginFrame", at = @At("HEAD"), cancellable = true, remap = false)
//    public void beginFrame(DynamicVisual.Context context, CallbackInfo ci) {
//        Contraption contraption = ((AbstractContraptionEntity)this.entity).getContraption();
//        if (contraption == null) {
//            return;
//        }
//        // Only render block entities when the contraption doesn't have a ship
//        if (!CreateInteractiveUtil.INSTANCE.doesContraptionHaveShipLoaded(contraption)) {
//            return;
//        } else {
//            // Remove block entities from contraptions that have shadows
//            //todo: does this even actually fix it? I don't understand the equivalence to old code (if it even exists)
//            if (!ci$hasRemovedBlockEntities) {
//                for (final ActorVisual actor : actors) {
//                    if (!(((ActorVisualAccessor) actor).getContext().blockEntityData.isEmpty())) {
//                        actor.delete();
//                    }
//                }
//                ci$hasRemovedBlockEntities = true;
//            }
//        }
//    }
}
