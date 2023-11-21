package org.valkyrienskies.create_interactive

import com.simibubi.create.content.contraptions.behaviour.MovementContext
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate
import org.apache.commons.lang3.tuple.MutablePair
import org.apache.commons.lang3.tuple.Pair

typealias CreateActor = MutablePair<StructureTemplate.StructureBlockInfo, MovementContext?>
typealias CreateActorImmutable = Pair<StructureTemplate.StructureBlockInfo, MovementContext?>
