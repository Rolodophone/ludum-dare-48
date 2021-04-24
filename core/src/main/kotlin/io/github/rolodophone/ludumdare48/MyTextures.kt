package io.github.rolodophone.ludumdare48

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.utils.Disposable

@Suppress("unused","PropertyName")
class MyTextures: Disposable {
	private val graphicsAtlas = TextureAtlas(Gdx.files.internal("graphics/graphics.atlas"))

	val grid8 = graphicsAtlas.findRegion("grid8")!!

	override fun dispose() {
		graphicsAtlas.dispose()
	}
}