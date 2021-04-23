package io.github.rolodophone.ludumdare48.ecs.system

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.utils.viewport.Viewport
import io.github.rolodophone.ludumdare48.event.GameEventManager

/**
 * Controls debugging features. This System will probably be disabled in the release.
 */
@Suppress("unused")
class DebugSystem(
	private val gameEventManager: GameEventManager,
	private val gameViewport: Viewport
): EntitySystem() {
	override fun update(deltaTime: Float) {

	}
}