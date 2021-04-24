package io.github.rolodophone.ludumdare48

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.utils.Disposable

@Suppress("unused","PropertyName")
class MyTextures: Disposable {
	private val graphicsAtlas = TextureAtlas(Gdx.files.internal("graphics/graphics.atlas"))

	val grid8 = graphicsAtlas.findRegion("grid8")!!
	val block_dirt0 = graphicsAtlas.findRegion("block_dirt0")!!
	val block_dirt1 = graphicsAtlas.findRegion("block_dirt1")!!

	override fun dispose() {
		graphicsAtlas.dispose()
	}
}