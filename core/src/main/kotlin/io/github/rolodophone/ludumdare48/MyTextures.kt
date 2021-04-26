package io.github.rolodophone.ludumdare48

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.utils.Disposable

@Suppress("unused","PropertyName")
class MyTextures: Disposable {
	private val graphicsAtlas = TextureAtlas(Gdx.files.internal("graphics/graphics.atlas"))

	val background = graphicsAtlas.findRegion("background")!!
	val block_background = graphicsAtlas.findRegion("block_background")!!
	val block_dirt = List(2) { graphicsAtlas.findRegion("block_dirt$it")!! }
	val block_pipe_end = graphicsAtlas.findRegion("block_pipe_end")!!
	val block_pipe_middle = graphicsAtlas.findRegion("block_pipe_middle")!!
	val block_stone_black = graphicsAtlas.findRegion("block_stone_black")!!
	val block_stone_sand = graphicsAtlas.findRegion("block_stone_sand")!!
	val block_stone = graphicsAtlas.findRegion("block_stone")!!
	val dialog_box = graphicsAtlas.findRegion("dialog_box")!!
	val dog_dig_down = List(4) { graphicsAtlas.findRegion("dog_dig_down$it")!! }
	val dog_dig_left = List(6) { graphicsAtlas.findRegion("dog_dig_left$it")!! }
	val dog_dig_right = List(6) { graphicsAtlas.findRegion("dog_dig_right$it")!! }
	val dog_happy_apple_core = graphicsAtlas.findRegion("dog_happy_apple_core")!!
	val dog_happy_bone = graphicsAtlas.findRegion("dog_happy_bone")!!
	val dog_hurt_dynamite = graphicsAtlas.findRegion("dog_hurt_dynamite")!!
	val dog_hurt_plastic_bag = graphicsAtlas.findRegion("dog_hurt_plastic_bag")!!
	val dog_hurt_sewage = graphicsAtlas.findRegion("dog_hurt_sewage")!!
	val dog_hurt_tin_can = graphicsAtlas.findRegion("dog_hurt_tin_can")!!
	val dog_rest = List(2) { graphicsAtlas.findRegion("dog_rest$it")!! }
	val grid8 = graphicsAtlas.findRegion("grid8")!!
	val tile_highlight = List(2) { graphicsAtlas.findRegion("tile_highlight$it")!! }
	val dog_think = List(4) { graphicsAtlas.findRegion("dog_think$it")!! }
	val dog_hungry = List(3) { graphicsAtlas.findRegion("dog_hungry$it")!! }
	val dog_outro = List(9) { graphicsAtlas.findRegion("dog_outro$it")!! }
	val boot = graphicsAtlas.findRegion("boot")!!
	val dog_hurt_nail = graphicsAtlas.findRegion("dog_hurt_nail")!!
	val diamonds = graphicsAtlas.findRegion("diamonds")!!
	val root_middle = graphicsAtlas.findRegion("root_middle")!!
	val treasure = graphicsAtlas.findRegion("treasure")!!
	val fossil = graphicsAtlas.findRegion("fossil")!!
	val gravel = graphicsAtlas.findRegion("gravel")!!
	val gravel_dark = graphicsAtlas.findRegion("gravel_dark")!!

	override fun dispose() {
		graphicsAtlas.dispose()
	}
}