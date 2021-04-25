package io.github.rolodophone.ludumdare48.ecs.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.GdxRuntimeException
import com.badlogic.gdx.utils.viewport.Viewport
import io.github.rolodophone.ludumdare48.MyTextures
import io.github.rolodophone.ludumdare48.ecs.component.AnimationComponent
import io.github.rolodophone.ludumdare48.ecs.component.GraphicsComponent
import io.github.rolodophone.ludumdare48.ecs.component.MoveComponent
import io.github.rolodophone.ludumdare48.ecs.component.TransformComponent
import io.github.rolodophone.ludumdare48.event.GameEvent
import io.github.rolodophone.ludumdare48.event.GameEventManager
import io.github.rolodophone.ludumdare48.util.halfWorldHeight
import io.github.rolodophone.ludumdare48.util.halfWorldWidth
import ktx.ashley.entity
import ktx.ashley.with

class CutsceneSystem(
	private val gameEventManager: GameEventManager,
	private val textures: MyTextures,
	private val gameViewport: Viewport,
	private val batch: SpriteBatch,
	private val startGame: () -> Unit,
	private val restartGame: () -> Unit
): EntitySystem() {
	private var funcQueue = mutableListOf<() -> Unit>()
	private var delays = mutableListOf<Float>()
	private var timeLeft = 0f

	private var entity: Entity? = null

	override fun update(deltaTime: Float) {
		if (funcQueue.isNotEmpty()) {
			timeLeft -= deltaTime

			if (timeLeft < 0f) {
				funcQueue[0].invoke()
				funcQueue.removeFirst()

				if (funcQueue.isNotEmpty()) {
					timeLeft = delays[0]
					delays.removeFirst()
				}
			}
		}
	}

	private fun runDelayed(delays: List<Float>, funcs: List<() -> Unit>) {
		this.delays = delays.toMutableList()
		this.funcQueue = funcs.toMutableList()
	}

	private fun zoomOut() {
		(gameViewport.camera as OrthographicCamera).zoom = 1f
		gameViewport.camera.update()
		batch.projectionMatrix = gameViewport.camera.combined
	}

	private fun hungry0() {
		entity = engine.entity {
			with<TransformComponent> {
				setSizeFromTexture(textures.dog_hungry[0])
				rect.setPosition(gameViewport.halfWorldWidth() - 75, gameViewport.halfWorldHeight() - 20)
			}
			with<MoveComponent> {
				velocity.set(2f, 0f)
			}
			with<GraphicsComponent> {
				sprite.setRegion(textures.dog_hungry[0])
			}
			with<AnimationComponent> {
				textureList = textures.dog_hungry
				frameDuration = 1/8f
			}
		}
	}

	private fun hungry1() {
		engine.removeEntity(entity)
		entity = engine.entity {
			with<TransformComponent> {
				setSizeFromTexture(textures.dog_think[0])
				rect.setPosition(gameViewport.halfWorldWidth() - 30, gameViewport.halfWorldHeight() - 20)
			}
			with<MoveComponent> {
				velocity.set(-3f, -2f)
			}
			with<GraphicsComponent> {
				sprite.setRegion(textures.dog_think[0])
			}
			with<AnimationComponent> {
				textureList = textures.dog_think
				frameDuration = 1f
				loop = false
			}
		}
	}

	private fun hungry2() {
		engine.removeEntity(entity)
		entity = null
		zoomOut()
		startGame()
	}

	override fun addedToEngine(engine: Engine) {
		gameEventManager.listen(GameEvent.Cutscene) { event ->
			//zoom in
			(gameViewport.camera as OrthographicCamera).zoom = 1/8f
			gameViewport.camera.update()
			batch.projectionMatrix = gameViewport.camera.combined

			when (event.id) {
				0 -> runDelayed(listOf(4f, 4.5f), listOf(::hungry0, ::hungry1, ::hungry2))
				else -> throw GdxRuntimeException("No cutscene with id ${event.id}")
			}
		}
	}
}