package org.valkyrienskies.create_interactive.content.ponders.scenes

import com.simibubi.create.foundation.ponder.CreateSceneBuilder
import net.createmod.catnip.math.Pointing
import net.createmod.ponder.api.PonderPalette
import net.createmod.ponder.api.element.ElementLink
import net.createmod.ponder.api.element.WorldSectionElement
import net.createmod.ponder.api.scene.SceneBuilder
import net.createmod.ponder.api.scene.SceneBuildingUtil
import net.createmod.ponder.foundation.element.InputWindowElement
import net.createmod.ponder.foundation.instruction.FadeOutOfSceneInstruction
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.Rotation
import org.valkyrienskies.create_interactive.GameContent
import org.valkyrienskies.create_interactive.content.ponders.InteractivePonderRegistry.Companion.getNextLang
import org.valkyrienskies.create_interactive.content.ponders.InteractivePonderRegistry.Companion.getPonderLang

class InteractMe {
    companion object {
        // This ponders code is an absolute mess, and for that I'm sorry
        fun normalSticker(scene: SceneBuilder, util: SceneBuildingUtil) {
            val scene = CreateSceneBuilder(scene)
            val ponderName = "normal_sticker"
            var langCount = 1
            // "Using the Interact Me"
            scene.title(ponderName, getPonderLang("$ponderName.header").string)
            scene.configureBasePlate(0, 0, 5)
            scene.setSceneOffsetY(-1.0f)

            val shaft1 = util.select().position(2, 3, 2)
            val shaft2 = util.select().position(2, 3, 1)
            val shaft3 = util.select().position(2, 3, 0)
            val allShafts = shaft1.copy().add(shaft2).add(shaft3)

            val valveCog1 = util.select().position(1, 1, 2)
            val valveCog2 = util.select().position(0, 1, 2)
            val valveCog3 = util.select().position(2, 1, 2)

            val bearingPos = util.grid().at(2, 2, 2)

            scene.world().showSection(util.select().layer(0).substract(util.select().position(5, 0, 3)), Direction.DOWN)
            scene.idle(10)

            scene.world().showSection(valveCog1, Direction.DOWN)
            scene.world().showSection(valveCog2, Direction.DOWN)
            scene.world().showSection(valveCog3, Direction.DOWN)
            scene.idle(10)

            var valve = scene.world().showIndependentSection(util.select().position(0, 2, 2), Direction.DOWN)
            scene.world().showSection(util.select().layer(2).substract(util.select().position(0, 2, 2)), Direction.DOWN)
            scene.idle(10)


            //setCogsSpeed(scene, util, 24f)

            scene.overlay().showText(60)
                .pointAt(util.vector().blockSurface(bearingPos, Direction.WEST))
                .attachKeyFrame()
                //"Normal Create contraptions can't be interacted with"
                .text(getNextLang(ponderName, langCount++))

            scene.idle(60)

            scene.idle(10)

            var contraption =
                scene.world().showIndependentSection(util.select().layersFrom(3), Direction.DOWN)

            scene.idle(20)

            scene.overlay().showOutline(
                PonderPalette.GREEN,
                "glue",
                util.select().fromTo(2, 3, 1, 3, 4, 3),
                20
            )

            scene.idle(20+20)

            scene.world().configureCenterOfRotation(contraption, util.vector().topOf(bearingPos))

            scene.overlay().showControls(
                util.vector().blockSurface(BlockPos(0, 2, 2), Direction.UP),
                    Pointing.DOWN,
                5
            ).rightClick()

            setValveSpeed(scene, util, -32f)
            moveValve(scene,  util, -45.0, 5, valve)

            scene.effects().rotationSpeedIndicator(bearingPos.below())
            scene.world().rotateBearing(bearingPos, -45.0f, 5)
            scene.world().rotateSection(contraption, 0.0, -45.0, 0.0, 5)
            scene.idle(5)

            setValveSpeed(scene, util, 0f)

            scene.idle(20)

            scene.addKeyframe()

            var placePos = bearingPos.above().above().south()
            var placePosVisually = BlockPos(2, 4, 3)

            scene.overlay().showControls(
                util.vector().blockSurface(placePosVisually, Direction.UP),
                Pointing.DOWN,
                20
            ).withItem(Items.OAK_PLANKS.defaultInstance).rightClick();

            scene.idle(20+10)

            scene.overlay().showControls(
                util.vector().blockSurface(placePosVisually, Direction.UP),
                Pointing.DOWN,
                20
            ).withItem(Items.BARRIER.defaultInstance)

            scene.idle(20+10)

            scene.overlay().showText(60)
                .pointAt(util.vector().blockSurface(placePosVisually, Direction.UP))
                //"While assembled, their blocks can't be changed"
                .text(getNextLang(ponderName, langCount++))

            scene.idle(60+20)

            var craftingPos = bearingPos.above().above().north().east()

            scene.overlay().showControls(
                util.vector().blockSurface(craftingPos, Direction.UP),
                Pointing.DOWN,
                20
            ).rightClick()

            scene.idle(20+10)

            scene.overlay().showControls(
                util.vector().blockSurface(craftingPos, Direction.UP),
                Pointing.DOWN,
                20
            ).withItem(Items.BARRIER.defaultInstance)

            scene.idle(20+10)

            scene.overlay().showText(60)
                .pointAt(util.vector().blockSurface(craftingPos, Direction.UP))
                //"And can't be used"
                .text(getNextLang(ponderName, langCount++))

            scene.idle(60+20)

            scene.overlay().showControls(
                util.vector().blockSurface(BlockPos(0, 2, 2), Direction.UP),
                Pointing.DOWN,
                5
            ).rightClick()

            setValveSpeed(scene, util, 32f)
            moveValve(scene,  util, 45.0, 5, valve)

            scene.effects().rotationSpeedIndicator(bearingPos.below())
            scene.world().rotateBearing(bearingPos, 45.0f, 5)
            scene.world().rotateSection(contraption, 0.0, 45.0, 0.0, 5)
            scene.idle(5)

            setValveSpeed(scene, util, 0f)

            scene.idle(20)

            scene.addKeyframe()

            scene.idle(20)

            var stickerPos = BlockPos(1, 3, 2)

            scene.world().setBlock(stickerPos, GameContent.INTERACT_ME.defaultState.rotate(Rotation.COUNTERCLOCKWISE_90), true)
            scene.effects().superGlue(stickerPos, Direction.EAST, false)

            scene.idle(10)

            scene.overlay().showText(60)
                .pointAt(util.vector().centerOf(stickerPos))
                //"Using an Interact Me sticker, you can make contraptions interactive"
                .text(getNextLang(ponderName, langCount++))

            scene.idle(60+20)

            scene.overlay().showControls(
                util.vector().blockSurface(BlockPos(0, 2, 2), Direction.UP),
                Pointing.DOWN,
                5
            ).rightClick()

            setValveSpeed(scene, util, -32f)
            moveValve(scene,  util, -45.0, 5, valve)

            scene.addInstruction(FadeOutOfSceneInstruction(0, Direction.DOWN, contraption));
            contraption =
                scene.world().showIndependentSectionImmediately(util.select().layersFrom(3))

            scene.world().configureCenterOfRotation(contraption, util.vector().topOf(bearingPos))


            scene.effects().rotationSpeedIndicator(bearingPos.below())
            scene.world().rotateBearing(bearingPos, -45.0f, 5)
            scene.world().rotateSection(contraption, 0.0, -45.0, 0.0, 5)
            scene.idle(5)

            setValveSpeed(scene, util, 0f)

            scene.idle(20)

            scene.overlay().showControls(
                util.vector().blockSurface(placePosVisually, Direction.UP),
                Pointing.DOWN,
                20
            ).withItem(Items.OAK_PLANKS.defaultInstance).rightClick()

            scene.idle(20+10)

            scene.world().setBlock(placePos, Blocks.OAK_PLANKS.defaultBlockState(), true)


            scene.idle(20)

            scene.overlay().showText(60)
                .pointAt(util.vector().centerOf(placePosVisually))
                //"When interactive, blocks can be modified and used to their full extent"
                .text(getNextLang(ponderName, langCount++))

            scene.idle(60+20)

            scene.world().hideSection(valveCog1, Direction.UP)
            scene.world().hideSection(valveCog2, Direction.UP)
            scene.world().hideIndependentSection(valve, Direction.UP)
            scene.idle(10)

            scene.world().showSection(util.select().layer(1).substract(valveCog1).substract(valveCog2).substract(valveCog3), Direction.DOWN)
            scene.world().showSection(util.select().position(5, 0, 3), Direction.DOWN)
            scene.idle(20)

            scene.addKeyframe()

            scene.idle(20)

            setAutoSpeed(scene, util, 12f)

            scene.effects().rotationSpeedIndicator(bearingPos.below())
            scene.world().rotateBearing(bearingPos, 405.0f, 112)
            scene.world().rotateSection(contraption, 0.0, 405.0, 0.0, 112)

            scene.overlay().showText(112)
                .pointAt(util.vector().blockSurface(bearingPos, Direction.WEST))
                //"Even while moving"
                .text(getNextLang(ponderName, langCount++))

            scene.idle(112)

            setAutoSpeed(scene, util, 0f)

            scene.idle(20)

            scene.markAsFinished()
        }

        fun invertedSticker(scene: SceneBuilder, util: SceneBuildingUtil) {
            val scene = CreateSceneBuilder(scene)
            val ponderName = "inverted_sticker"
            var langCount = 1
            // "Using the Interact Me Not"
            scene.title(ponderName, getPonderLang("$ponderName.header").string)
            scene.configureBasePlate(0, 0, 5)
            scene.setSceneOffsetY(-1.0f)

            val shaft1 = util.select().position(2, 3, 2)
            val shaft2 = util.select().position(2, 3, 1)
            val shaft3 = util.select().position(2, 3, 0)
            val allShafts = shaft1.copy().add(shaft2).add(shaft3)

            val valveCog1 = util.select().position(1, 1, 2)
            val valveCog2 = util.select().position(0, 1, 2)
            val valveCog3 = util.select().position(2, 1, 2)

            val bearingPos = util.grid().at(2, 2, 2)

            scene.world().showSection(util.select().layer(0).substract(util.select().position(5, 0, 3)), Direction.DOWN)
            scene.idle(10)

            scene.world().showSection(valveCog1, Direction.DOWN)
            scene.world().showSection(valveCog2, Direction.DOWN)
            scene.world().showSection(valveCog3, Direction.DOWN)
            scene.idle(10)

            var valve = scene.world().showIndependentSection(util.select().position(0, 2, 2), Direction.DOWN)
            scene.world().showSection(util.select().layer(2).substract(util.select().position(0, 2, 2)), Direction.DOWN)
            scene.idle(10)

            var stickerPos = bearingPos.above().west()
            scene.world().setBlock(stickerPos, GameContent.INTERACT_ME_NOT.defaultState.rotate(Rotation.COUNTERCLOCKWISE_90), false)

            var contraption =
                scene.world().showIndependentSection(util.select().layersFrom(3), Direction.DOWN)

            scene.idle(20)

            scene.addKeyframe()

            scene.idle(10)

            scene.overlay().showText(90)
                .pointAt(util.vector().centerOf(stickerPos))
                //"The Interact-Me-Not isn't needed with the default config"
                .text(getNextLang(ponderName, langCount++))

            scene.idle(90+20)

            scene.overlay().showText(90)
                .pointAt(util.vector().centerOf(stickerPos))
                //"It's only needed if the config uses 'WITHOUT STICKER' handling"
                .text(getNextLang(ponderName, langCount++))

            scene.idle(90+20)

            scene.overlay().showText(90)
                .pointAt(util.vector().centerOf(stickerPos))
                .attachKeyFrame()
                //"'WITHOUT STICKER' handling means the contraption will always be interactive"
                .text(getNextLang(ponderName, langCount++))

            scene.idle(90+20)

            scene.overlay().showText(70)
                .pointAt(util.vector().centerOf(stickerPos))
                //"Unless an Interact-Me-Not is used"
                .text(getNextLang(ponderName, langCount++))

            scene.idle(90+10)

            scene.addKeyframe()

            scene.idle(10)

            scene.world().setBlock(stickerPos, GameContent.INTERACT_ME.defaultState.rotate(Rotation.COUNTERCLOCKWISE_90), true)
            scene.effects().superGlue(stickerPos, Direction.EAST, false)

            scene.idle(15)

            scene.overlay().showText(90)
                .pointAt(util.vector().centerOf(stickerPos))
                //"When handling is 'WITHOUT STICKER', the Interact-Me has no affect"
                .text(getNextLang(ponderName, langCount++))

            scene.idle(90+20)

            scene.markAsFinished()
        }

        // TODO: explain interact-me-not and briefly the config
        // TODO: oh and lang-ify properly
        // TODO: then to the backport-mobile

        private fun setAutoSpeed(scene: SceneBuilder, util: SceneBuildingUtil, speed: Float) {
            val scene = CreateSceneBuilder(scene)
            val cog1 = util.select().position(5, 0, 3)
            val cog2 = util.select().position(4, 1, 3)
            val cog3 = util.select().position(3, 1, 2)
            val cog4 = util.select().position(2, 1, 2)

            scene.world().setKineticSpeed(cog1, -(speed / 2))
            scene.world().setKineticSpeed(cog2, (speed / 2))
            scene.world().setKineticSpeed(cog3, -speed)
            scene.world().setKineticSpeed(cog4, speed)
        }

        private fun setValveSpeed(scene: SceneBuilder, util: SceneBuildingUtil, speed: Float) {
            val scene = CreateSceneBuilder(scene)
            val valveCog1 = util.select().position(1, 1, 2)
            val valveCog2 = util.select().position(0, 1, 2)
            val valveCog3 = util.select().position(2, 1, 2)

            scene.world().setKineticSpeed(valveCog1, speed)
            scene.world().setKineticSpeed(valveCog2, -speed)
            scene.world().setKineticSpeed(valveCog3, speed)
        }

        private fun moveValve(scene: SceneBuilder, util: SceneBuildingUtil, degrees: Double, duration: Int, valve: ElementLink<WorldSectionElement>) {
            scene.world().rotateSection(valve, 0.0, degrees, 0.0, duration)
        }
    }
}