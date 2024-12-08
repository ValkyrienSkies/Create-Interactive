package org.valkyrienskies.create_interactive.content.interact_me

import com.simibubi.create.content.contraptions.glue.SuperGlueEntity
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.block.Block

class InteractMeBlockItem(block: Block, properties: Properties) : BlockItem(block, properties) {

    /*
    override fun place(ctx: BlockPlaceContext): InteractionResult {
        val world = ctx.level
        val gluePos = ctx.clickedPos
        val face = ctx.clickedFace
        val placer = ctx.player
        val itemStack = ctx.itemInHand

        val entity = SuperGlueEntity(world, SuperGlueEntity.span(gluePos, gluePos.relative(face.opposite)))
        val compoundnbt: CompoundTag? = itemStack.tag
        if (compoundnbt != null) EntityType.updateCustomEntityTag(world, placer, entity, compoundnbt)

        if (!world.isClientSide) {
            world.addFreshEntity(entity)
        }

        return super.place(ctx)
    }
    */
}