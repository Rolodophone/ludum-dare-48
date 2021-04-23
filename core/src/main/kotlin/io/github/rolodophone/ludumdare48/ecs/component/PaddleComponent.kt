package io.github.rolodophone.ludumdare48.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

class PaddleComponent: Component, Pool.Poolable {
	companion object {
		val mapper = mapperFor<PaddleComponent>()
		const val Y = 20f
	}

	enum class State {
		WAITING_TO_FIRE, AIMING, DEFLECTING
	}

	var state = State.WAITING_TO_FIRE
	var firingAngle = MathUtils.random(20f, 160f)
	var velocity = 0f

	override fun reset() {
		state = State.WAITING_TO_FIRE
		firingAngle = MathUtils.random(20f, 160f)
		velocity = 0f
	}
}