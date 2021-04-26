package io.github.rolodophone.ludumdare48.ecs.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.viewport.Viewport
import io.github.rolodophone.ludumdare48.MyTextures
import io.github.rolodophone.ludumdare48.ecs.component.DogComponent
import io.github.rolodophone.ludumdare48.ecs.component.GraphicsComponent
import io.github.rolodophone.ludumdare48.ecs.component.TextComponent
import io.github.rolodophone.ludumdare48.ecs.component.TransformComponent
import io.github.rolodophone.ludumdare48.event.GameEvent
import io.github.rolodophone.ludumdare48.event.GameEventManager
import io.github.rolodophone.ludumdare48.screen.LEVEL_HEIGHT
import io.github.rolodophone.ludumdare48.screen.NUM_LEVELS
import io.github.rolodophone.ludumdare48.screen.START_LEVEL
import io.github.rolodophone.ludumdare48.screen.TILE_WIDTH
import io.github.rolodophone.ludumdare48.util.getNotNull
import ktx.ashley.entity
import ktx.ashley.with
import ktx.graphics.center
import java.util.*

private const val ACTION_DELAY = 1f

class DialogSystem(
	private val gameEventManager: GameEventManager,
	private val gameViewport: Viewport,
	private val batch: SpriteBatch,
	private val textures: MyTextures,
	dog: Entity
): EntitySystem() {
	private val dogComp = dog.getNotNull(DogComponent.mapper)
	private val dogTransform = dog.getNotNull(TransformComponent.mapper)

	private var dialogDelaying = false
	private var timeSinceDialogShown = 0f
	private var actionText = ""
	private var shownActionText = ""
	private var effect: () -> Unit = {}

	private var dialog: Entity? = null

	private var level = START_LEVEL

	private var willShowActionText = false

	override fun addedToEngine(engine: Engine) {
		gameEventManager.listen(GameEvent.ViewportResized) {
			if (dialog != null) {
				centerOnDog()
			}
		}

		gameEventManager.listen(GameEvent.ShowActionText) {
			willShowActionText = true
		}

		gameEventManager.listen(GameEvent.DescendLevel) {
			level--
		}

		gameEventManager.listen(GameEvent.ShowDialog) { event ->
			dogComp.state = DogComponent.State.DIALOG

			actionText = event.actionText.toUpperCase(Locale.getDefault())
			shownActionText = ""
			dialogDelaying = true
			val message = event.message.joinToString("\n").toUpperCase(Locale.getDefault())
			effect = event.effect

			centerOnDog()

			//draw the dialog box
			dialog = engine.entity {
				with<TransformComponent> {
					rect.set(
						dogTransform.rect.x - 3.5f,
						dogTransform.rect.y + 43,
						37f,
						26f
					)
				}
				with<GraphicsComponent> {
					sprite.setRegion(textures.dialog_box)
				}
				with<TextComponent> {
					draw = { font, batch ->
						font.setColor(0f, 0f, 0f, 1f)
						font.draw(batch, message, -65f, 110f)
						font.draw(batch, shownActionText, -65f, 55f)
					}
				}
			}
		}

		gameEventManager.listen(GameEvent.CloseDialog) {
			//remove dialog entity
			engine.removeEntity(dialog)
			dialog = null

			//reset zoom
			(gameViewport.camera as OrthographicCamera).zoom = 1f
			gameViewport.camera.center(
				-gameViewport.worldWidth,
				-gameViewport.worldHeight,
				gameViewport.worldWidth,
				gameViewport.worldHeight
			)
			gameViewport.camera.translate(0f, -((NUM_LEVELS -level-1) * LEVEL_HEIGHT * TILE_WIDTH).toFloat(), 0f)
			gameViewport.camera.update()
			batch.projectionMatrix = gameViewport.camera.combined

			//apply effect
			effect.invoke()

			//continue game
			gameEventManager.trigger(GameEvent.DogRest)
		}
	}

	private fun centerOnDog() {
		//zoom in on dog
		(gameViewport.camera as OrthographicCamera).zoom = 1 / 4f
		gameViewport.camera.center(30f, 30f, dogTransform.rect.x, dogTransform.rect.y + 20)
		gameViewport.camera.update()
		batch.projectionMatrix = gameViewport.camera.combined
	}

	override fun update(deltaTime: Float) {
		if (dialogDelaying) {
			timeSinceDialogShown += deltaTime

			if (timeSinceDialogShown > ACTION_DELAY) {
				showActionText()
			}
		}

		if (willShowActionText) {
			showActionText()
			willShowActionText = false
		}
	}

	private fun showActionText() {
		timeSinceDialogShown = 0f
		shownActionText = actionText
		gameEventManager.trigger(GameEvent.DialogActionable)
		dialogDelaying = false
	}
}
