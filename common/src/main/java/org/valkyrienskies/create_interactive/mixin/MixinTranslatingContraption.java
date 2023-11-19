package org.valkyrienskies.create_interactive.mixin;

import com.simibubi.create.content.contraptions.TranslatingContraption;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Set;

@Mixin(TranslatingContraption.class)
public abstract class MixinTranslatingContraption extends MixinContraption {
    @Shadow
    protected Set<BlockPos> cachedColliders;

    @Override
    public void ci$setBlock(final BlockPos localPos, final StructureTemplate.StructureBlockInfo structureBlockInfo) {
        super.ci$setBlock(localPos, structureBlockInfo);
        if (cachedColliders != null && cachedColliders.contains(localPos)) {
            cachedColliders = null;
        }
    }
}
