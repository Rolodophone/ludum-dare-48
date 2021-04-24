package io.github.rolodophone.ludumdare48.ecs.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.utils.viewport.Viewport
import io.github.rolodophone.ludumdare48.MyTextures
import io.github.rolodophone.ludumdare48.ecs.component.GraphicsComponent
import io.github.rolodophone.ludumdare48.ecs.component.TransformComponent
import io.github.rolodophone.ludumdare48.event.GameEventManager
import ktx.ashley.entity
import ktx.ashley.with

/**
 * Controls debugging features. This System will probably be disabled in the release.
 */
@Suppress("unused")
class DebugSystem(
	private val gameEventManager: GameEventManager,
	private val gameViewport: Viewport,
	private val textures: MyTextures
): EntitySystem() {
	override fun addedToEngine(engine: Engine) {
		engine.entity {
			with<TransformComponent> {
				setSizeFromTexture(textures.grid8)
				rect.setPosition(0f, 0f)
			}
			with<GraphicsComponent> {
				sprite.setRegion(textures.grid8)
			}
		}
	}

	override fun update(deltaTime: Float) {

	}
}