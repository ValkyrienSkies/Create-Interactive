package org.valkyrienskies.create_interactive

import net.minecraft.core.Direction

val Direction.Axis.directions get() = when (this) {
    Direction.Axis.X -> listOf(Direction.EAST, Direction.WEST)
    Direction.Axis.Y -> listOf(Direction.UP, Direction.DOWN)
    Direction.Axis.Z -> listOf(Direction.NORTH, Direction.SOUTH)
}