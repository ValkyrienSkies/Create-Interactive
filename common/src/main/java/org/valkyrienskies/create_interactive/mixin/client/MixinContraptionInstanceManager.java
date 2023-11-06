package org.valkyrienskies.create_interactive.mixin.client;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.backend.instancing.TaskEngine;
import com.jozufozu.flywheel.backend.instancing.blockentity.BlockEntityInstanceManager;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.render.ActorInstance;
import com.simibubi.create.content.contraptions.render.ContraptionInstanceManager;
import net.minecraft.client.Camera;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.valkyrienskies.create_interactive.CreateInteractiveUtil;
import org.valkyrienskies.create_interactive.mixin_logic.client.MixinContraptionInstanceManagerLogic;
import org.valkyrienskies.create_interactive.mixinducks.ContraptionInstanceManagerDuck;

import java.util.ArrayList;

@Mixin(ContraptionInstanceManager.class)
public abstract class MixinContraptionInstanceManager extends BlockEntityInstanceManager implements ContraptionInstanceManagerDuck {
    @Shadow
    protected ArrayList<ActorInstance> actors;

    @Shadow
    private Contraption contraption;

    @Unique
    private boolean ci$hasRemovedBlockEntities = false;

    public MixinContraptionInstanceManager(MaterialManager materialManager) {
        super(materialManager);
    }

    @Override
    public void ci$deleteActorInstance(final ActorInstance actorInstance) {
        MixinContraptionInstanceManagerLogic.INSTANCE.deleteActorInstance$create_interactive(actors, actorInstance);
    }

    /**
     * @author Tri0de
     * @reason Don't render tile entities when the contraption has a shadow
     */
    @Overwrite
    public void beginFrame(final @NotNull TaskEngine taskEngine, final @NotNull Camera info) {
        // Only render block entities when the contraption doesn't have a ship
        if (!CreateInteractiveUtil.INSTANCE.doesContraptionHaveShipLoaded(contraption)) {
            super.beginFrame(taskEngine, info);
        } else {
            // Remove block entities from contraptions that have shadows
            if (!ci$hasRemovedBlockEntities) {
                for (final BlockEntity be : contraption.maybeInstancedBlockEntities) {
                    remove(be);
                }
                ci$hasRemovedBlockEntities = true;
            }
        }

        actors.forEach(ActorInstance::beginFrame);
    }
}
