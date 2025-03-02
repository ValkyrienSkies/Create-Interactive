package org.valkyrienskies.create_interactive.content.ponders.scenes

import com.simibubi.create.AllItems
import com.simibubi.create.foundation.ponder.PonderPalette
import com.simibubi.create.foundation.ponder.SceneBuilder
import com.simibubi.create.foundation.ponder.SceneBuildingUtil
import com.simibubi.create.foundation.ponder.element.InputWindowElement
import com.simibubi.create.foundation.utility.Pointing
import net.minecraft.core.Direction
import net.minecraft.world.level.block.Blocks

class PropagatorBearings {
    companion object {
        fun standardBearing(scene: SceneBuilder, util: SceneBuildingUtil) {
            scene.title("propagator_bearing", "Using Create: Interactive propagator bearing")
            scene.configureBasePlate(0, 0, 5)
            scene.setSceneOffsetY(-1.0f)
            /*scene.world.showSection(util.select.layer(0), Direction.UP)
            scene.idle(5)
            scene.world.showSection(util.select.layer(1), Direction.DOWN)
            scene.idle(10)
            scene.world.showSection(util.select.layer(2), Direction.DOWN)
            scene.idle(10)
            val cog1 = util.select.position(5, 0, 3)
            val cog2 = util.select.position(4, 1, 3)
            val cog3 = util.select.position(3, 1, 2)
            val cog4 = util.select.position(2, 1, 2)
            val all = cog1.copy().add(cog2).add(cog3).add(cog4)
            val bearingPos = util.grid.at(3, 2, 3)
            scene.overlay.showSelectionWithText(util.select.position(bearingPos.above()), 60)
                .colored(PonderPalette.GREEN).pointAt(util.vector.blockSurface(bearingPos, Direction.WEST))
                .placeNearTarget().attachKeyFrame().text("Mechanical Bearings attach to the block in front of them")
            scene.idle(50)
            val plank = scene.world.showIndependentSection(
                util.select.position(bearingPos.above().east().north()),
                Direction.DOWN
            )
            scene.world.moveSection(plank, util.vector.of(-1.0, 0.0, 1.0), 0)
            scene.idle(20)
            scene.world.setKineticSpeed(cog1, -8.0f)
            scene.world.setKineticSpeed(cog2, 8.0f)
            scene.world.setKineticSpeed(cog3, -16.0f)
            scene.world.setKineticSpeed(cog4, 16.0f)
            scene.effects.rotationSpeedIndicator(bearingPos.below())
            scene.world.rotateBearing(bearingPos, 360.0f, 74)
            scene.world.rotateSection(plank, 0.0, 360.0, 0.0, 74)
            scene.overlay.showText(80).pointAt(util.vector.topOf(bearingPos.above())).placeNearTarget().attachKeyFrame()
                .text("Upon receiving Rotational Force, it will assemble it into a Rotating Contraption")
            scene.idle(74)
            scene.world.setKineticSpeed(all, 0.0f)
            scene.idle(20)
            scene.world.hideIndependentSection(plank, Direction.UP)
            scene.idle(15)
            val plank2 = util.select.position(3, 3, 1)
            val contraption =
                scene.world.showIndependentSection(util.select.layersFrom(3).substract(plank2), Direction.DOWN)
            scene.world.replaceBlocks(
                util.select.fromTo(1, 4, 2, 3, 3, 2),
                Blocks.OAK_PLANKS.defaultBlockState(),
                false
            )
            scene.idle(10)
            scene.overlay.showOutline(
                PonderPalette.GREEN,
                "glue",
                util.select.position(1, 4, 2).add(util.select.fromTo(3, 3, 2, 1, 3, 2))
                    .add(util.select.position(3, 3, 1)),
                40
            )
            scene.overlay.showControls(
                (InputWindowElement(util.vector.centerOf(util.grid.at(3, 3, 2)), Pointing.RIGHT)).withItem(
                    AllItems.SUPER_GLUE.asStack()
                ), 40
            )
            scene.idle(10)
            scene.world.showSectionAndMerge(plank2, Direction.SOUTH, contraption)
            scene.idle(15)
            scene.effects.superGlue(util.grid.at(3, 3, 1), Direction.SOUTH, true)
            scene.overlay.showText(120).pointAt(util.vector.topOf(bearingPos.above())).placeNearTarget()
                .attachKeyFrame().sharedText("movement_anchors")
            scene.idle(25)
            scene.world.configureCenterOfRotation(contraption, util.vector.topOf(bearingPos))
            scene.world.setKineticSpeed(cog1, -8.0f)
            scene.world.setKineticSpeed(cog2, 8.0f)
            scene.world.setKineticSpeed(cog3, -16.0f)
            scene.world.setKineticSpeed(cog4, 16.0f)
            scene.effects.rotationSpeedIndicator(bearingPos.below())
            scene.world.rotateBearing(bearingPos, 720.0f, 148)
            scene.world.rotateSection(contraption, 0.0, 720.0, 0.0, 148)
            scene.idle(148)
            scene.world.setKineticSpeed(all, 0.0f)*/

            scene.markAsFinished()
        }
    }
}