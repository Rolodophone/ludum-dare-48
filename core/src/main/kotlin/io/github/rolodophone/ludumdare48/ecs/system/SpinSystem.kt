package io.github.rolodophone.ludumdare48.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import io.github.rolodophone.ludumdare48.ecs.component.MoveComponent
import io.github.rolodophone.ludumdare48.ecs.component.SpinComponent
import io.github.rolodophone.ludumdare48.util.getNotNull
import ktx.ashley.allOf

class SpinSystem: IteratingSystem(allOf(SpinComponent::class, MoveComponent::class).get()) {
	override fun processEntity(entity: Entity, deltaTime: Float) {
		val moveComp = entity.getNotNull(MoveComponent.mapper)
		val spinComp = entity.getNotNull(SpinComponent.mapper)

		if (spinComp.angularVelocity != 0f) {
			println("Angular velocity: ${spinComp.angularVelocity}")
		}

		spinComp.angularVelocity += spinComp.angularAcceleration * deltaTime
		if (
			spinComp.angularAcceleration > 0 && spinComp.angularVelocity > 0 ||
			spinComp.angularAcceleration < 0 && spinComp.angularVelocity < 0
		) {
			spinComp.angularAcceleration = 0f
			spinComp.angularVelocity = 0f
		}

		moveComp.velocity.rotateDeg(spinComp.angularVelocity * deltaTime)
	}
}