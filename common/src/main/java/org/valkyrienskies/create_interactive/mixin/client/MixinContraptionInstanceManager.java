package org.valkyrienskies.create_interactive.mixin.client;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
//import com.simibubi.create.content.contraptions.render.ActorInstance;
//import com.simibubi.create.content.contraptions.render.ContraptionInstanceManager;
import com.simibubi.create.content.contraptions.actors.ActorInstance;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ActorVisual;
import com.simibubi.create.content.contraptions.render.ContraptionVisual;
import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visual.Visual;
import dev.engine_room.flywheel.api.visualization.BlockEntityVisualizer;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.impl.visualization.VisualizationManagerImpl;
import dev.engine_room.flywheel.lib.visual.AbstractEntityVisual;
import net.minecraft.client.Camera;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.apache.commons.lang3.tuple.MutablePair;
import org.jetbrains.annotations.NotNull;
import dev.engine_room.flywheel.lib.visual.AbstractVisual;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.create_interactive.CreateInteractiveUtil;
import org.valkyrienskies.create_interactive.mixin_logic.client.MixinContraptionInstanceManagerLogic;
import org.valkyrienskies.create_interactive.mixinducks.ContraptionInstanceManagerDuck;

import java.util.ArrayList;
import java.util.List;

import static dev.engine_room.flywheel.impl.visualization.storage.Transaction.remove;

@Mixin(ContraptionVisual.class)
public abstract class MixinContraptionInstanceManager<T extends AbstractContraptionEntity> extends AbstractEntityVisual<T> implements ContraptionInstanceManagerDuck {

    public MixinContraptionInstanceManager(VisualizationContext ctx, T entity, float partialTick) {
        super(ctx, entity, partialTick);
    }
//    @Final
//    @Shadow
//    protected List<ActorVisual> actors;
//
//    @Unique
//    private boolean ci$hasRemovedBlockEntities = false;
//
//
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
