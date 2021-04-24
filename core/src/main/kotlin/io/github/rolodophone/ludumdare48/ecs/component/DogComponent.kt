package io.github.rolodophone.ludumdare48.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

class DogComponent: Component, Pool.Poolable {
	companion object {
		val mapper = mapperFor<DogComponent>()
	}

	enum class State {
		RESTING, DIGGING, DIALOG
	}

	var state = State.RESTING
	var digDuration = 3f
	var diggingX = 0
	var diggingY = 0
	var diggableTiles = mutableListOf<Pair<Int, Int>>()

	override fun reset() {
		state = State.RESTING
		var digDuration = 3f
		var diggingX = 0
		var diggingY = 0
		diggableTiles.clear()
	}
}