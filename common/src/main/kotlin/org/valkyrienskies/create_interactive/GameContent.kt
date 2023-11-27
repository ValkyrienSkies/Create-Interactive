package org.valkyrienskies.create_interactive

import com.simibubi.create.AllCreativeModeTabs
import net.minecraft.Util
import net.minecraft.core.BlockPos
import net.minecraft.core.Registry
import net.minecraft.util.datafix.fixes.References
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BooleanProperty
import org.valkyrienskies.create_interactive.content.MechanicalPropagatorBearingBlock
import org.valkyrienskies.create_interactive.content.MechanicalPropagatorBearingBlockEntity
import org.valkyrienskies.create_interactive.content.PropagatorBlock
import org.valkyrienskies.create_interactive.content.PropagatorBlockEntity
import org.valkyrienskies.create_interactive.registry.DeferredRegister
import org.valkyrienskies.create_interactive.registry.RegistrySupplier

object GameContent {
    private val ITEMS = DeferredRegister.create(CreateInteractiveMod.MOD_ID, Registry.ITEM_REGISTRY)
    private val BLOCKS = DeferredRegister.create(CreateInteractiveMod.MOD_ID, Registry.BLOCK_REGISTRY)
    private val BLOCK_ENTITIES = DeferredRegister.create(CreateInteractiveMod.MOD_ID, Registry.BLOCK_ENTITY_TYPE_REGISTRY)

    fun init() {
        BLOCKS.applyAll()
        BLOCKS.forEach {
            ITEMS.register(it.name) { BlockItem(it.get(), Item.Properties().tab(AllCreativeModeTabs.BASE_CREATIVE_TAB)) }
        }

        BLOCK_ENTITIES.applyAll()

        ITEMS.applyAll()
    }

    val CONNECTED = BooleanProperty.create("connected")

    val PROPAGATOR = BLOCKS.register("propagator") { PropagatorBlock }
    val PROPAGATOR_BE: RegistrySupplier<BlockEntityType<PropagatorBlockEntity>> =
        PROPAGATOR.hasBE { pos, state -> PropagatorBlockEntity(::PROPAGATOR_BE.get().get(), pos, state) }.byName("propagator")
    val MECHANICAL_PROPAGATOR_BEARING_BLOCK = BLOCKS.register("mechanical_bearing_propagator") { MechanicalPropagatorBearingBlock }
    val MECHANICAL_PROPAGATOR_BEARING_BE: RegistrySupplier<BlockEntityType<MechanicalPropagatorBearingBlockEntity>> =
        MECHANICAL_PROPAGATOR_BEARING_BLOCK.hasBE { pos, state -> MechanicalPropagatorBearingBlockEntity(::MECHANICAL_PROPAGATOR_BEARING_BE.get().get(), pos, state) }.byName("mechanical_bearing_propagator")

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
