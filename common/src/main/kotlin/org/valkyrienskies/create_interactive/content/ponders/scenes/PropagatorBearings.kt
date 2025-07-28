package org.valkyrienskies.create_interactive.content.ponders.scenes

import com.simibubi.create.AllBlocks
import com.simibubi.create.content.kinetics.gauge.GaugeBlock
import com.simibubi.create.foundation.ponder.PonderPalette
import com.simibubi.create.foundation.ponder.SceneBuilder
import com.simibubi.create.foundation.ponder.SceneBuildingUtil
import com.simibubi.create.foundation.ponder.Selection
import com.simibubi.create.foundation.ponder.element.InputWindowElement
import com.simibubi.create.foundation.utility.Pointing
import com.sun.jna.platform.unix.solaris.LibKstat.KstatNamed.UNION.STR
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.Rotation
import net.minecraft.world.level.block.state.BlockState
import org.valkyrienskies.create_interactive.CreateInteractiveMod
import org.valkyrienskies.create_interactive.GameContent
import org.valkyrienskies.create_interactive.content.ponders.InteractivePonderRegistry.Companion.getNextLang
import org.valkyrienskies.create_interactive.content.ponders.InteractivePonderRegistry.Companion.getPonderLang
import org.valkyrienskies.create_interactive.content.ponders.InteractivePonderRegistry.Companion.selection

