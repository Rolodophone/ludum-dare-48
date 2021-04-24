package io.github.rolodophone.ludumdare48.ecs.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import io.github.rolodophone.ludumdare48.MyTextures
import io.github.rolodophone.ludumdare48.ecs.component.*
import io.github.rolodophone.ludumdare48.event.GameEvent
import io.github.rolodophone.ludumdare48.event.GameEventManager
import io.github.rolodophone.ludumdare48.screen.TILE_WIDTH
import io.github.rolodophone.ludumdare48.util.getNotNull
import ktx.ashley.entity
import ktx.ashley.with

class DigSystem(private val gameEventManager: GameEventManager, private val textures: MyTextures, dog: Entity): EntitySystem() {
	private val dogTransformComp = dog.getNotNull(TransformComponent.mapper)
	private val dogAnimationComp = dog.getNotNull(AnimationComponent.mapper)
	private val dogTileComp = dog.getNotNull(TileComponent.mapper)
	private val dogComp = dog.getNotNull(DogComponent.mapper)

	var gameStarted = false
	var timeSinceStartedDigging = 0f

	override fun addedToEngine(engine: Engine) {
		val tileHighlights = MutableList(6) {
			createTileHighlight(it, 8)
		}

		gameEventManager.listen(GameEvent.StartDigging) {
			if (!gameStarted) {
				dogTransformComp.rect.setPosition(
					(dogComp.diggingX * TILE_WIDTH).toFloat(),
					((dogComp.diggingY + 1) * TILE_WIDTH).toFloat()
				)
				dogTileComp.xIndex = dogComp.diggingX
				dogTileComp.yIndex = dogComp.diggingY + 1

				gameStarted = true
			}

			when (dogComp.diggingX) {
				dogTileComp.xIndex -> { //digging down
					dogAnimationComp.textureList = textures.dog_dig_down
					dogAnimationComp.animIndex = 0
					dogAnimationComp.frameDuration = 1/8f
				}
				dogTileComp.xIndex - 1 -> { //digging left
					dogAnimationComp.textureList = textures.dog_dig_left
					dogAnimationComp.animIndex = 0
					dogAnimationComp.frameDuration = 1/12f
				}
				dogTileComp.xIndex + 1 -> { //digging right
					dogAnimationComp.textureList = textures.dog_dig_right
					dogAnimationComp.animIndex = 0
					dogAnimationComp.frameDuration = 1/12f
				}
			}
			
			dogComp.state = DogComponent.State.DIGGING
			timeSinceStartedDigging = 0f
		}

		gameEventManager.listen(GameEvent.FinishDigging) {
			dogAnimationComp.textureList = textures.dog_rest
			dogAnimationComp.animIndex = 0
			dogAnimationComp.frameDuration = 1/4f

			dogTransformComp.rect.setPosition(
				(dogComp.diggingX * TILE_WIDTH).toFloat(),
				(dogComp.diggingY * TILE_WIDTH).toFloat()
			)

			dogTileComp.xIndex = dogComp.diggingX
			dogTileComp.yIndex = dogComp.diggingY

			dogComp.state = DogComponent.State.RESTING
		}
	}

	override fun update(deltaTime: Float) {
		when (dogComp.state) {
			DogComponent.State.DIGGING -> {
				timeSinceStartedDigging += deltaTime
				
				if (timeSinceStartedDigging > dogComp.digDuration) {
					gameEventManager.trigger(GameEvent.FinishDigging)
				}
			}
			else -> {}
		}
	}

	private fun createTileHighlight(x: Int, y: Int): Entity {
		return engine.entity {
			with<TransformComponent> {
				setSizeFromTexture(textures.tile_highlight[0])
				rect.setPosition(
					(x * TILE_WIDTH).toFloat(),
					(y * TILE_WIDTH).toFloat()
				)
				z = 10
			}
			with<GraphicsComponent> {
				sprite.setRegion(textures.tile_highlight[0])
			}
			with<AnimationComponent> {
				textureList = textures.tile_highlight
				frameDuration = 1f
			}
			with<TileComponent> {
				xIndex = x
				yIndex = y
			}
		}
	}
}