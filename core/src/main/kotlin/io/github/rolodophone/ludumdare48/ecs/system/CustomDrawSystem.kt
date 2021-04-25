package io.github.rolodophone.ludumdare48.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import io.github.rolodophone.ludumdare48.ecs.component.CustomDrawComponent
import io.github.rolodophone.ludumdare48.util.getNotNull
import ktx.ashley.allOf

class CustomDrawSystem(private val shapeRenderer: ShapeRenderer): IteratingSystem(allOf(CustomDrawComponent::class).get()) {
	override fun processEntity(entity: Entity, deltaTime: Float) {
		val customDrawComponent = entity.getNotNull(CustomDrawComponent.mapper)
		customDrawComponent.draw(shapeRenderer)
	}
}
