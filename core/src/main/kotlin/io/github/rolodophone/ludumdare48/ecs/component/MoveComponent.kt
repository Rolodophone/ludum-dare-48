package io.github.rolodophone.ludumdare48.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

class MoveComponent: Component, Pool.Poolable {
	companion object {
		val mapper = mapperFor<MoveComponent>()
	}

	/**
	 * The current velocity vector of the entity, in viewport units per second
	 */
	val velocity = Vector2()

	/**
	 * The previous position of [TransformComponent.rect].
	 *
	 * This is used to interpolate between frames.
	 */
	val prevPosition = Vector2()

	/**
	 * The interpolated position of the entity.
	 *
	 * This will be somewhere between [MoveComponent.prevPosition] and [TransformComponent.rect].position.
	 *
	 * The [RenderSystem][io.github.rolodophone.ludumdare48.ecs.system.RenderSystem] will use this as the position of the sprite if the
	 * entity has a [MoveComponent]. Otherwise it will use [TransformComponent.rect]
	 */
	val interpolatedPosition = Vector2()

	override fun reset() {
		velocity.setZero()
		prevPosition.setZero()
		interpolatedPosition.setZero()
	}
}