package io.github.rolodophone.ludumdare48.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.utils.viewport.Viewport
import io.github.rolodophone.ludumdare48.ecs.component.TransformComponent
import io.github.rolodophone.ludumdare48.event.GameEvent
import io.github.rolodophone.ludumdare48.event.GameEventManager
import ktx.ashley.allOf

/**
 * Handles player input and triggers the appropriate [GameEvent]s.
 */
class PlayerInputSystem(
	private val gameViewport: Viewport,
	private val gameEventManager: GameEventManager
): IteratingSystem(
	allOf(TransformComponent::class).get()
) {
	override fun processEntity(entity: Entity, deltaTime: Float) {

	}
}