class PropagatorBearings {
    companion object {
        fun standardBearing(scene: SceneBuilder, util: SceneBuildingUtil) {
            val ponderName = "propagator_bearing"
            var langCount = 1
            // "Using Create: Interactive Propagator bearing"
            scene.title(ponderName, getPonderLang("$ponderName.header").string)
            scene.configureBasePlate(0, 0, 5)
            scene.setSceneOffsetY(-1.0f)

            scene.world.showSection(util.select.layer(0), Direction.UP)
            scene.idle(5)

            scene.world.showSection(util.select.layer(1), Direction.DOWN)
            scene.idle(10)

            scene.world.showSection(util.select.layer(2), Direction.DOWN)
            scene.idle(10)

            val shaft1 = util.select.position(2, 3, 2)
            val shaft2 = util.select.position(2, 3, 1)
            val shaft3 = util.select.position(2, 3, 0)
            val allShafts = shaft1.copy().add(shaft2).add(shaft3)

            val bearingPos = util.grid.at(2, 2, 2)
            val stickerPos = util.grid.at(1, 3, 2)
            val crusherPos = util.grid.at(3, 3, 0)
            val aboveBearing = util.grid.at(2, 3, 2)

            scene.world.setBlock(aboveBearing, Blocks.OAK_PLANKS.defaultBlockState(), false)

            scene.overlay.showText(60)
                .pointAt(util.vector.blockSurface(bearingPos, Direction.WEST))
                .attachKeyFrame()
                //"Propagator bearings act like normal bearings"
                .text(getNextLang(ponderName, langCount++))


            scene.idle(60+10)

            var contraption =
                scene.world.showIndependentSection(util.select.layersFrom(3), Direction.DOWN)

            scene.idle(20)

            scene.overlay.showOutline(
                PonderPalette.GREEN,
                "glue",
                util.select.fromTo(1, 3, 0, 3, 4, 2),
                20
            )

            scene.idle(20+20)

            scene.world.configureCenterOfRotation(contraption, util.vector.topOf(bearingPos))

            setCogsSpeed(scene, util, 24f)

            scene.effects.rotationSpeedIndicator(bearingPos.below())
            scene.world.rotateBearing(bearingPos, 360.0f, 50)
            scene.world.rotateSection(contraption, 0.0, 360.0, 0.0, 50)
            scene.idle(50)

            setCogsSpeed(scene, util, 0f)

            scene.idle(20)

            scene.addKeyframe()

            scene.idle(10)

            scene.overlay.showControls(
                InputWindowElement(util.vector.blockSurface(stickerPos, Direction.WEST), Pointing.LEFT)
                    .rightClick()
                    .withItem(GameContent.INTERACT_ME.asStack()),
                15
            )

            scene.idle(15+5)

            scene.world.setBlock(stickerPos, GameContent.INTERACT_ME.defaultState.rotate(Rotation.COUNTERCLOCKWISE_90), true)
            scene.effects.superGlue(stickerPos, Direction.EAST, false)

            scene.idle(15)

            scene.overlay.showText(60)
                .pointAt(util.vector.centerOf(stickerPos))
                //"They still need an interact-me (with the default config)"
                .text(getNextLang(ponderName, langCount++))


            scene.idle(60+20)

            // For the particles
            scene.world.setBlock(aboveBearing, AllBlocks.GEARBOX.defaultState, true)
            // So that its vertical
            scene.world.restoreBlocks(aboveBearing.selection(util))


            scene.idle(20)

            scene.overlay.showText(60)
                .pointAt(util.vector.blockSurface(bearingPos.above(), Direction.UP))
                .attachKeyFrame()
                //"However, unlike normal bearings, they can provide a source of rotation"
                .text(getNextLang(ponderName, langCount++))


            scene.idle(60+30)

            setCogsSpeed(scene, util, 12f)

            scene.world.setKineticSpeed(allShafts, 12f)
            scene.world.setKineticSpeed(crusherPos.selection(util), 12f)

            scene.effects.rotationSpeedIndicator(bearingPos.below())
            scene.world.rotateBearing(bearingPos, 360.0f, 100)
            scene.world.rotateSection(contraption, 0.0, 360.0, 0.0, 100)
            scene.idle(100)

            setCogsSpeed(scene, util, 0f)
            scene.world.setKineticSpeed(allShafts, 0.0f)
            scene.world.setKineticSpeed(crusherPos.selection(util), 0.0f)

            scene.idle(30)

            scene.world.hideIndependentSection(contraption, Direction.UP)

            // Hide the air early
            val speed1 = BlockPos(3, 2, 2)
            scene.world.hideSection(speed1.selection(util), Direction.UP)

            scene.idle(20)

            scene.world.setBlock(speed1, getUprightSpeedometer(), false)
            scene.world.setBlock(aboveBearing, getUprightSpeedometer(), false)
            scene.world.showSection(speed1.selection(util), Direction.DOWN)
            scene.world.showSection(aboveBearing.selection(util), Direction.DOWN)

            scene.idle(20)

            scene.overlay.showText(60)
                .pointAt(util.vector.centerOf(aboveBearing))
                .attachKeyFrame()
                //"They provide the same rpm at which the bearing is spinning"
                .text(getNextLang(ponderName, langCount++))

            scene.idle(60+30)

            contraption =
                scene.world.makeSectionIndependent(aboveBearing.selection(util))

            setCogsSpeed(scene, util, 12f)

            scene.world.setKineticSpeed(allShafts, 12f)
            scene.world.setKineticSpeed(speed1.selection(util), 12f)

            scene.effects.rotationSpeedIndicator(bearingPos.below())
            scene.world.rotateBearing(bearingPos, 360.0f, 100)
            scene.world.rotateSection(contraption, 0.0, 360.0, 0.0, 100)

            scene.overlay.showText(100)
                .pointAt(util.vector.centerOf(speed1))
                .placeNearTarget()
                //"12 rpm"
                .text(getNextLang(ponderName, langCount++))
                .colored(PonderPalette.GREEN)

            scene.overlay.showText(100)
                .pointAt(util.vector.centerOf(aboveBearing))
                //"12 rpm"
                .text(getNextLang(ponderName, langCount++))
                .colored(PonderPalette.GREEN)

            scene.idle(100)

            setCogsSpeed(scene, util, 0f)
            scene.world.setKineticSpeed(allShafts, 0.0f)
            scene.world.setKineticSpeed(speed1.selection(util), 0f)

            scene.idle(10)

            scene.markAsFinished()
        }

        fun disjointedBearing(scene: SceneBuilder, util: SceneBuildingUtil) {
            val ponderName = "disjointed_bearing"
            var langCount = 1
            // "Using Create: Interactive Disjointed Propagator bearing"
            scene.title(ponderName, getPonderLang("$ponderName.header").string)
            scene.configureBasePlate(0, 0, 5)
            scene.setSceneOffsetY(-1.0f)

            scene.world.showSection(util.select.layer(0), Direction.UP)
            scene.idle(5)

            scene.world.showSection(util.select.layer(1), Direction.DOWN)
            scene.idle(5)

            scene.world.showSection(util.select.layer(2), Direction.DOWN)
            scene.idle(5)

            var contraption =
                scene.world.showIndependentSection(util.select.layersFrom(3), Direction.DOWN)
            scene.idle(10)

            val bearingPos = util.grid.at(2, 2, 2)

            scene.world.configureCenterOfRotation(contraption, util.vector.topOf(bearingPos))


            scene.overlay.showText(60)
                .pointAt(util.vector.centerOf(bearingPos))
                //"Disjointed bearings have 2 rotational inputs"
                .text(getNextLang(ponderName, langCount++))

            scene.idle(60+10)

            scene.overlay.showText(70)
                .pointAt(util.vector.centerOf(bearingPos.below()))
                .attachKeyFrame()
                //"The bottom controls the bearing rotation"
                .text(getNextLang(ponderName, langCount++))

            scene.idle(70+10)


            setCogsSpeed(scene, util, 24f)

            scene.effects.rotationSpeedIndicator(bearingPos.below())
            scene.world.rotateBearing(bearingPos, 360.0f, 50)
            scene.world.rotateSection(contraption, 0.0, 360.0, 0.0, 50)
            scene.idle(50)

            setCogsSpeed(scene, util, 0f)

            scene.idle(30)

            scene.overlay.showText(80)
                .pointAt(util.vector.blockSurface(bearingPos, Direction.WEST))
                .attachKeyFrame()
                //"The side controls the rotation on the contraption"
                .text(getNextLang(ponderName, langCount++))


            scene.idle(80+10)

            val speed1 = bearingPos.below()
            val speed2 = BlockPos(1, 1, 3)
            val speed3 = BlockPos(2, 4, 2)

            scene.world.setBlock(speed1, getUprightSpeedometer(), true)
            scene.idle(5)
            scene.world.setBlock(speed2, getUprightSpeedometer(), true)
            scene.idle(5)
            scene.world.setBlock(speed3, getUprightSpeedometer(), true)
            scene.idle(5)


            scene.idle(20)

            scene.addKeyframe()

            setCogsSpeed(scene, util, 12f)

            val shafts = util.select.fromTo(1, 0, 3, 1, 2, 3)
            val cog2 = util.select.position(2, 2, 3)
            val contraptionShafts = util.select.fromTo(2, 3, 0, 3, 4, 2)

            scene.world.setKineticSpeed(shafts, 32f)
            scene.world.setKineticSpeed(cog2, -32f)
            scene.world.setKineticSpeed(contraptionShafts, 32f)

            scene.world.setKineticSpeed(bearingPos.selection(util), 32f)

            scene.effects.rotationSpeedIndicator(bearingPos.below())
            scene.effects.rotationSpeedIndicator(BlockPos(1, 1, 3))

            scene.world.rotateBearing(bearingPos, 720.0f, 200)
            scene.world.rotateSection(contraption, 0.0, 720.0, 0.0, 200)

            scene.idle(10)

            scene.overlay.showText(190)
                .pointAt(util.vector.centerOf(speed1))
                .placeNearTarget()
                //"12 rpm"
                .text(getNextLang(ponderName, langCount++))
                .colored(PonderPalette.GREEN)

            scene.overlay.showText(190)
                .pointAt(util.vector.centerOf(speed2))
                //"32 rpm"
                .text(getNextLang(ponderName, langCount++))
                .colored(PonderPalette.BLUE)

            scene.overlay.showText(190)
                .pointAt(util.vector.centerOf(speed3))
                //"32 rpm"
                .text(getNextLang(ponderName, langCount++))
                .colored(PonderPalette.BLUE)

            scene.idle(190)

            setCogsSpeed(scene, util, 0f)

            scene.world.setKineticSpeed(shafts, 0f)
            scene.world.setKineticSpeed(cog2, 0f)
            scene.world.setKineticSpeed(contraptionShafts, 0f)

            scene.world.setKineticSpeed(bearingPos.selection(util), 0f)

            scene.markAsFinished()
        }

        fun getUprightSpeedometer(): BlockState {
            // I hate whoever made the speedometer like this
            return AllBlocks.SPEEDOMETER.defaultState.setValue(GaugeBlock.FACING, Direction.WEST).rotate(Rotation.CLOCKWISE_90)
        }

        private fun setCogsSpeed(scene: SceneBuilder, util: SceneBuildingUtil, speed: Float) {
            // Normal bearing
            val cog1 = util.select.position(5, 0, 3)
            val cog2 = util.select.position(4, 1, 3)
            val cog3 = util.select.position(3, 1, 2)

            // Both
            val cog4 = util.select.position(2, 1, 2)

            // Disjointed
            val shaft = util.select.position(2, 0, 2)

            scene.world.setKineticSpeed(cog1, -(speed / 2))
            scene.world.setKineticSpeed(cog2, (speed / 2))
            scene.world.setKineticSpeed(cog3, -speed)
            scene.world.setKineticSpeed(cog4, speed)
            scene.world.setKineticSpeed(shaft, speed)
        }
    }
}