package io.github.rolodophone.ludumdare48.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import io.github.rolodophone.ludumdare48.ecs.component.AnimationComponent
import io.github.rolodophone.ludumdare48.ecs.component.GraphicsComponent
import io.github.rolodophone.ludumdare48.util.getNotNull
import ktx.ashley.allOf

class AnimationSystem: IteratingSystem(allOf(AnimationComponent::class, GraphicsComponent::class).get()) {
	override fun processEntity(entity: Entity, deltaTime: Float) {
		val animationComp = entity.getNotNull(AnimationComponent.mapper)

		animationComp.timeSinceLastFrame += deltaTime

		if (animationComp.timeSinceLastFrame > animationComp.frameDuration) {
			val graphicsComponent = entity.getNotNull(GraphicsComponent.mapper)
			animationComp.advanceFrame(graphicsComponent)

			animationComp.timeSinceLastFrame -= animationComp.frameDuration
		}
	}
}