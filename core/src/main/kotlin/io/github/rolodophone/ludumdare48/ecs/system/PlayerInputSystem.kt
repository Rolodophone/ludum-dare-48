package io.github.rolodophone.ludumdare48.ecs.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.utils.viewport.Viewport
import io.github.rolodophone.ludumdare48.ecs.component.DogComponent
import io.github.rolodophone.ludumdare48.ecs.component.TransformComponent
import io.github.rolodophone.ludumdare48.event.GameEvent
import io.github.rolodophone.ludumdare48.event.GameEventManager
import io.github.rolodophone.ludumdare48.screen.LEVEL_HEIGHT
import io.github.rolodophone.ludumdare48.screen.START_LEVEL
import io.github.rolodophone.ludumdare48.screen.TILE_WIDTH
import io.github.rolodophone.ludumdare48.util.getNotNull
import io.github.rolodophone.ludumdare48.util.unprojectX
import io.github.rolodophone.ludumdare48.util.unprojectY
import ktx.ashley.allOf

/**
 * Handles player input and triggers the appropriate [GameEvent]s.
 */
class PlayerInputSystem(
	private val gameViewport: Viewport,
	private val gameEventManager: GameEventManager,
	dog: Entity
): IteratingSystem(
	allOf(TransformComponent::class).get()
) {
	private val dogComp = dog.getNotNull(DogComponent.mapper)
	private var dialogActionable = false

	private var level = START_LEVEL

	override fun addedToEngine(engine: Engine) {
		super.addedToEngine(engine)

		gameEventManager.listen(GameEvent.DialogActionable) {
			dialogActionable = true
		}

		gameEventManager.listen(GameEvent.DescendLevel) {
			level--
		}
	}

	override fun processEntity(entity: Entity, deltaTime: Float) {
		if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
			if (dogComp.state == DogComponent.State.RESTING) {
				for (diggableTile in dogComp.diggableTiles) {
					if (mouseInTile(diggableTile.first, diggableTile.second)) {
						startDigging(diggableTile.first, diggableTile.second)
						break
					}
				}
			}

			if (dialogActionable) {
				gameEventManager.trigger(GameEvent.CloseDialog)
				dialogActionable = false
			}
		}
	}

	private fun mouseInTile(x: Int, y: Int): Boolean {
		val mouseX = gameViewport.unprojectX(Gdx.input.x.toFloat())
		val mouseY = gameViewport.unprojectY(Gdx.input.y.toFloat())

		val left = x * TILE_WIDTH
		val bottom = (y - level * LEVEL_HEIGHT) * TILE_WIDTH
		val right = (x+1) * TILE_WIDTH
		val top = bottom + TILE_WIDTH

		return mouseX >= left &&
				mouseX < right &&
				mouseY >= bottom &&
				mouseY < top
	}

	private fun startDigging(x: Int, y: Int) {
		dogComp.diggingX = x
		dogComp.diggingY = y
		gameEventManager.trigger(GameEvent.StartDigging)
	}
}