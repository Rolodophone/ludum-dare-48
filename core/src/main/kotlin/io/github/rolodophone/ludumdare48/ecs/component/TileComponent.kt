package io.github.rolodophone.ludumdare48.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

class TileComponent: Component, Pool.Poolable {
	companion object {
		val mapper = mapperFor<TileComponent>()
	}

	enum class Type {
		DIGGABLE, OBSTACLE, BACKGROUND, ITEM, OTHER
	}

	var xIndex = 0
	var yIndex = 0
	var type = Type.OTHER

	override fun reset() {
		xIndex = 0
		yIndex = 0
	}
}