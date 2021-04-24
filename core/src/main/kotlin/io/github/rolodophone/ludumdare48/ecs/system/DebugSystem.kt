package io.github.rolodophone.ludumdare48.ecs.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.utils.viewport.Viewport
import io.github.rolodophone.ludumdare48.MyTextures
import io.github.rolodophone.ludumdare48.ecs.component.DogComponent
import io.github.rolodophone.ludumdare48.event.GameEventManager
import io.github.rolodophone.ludumdare48.util.getNotNull

/**
 * Controls debugging features. This System will probably be disabled in the release.
 */
@Suppress("unused")
class DebugSystem(
	private val gameEventManager: GameEventManager,
	private val gameViewport: Viewport,
	private val textures: MyTextures,
	dog: Entity
): EntitySystem() {
	private val dogComp = dog.getNotNull(DogComponent.mapper)

	override fun addedToEngine(engine: Engine) {
		// grid
//		engine.entity {
//			with<TransformComponent> {
//				setSizeFromTexture(textures.grid8)
//				rect.setPosition(0f, 0f)
//			}
//			with<GraphicsComponent> {
//				sprite.setRegion(textures.grid8)
//			}
//		}
	}

	override fun update(deltaTime: Float) {
		// fast digging
		if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
			dogComp.digDuration = 0.1f
		}
	}
}