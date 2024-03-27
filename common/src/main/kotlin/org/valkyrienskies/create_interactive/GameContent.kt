package org.valkyrienskies.create_interactive

import com.jozufozu.flywheel.api.MaterialManager
import com.jozufozu.flywheel.backend.instancing.blockentity.BlockEntityInstance
import com.simibubi.create.AllTags
import com.simibubi.create.foundation.data.BuilderTransformers
import com.simibubi.create.foundation.data.ModelGen
import com.simibubi.create.foundation.data.TagGen
import com.tterrag.registrate.builders.BlockEntityBuilder
import com.tterrag.registrate.util.entry.BlockEntry
import com.tterrag.registrate.util.nullness.NonNullBiFunction
import com.tterrag.registrate.util.nullness.NonNullFunction
import net.minecraft.Util
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.Registries
import net.minecraft.util.datafix.fixes.References
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.material.MapColor
import org.valkyrienskies.create_interactive.content.buffer_stop.BufferStopBlock
import org.valkyrienskies.create_interactive.content.buffer_stop.BufferStopBlockEntity
import org.valkyrienskies.create_interactive.content.buffer_stop.BufferStopRenderer
import org.valkyrienskies.create_interactive.content.interact_me.InteractMeBlock
import org.valkyrienskies.create_interactive.content.interact_me.InteractMeBlockItem
import org.valkyrienskies.create_interactive.content.mechanical_propagator.MechPropBearingInstance
import org.valkyrienskies.create_interactive.content.mechanical_propagator.MechanicalPropagatorBearingBlock
import org.valkyrienskies.create_interactive.content.mechanical_propagator.MechanicalPropagatorBearingBlockEntity
import org.valkyrienskies.create_interactive.content.mechanical_propagator.MechanicalPropagatorBearingRenderer
import org.valkyrienskies.create_interactive.content.propagator.PropagatorBlock
import org.valkyrienskies.create_interactive.content.propagator.PropagatorBlockEntity
import org.valkyrienskies.create_interactive.registry.DeferredRegister
import org.valkyrienskies.create_interactive.registry.RegistrySupplier
import java.util.function.BiFunction

object GameContent {
    private val ITEMS = DeferredRegister.create(CreateInteractiveMod.MOD_ID, Registries.ITEM)
    private val BLOCKS = DeferredRegister.create(CreateInteractiveMod.MOD_ID, Registries.BLOCK)
    private val BLOCK_ENTITIES = DeferredRegister.create(CreateInteractiveMod.MOD_ID, Registries.BLOCK_ENTITY_TYPE)

    fun init() {
        BLOCKS.applyAll()
        // Disable the creative tabs for the propagator
        // BLOCKS.forEach {
        //     ITEMS.register(it.name) { BlockItem(it.get(), Item.Properties().tab(AllCreativeModeTabs.BASE_CREATIVE_TAB)) }
        // }

        BLOCK_ENTITIES.applyAll()

        ITEMS.applyAll()
    }

    val CONNECTED = BooleanProperty.create("connected")

    val PROPAGATOR = BLOCKS.register("propagator") { PropagatorBlock }
    val PROPAGATOR_BE: RegistrySupplier<BlockEntityType<PropagatorBlockEntity>> =
        PROPAGATOR.hasBE { pos, state -> PropagatorBlockEntity(::PROPAGATOR_BE.get().get(), pos, state) }.byName("propagator")

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

    /*
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
                p.color(
                    MaterialColor.PODZOL
                )
            }
            .transform(BuilderTransformers.bearing("mechanical", "gearbox"))
            .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
            .register()

     */

    val MECHANICAL_PROPAGATOR_BEARING_BE = CreateInteractiveMod.REGISTRATE
        .blockEntity("propagator_bearing",
            BlockEntityBuilder.BlockEntityFactory<MechanicalPropagatorBearingBlockEntity> { type, pos, state ->
                MechanicalPropagatorBearingBlockEntity(
                    type,
                    pos,
                    state
                )
            })
        .instance {
            BiFunction<MaterialManager?, MechanicalPropagatorBearingBlockEntity?, BlockEntityInstance<in MechanicalPropagatorBearingBlockEntity?>> { materialManager: MaterialManager?, blockEntity: MechanicalPropagatorBearingBlockEntity? ->
                MechPropBearingInstance(
                    materialManager,
                    blockEntity
                )
            }
        }
        .validBlocks(MECHANICAL_PROPAGATOR_BEARING_BLOCK)
        .renderer {
            NonNullFunction<BlockEntityRendererProvider.Context, BlockEntityRenderer<in MechanicalPropagatorBearingBlockEntity>> { context: BlockEntityRendererProvider.Context ->
                MechanicalPropagatorBearingRenderer(
                    context
                )
            }
        }
        .register()

    /*
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
     */

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

    private fun <T : BlockEntity> RegistrySupplier<out Block>.hasBE(blockEntity: (BlockPos, BlockState) -> T) = Pair(setOf(this), blockEntity)
    private fun <T : BlockEntity> Pair<Set<RegistrySupplier<out Block>>, (BlockPos, BlockState) -> T>.byName(name: String): RegistrySupplier<BlockEntityType<T>> =
        BLOCK_ENTITIES.register(name) {
            val type = Util.fetchChoiceType(References.BLOCK_ENTITY, name)

            BlockEntityType.Builder.of(
                this.second,
                *this.first.map { it.get() }.toTypedArray()
            ).build(type)
        }
}
