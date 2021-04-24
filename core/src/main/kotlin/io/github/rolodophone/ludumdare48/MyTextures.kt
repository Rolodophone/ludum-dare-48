package io.github.rolodophone.ludumdare48

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.utils.Disposable

@Suppress("unused","PropertyName")
class MyTextures: Disposable {
	private val graphicsAtlas = TextureAtlas(Gdx.files.internal("graphics/graphics.atlas"))

	val grid8 = graphicsAtlas.findRegion("grid8")!!
	val block_dirt = List(2) { graphicsAtlas.findRegion("block_dirt$it") }
	val dog_dig_down = List(4) { graphicsAtlas.findRegion("dog_dig_down$it") }
	val dog_dig_left = List(6) { graphicsAtlas.findRegion("dog_dig_left$it") }
	val dog_dig_right = List(6) { graphicsAtlas.findRegion("dog_dig_right$it") }
	val dog_rest = List(2) { graphicsAtlas.findRegion("dog_rest$it") }

	override fun dispose() {
		graphicsAtlas.dispose()
	}
}