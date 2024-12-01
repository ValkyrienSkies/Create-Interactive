package org.valkyrienskies.create_interactive.mixin.compat.cbc;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.valkyrienskies.create_interactive.mixin_logic.compat.cbc.MixinAbstractCannonDrillBlockEntityLogic;
import rbasamoyai.createbigcannons.crafting.boring.AbstractCannonDrillBlockEntity;

import java.util.Map;

@Pseudo
@Mixin(AbstractCannonDrillBlockEntity.class)
public class MixinAbstractCannonDrillBlockEntity {
    @Shadow(remap = false)
    protected AbstractContraptionEntity latheEntity;

    /**
     * Fix boring not updating on ships
     */
    @Redirect(method = "tryFinishingBoring", at = @At(value = "INVOKE", target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"), remap = false)
    private Object onModifyContraptionBlock(final Map<BlockPos, StructureTemplate.StructureBlockInfo> blocks, final Object boringOffset, final Object newInfo) {
        return MixinAbstractCannonDrillBlockEntityLogic.INSTANCE.onModifyContraptionBlock$create_interactive(latheEntity, blocks, boringOffset, newInfo);
    }
}
