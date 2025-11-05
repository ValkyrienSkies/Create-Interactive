package org.valkyrienskies.create_interactive.mixin.deployer;

import com.simibubi.create.content.kinetics.deployer.DeployerBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollOptionBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.create_interactive.mixin_logic.deployer.DeployerActorMode;
import org.valkyrienskies.create_interactive.mixin_logic.deployer.DeployerActorValueboxTransform;
import org.valkyrienskies.create_interactive.mixinducks.DeployerDuck;

import java.util.List;

@Mixin(DeployerBlockEntity.class)
public class MixinDeployerBlockEntity implements DeployerDuck {

    @Unique
    protected ScrollOptionBehaviour<DeployerActorMode> actorBehaviour;

    @Inject(method = "addBehaviours", at = @At("RETURN"), remap = false)
    private void onAddBehaviours(List<BlockEntityBehaviour> behaviours, final CallbackInfo ci) {
        actorBehaviour = new ScrollOptionBehaviour<>(
                DeployerActorMode.class,
                CreateLang.text("Deployer Actor").component(),
                ((DeployerBlockEntity) (Object) this),
                new DeployerActorValueboxTransform()
        );
        behaviours.add(actorBehaviour);
    }


    @Override
    public DeployerActorMode ci$getActorMode() {
        return actorBehaviour.get();
    }
}
