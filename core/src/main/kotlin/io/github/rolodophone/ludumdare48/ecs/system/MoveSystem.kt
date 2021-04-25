package io.github.rolodophone.ludumdare48.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.MathUtils
import io.github.rolodophone.ludumdare48.ecs.component.MoveComponent
import io.github.rolodophone.ludumdare48.ecs.component.TransformComponent
import io.github.rolodophone.ludumdare48.util.getNotNull
import ktx.ashley.allOf

private const val DELTA_TIME = 1/60f

/**
 * Controls smooth movement of entities.
 */
class MoveSystem: IteratingSystem(allOf(TransformComponent::class, MoveComponent::class).get()) {
	private var accumulator = 0f

	override fun update(deltaTime: Float) {
		accumulator += deltaTime

		//move each entity enough times so that it overshoots
		while (accumulator >= 0f) {
			accumulator -= DELTA_TIME
			super.update(DELTA_TIME)
		}

		// interpolate between the frames
		val alpha = (accumulator + DELTA_TIME) / DELTA_TIME

		for (entity in entities) {
			val transformComp = entity.getNotNull(TransformComponent.mapper)
			val moveComp = entity.getNotNull(MoveComponent.mapper)

			moveComp.interpolatedPosition.set(
				MathUtils.lerp(moveComp.prevPosition.x, transformComp.rect.x, alpha),
				MathUtils.lerp(moveComp.prevPosition.y, transformComp.rect.y, alpha)
			)
		}
	}

	override fun processEntity(entity: Entity, deltaTime: Float) {
		val moveComp = entity.getNotNull(MoveComponent.mapper)
		val transformComp = entity.getNotNull(TransformComponent.mapper)

		//save previous position
		moveComp.prevPosition.set(transformComp.rect.x, transformComp.rect.y)

		//move position according to velocity
		transformComp.rect.x += moveComp.velocity.x * deltaTime
		transformComp.rect.y += moveComp.velocity.y * deltaTime
	}
}