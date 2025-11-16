package org.valkyrienskies.create_interactive

import com.simibubi.create.AllTags
import com.simibubi.create.foundation.data.BuilderTransformers
import com.simibubi.create.foundation.data.ModelGen
import com.simibubi.create.foundation.data.TagGen
import com.simibubi.create.infrastructure.fabric.SimpleBlockEntityVisualFactory
import com.tterrag.registrate.builders.BlockEntityBuilder
import com.tterrag.registrate.util.entry.BlockEntry
import com.tterrag.registrate.util.nullness.NonNullBiFunction
import com.tterrag.registrate.util.nullness.NonNullFunction
import dev.engine_room.flywheel.api.visual.BlockEntityVisual
import dev.engine_room.flywheel.api.visualization.VisualManager
import dev.engine_room.flywheel.api.visualization.VisualizationContext
import dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.material.MapColor
import org.valkyrienskies.create_interactive.content.buffer_stop.BufferStopBlock
import org.valkyrienskies.create_interactive.content.buffer_stop.BufferStopBlockEntity
import org.valkyrienskies.create_interactive.content.buffer_stop.BufferStopRenderer
import org.valkyrienskies.create_interactive.content.interact_me.InteractMeBlock
import org.valkyrienskies.create_interactive.content.interact_me.InteractMeBlockItem
import org.valkyrienskies.create_interactive.content.mechanical_propagator.*
import java.util.function.BiFunction
import java.util.function.Function

object GameContent {



    val CONNECTED = BooleanProperty.create("connected")

    @JvmField
    val MECHANICAL_PROPAGATOR_BEARING_BLOCK: BlockEntry<MechanicalPropagatorBearingBlock> =
        CreateInteractiveMod.REGISTRATE.block<MechanicalPropagatorBearingBlock>(
            "propagator_bearing"
        ) { properties: BlockBehaviour.Properties? ->
            MechanicalPropagatorBearingBlock(
                properties!!
            )
        }
            .transform(TagGen.axeOrPickaxe())
            .properties { p: BlockBehaviour.Properties ->
                p.mapColor(
                    MapColor.PODZOL
                )
            }
            .transform(BuilderTransformers.bearing("mechanical", "gearbox"))
            .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
            .register()


    @JvmField
    val DISJOINTED_PROPAGATOR_BEARING_BLOCK: BlockEntry<DisjointedPropagatorBearingBlock> =
        CreateInteractiveMod.REGISTRATE.block<DisjointedPropagatorBearingBlock>(
            "disjointed_propagator_bearing"
        ) { properties: BlockBehaviour.Properties? ->
            DisjointedPropagatorBearingBlock(
                properties!!
            )
        }
            .transform(TagGen.axeOrPickaxe())
            .properties { p: BlockBehaviour.Properties ->
                p.mapColor(
                    MapColor.PODZOL
                )
            }
            .transform(BuilderTransformers.bearing("mechanical", "gearbox"))
            .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
            .register()



    val MECHANICAL_PROPAGATOR_BEARING_BE = CreateInteractiveMod.REGISTRATE
        .blockEntity("propagator_bearing",
            BlockEntityBuilder.BlockEntityFactory<MechanicalPropagatorBearingBlockEntity> { type, pos, state ->
                MechanicalPropagatorBearingBlockEntity(
                    type,
                    pos,
                    state
                )
            })
//        .visual {
//            SimpleBlockEntityVisualFactory {
//                ctx, be, pt -> MechPropBearingInstance<MechanicalPropagatorBearingBlockEntity>(ctx, be, pt)
//            }
//        }
        .validBlocks(MECHANICAL_PROPAGATOR_BEARING_BLOCK)
        .renderer {
            NonNullFunction<BlockEntityRendererProvider.Context, BlockEntityRenderer<in MechanicalPropagatorBearingBlockEntity>> { context: BlockEntityRendererProvider.Context ->
                MechanicalPropagatorBearingRenderer(
                    context
                )
            }
        }
        .register()


    val DISJOINTED_PROPAGATOR_BEARING_BE = CreateInteractiveMod.REGISTRATE
        .blockEntity("disjointed_propagator_bearing",
            BlockEntityBuilder.BlockEntityFactory<DisjointedPropagatorBearingBlockEntity> { type, pos, state ->
                DisjointedPropagatorBearingBlockEntity(
                    type,
                    pos,
                    state
                )
            })
        .validBlocks({ DISJOINTED_PROPAGATOR_BEARING_BLOCK.get() })
        .renderer {
            NonNullFunction<BlockEntityRendererProvider.Context, BlockEntityRenderer<in DisjointedPropagatorBearingBlockEntity>> { context: BlockEntityRendererProvider.Context ->
                DisjointedPropagatorBearingRenderer(
                    context
                )
            }
        }
        .register()


    val BUFFER_STOP_BLOCK: BlockEntry<BufferStopBlock> = CreateInteractiveMod.REGISTRATE.block<BufferStopBlock>(
        "buffer_stop"
    ) { properties: BlockBehaviour.Properties? ->
        BufferStopBlock(
            properties!!
        )
    }
        .transform(TagGen.axeOrPickaxe())
        .properties { p: BlockBehaviour.Properties ->
            p.mapColor(
                MapColor.PODZOL
            )
            p.noOcclusion()
        }
        .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
        .item()
        .transform(ModelGen.customItemModel())
        .register()

    val BUFFER_STOP_BE = CreateInteractiveMod.REGISTRATE
        .blockEntity("buffer_stop",
            BlockEntityBuilder.BlockEntityFactory<BufferStopBlockEntity> { type, pos, state ->
                BufferStopBlockEntity(
                    type,
                    pos,
                    state
                )
            })
        .validBlocks({ BUFFER_STOP_BLOCK.get() })
        .renderer {
            NonNullFunction<BlockEntityRendererProvider.Context, BlockEntityRenderer<in BufferStopBlockEntity>> { context: BlockEntityRendererProvider.Context ->
                BufferStopRenderer(context)
            }
        }
        .register()

    @JvmField
    val INTERACT_ME: BlockEntry<InteractMeBlock> = CreateInteractiveMod.REGISTRATE.block<InteractMeBlock>(
        "interact_me"
    ) { properties: BlockBehaviour.Properties? ->
        InteractMeBlock(
            properties!!
        )
    }
        .properties { p: BlockBehaviour.Properties ->
            p.mapColor(
                MapColor.PODZOL
            )
            p.noOcclusion()
        }
        .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
        .item(NonNullBiFunction<InteractMeBlock, Item.Properties, InteractMeBlockItem> { block: InteractMeBlock, properties: Item.Properties ->
            InteractMeBlockItem(block,
                properties)
        })
        .transform(ModelGen.customItemModel())
        .register()

    @JvmField
    val INTERACT_ME_NOT: BlockEntry<InteractMeBlock> = CreateInteractiveMod.REGISTRATE.block<InteractMeBlock>(
        "interact_me_not"
    ) { properties: BlockBehaviour.Properties? ->
        InteractMeBlock(
            properties!!
        )
    }
        .properties { p: BlockBehaviour.Properties ->
            p.mapColor(
                MapColor.PODZOL
            )
            p.noOcclusion()
        }
        .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
        .item(NonNullBiFunction<InteractMeBlock, Item.Properties, InteractMeBlockItem> { block: InteractMeBlock, properties: Item.Properties ->
            InteractMeBlockItem(block,
                properties)
        })
        .transform(ModelGen.customItemModel())
        .register()

    @JvmStatic
    fun init() {

    }
}
