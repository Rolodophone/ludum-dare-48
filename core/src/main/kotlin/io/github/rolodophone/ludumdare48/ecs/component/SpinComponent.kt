package io.github.rolodophone.ludumdare48.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

class SpinComponent: Component, Pool.Poolable {
	companion object {
		val mapper = mapperFor<SpinComponent>()
	}

	/**
	 * The spin of the ball in degrees per second anticlockwise
	 */
	var angularVelocity = 0f

	/**
	 * Amount the [angularVelocity] changes by each second, measured in degrees per second squared
	 */
	var angularAcceleration = 0f

	override fun reset() {
		angularVelocity = 0f
		angularAcceleration = 0f
	}
}