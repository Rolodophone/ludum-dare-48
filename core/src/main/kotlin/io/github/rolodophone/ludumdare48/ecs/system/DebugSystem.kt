package io.github.rolodophone.ludumdare48.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.utils.viewport.Viewport
import io.github.rolodophone.ludumdare48.MyTextures
import io.github.rolodophone.ludumdare48.ecs.component.DogComponent
import io.github.rolodophone.ludumdare48.ecs.component.GraphicsComponent
import io.github.rolodophone.ludumdare48.ecs.component.TileComponent
import io.github.rolodophone.ludumdare48.ecs.component.TransformComponent
import io.github.rolodophone.ludumdare48.event.GameEvent
import io.github.rolodophone.ludumdare48.event.GameEventManager
import io.github.rolodophone.ludumdare48.util.getNotNull
import ktx.ashley.entity
import ktx.ashley.with

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
	private val dogTileComp = dog.getNotNull(TileComponent.mapper)

	private var debuggingEnabled = false

	private var grid: Entity? = null

	override fun update(deltaTime: Float) {
		//enable/disable debugging
		if (Gdx.input.isKeyJustPressed(Input.Keys.F3)) {
			if (!debuggingEnabled) {
				debuggingEnabled = true
				gameEventManager.trigger(GameEvent.ShowDialog.apply {
					message = listOf("Enabled debugging.")
					actionText = "Tap to continue."
					effect = { }
				})
			}
		}

		if (debuggingEnabled) {
			// fast digging
			if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
				dogComp.digDuration = 0.1f
			}

			//end game
			if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
				gameEventManager.trigger(GameEvent.GameCompleted)
			}

			//grid
			if (Gdx.input.isKeyJustPressed(Input.Keys.G)) {
				if (grid == null) {
					//add grid
					grid = engine.entity {
						with<TransformComponent> {
							setSizeFromTexture(textures.grid8)
							rect.setPosition(0f, 0f)
						}
						with<GraphicsComponent> {
							sprite.setRegion(textures.grid8)
						}
					}
				}
				else {
					//remove grid
					engine.removeEntity(grid)
					grid = null
				}
			}
		}
	}
}