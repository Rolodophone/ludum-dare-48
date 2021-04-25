package io.github.rolodophone.ludumdare48.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

class TextComponent: Component, Pool.Poolable {
	companion object {
		val mapper = mapperFor<TextComponent>()
	}

	var draw: (BitmapFont, SpriteBatch) -> Unit = { _, _ -> }

	override fun reset() {
		draw = { _, _ -> }
	}
}