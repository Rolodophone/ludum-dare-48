package io.github.rolodophone.ludumdare48.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

class CustomDrawComponent: Component, Pool.Poolable {
	companion object {
		val mapper = mapperFor<CustomDrawComponent>()
	}

	var draw: (ShapeRenderer) -> Unit = {}

	override fun reset() {
		draw = {}
	}
}