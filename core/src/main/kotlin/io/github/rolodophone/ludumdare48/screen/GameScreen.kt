package io.github.rolodophone.ludumdare48.screen

import com.badlogic.gdx.math.Vector2
import io.github.rolodophone.ludumdare48.MyGame
import io.github.rolodophone.ludumdare48.ecs.component.AnimationComponent
import io.github.rolodophone.ludumdare48.ecs.component.GraphicsComponent
import io.github.rolodophone.ludumdare48.ecs.component.TileComponent
import io.github.rolodophone.ludumdare48.ecs.component.TransformComponent
import io.github.rolodophone.ludumdare48.ecs.system.AnimationSystem
import io.github.rolodophone.ludumdare48.ecs.system.DebugSystem
import io.github.rolodophone.ludumdare48.ecs.system.PlayerInputSystem
import io.github.rolodophone.ludumdare48.ecs.system.RenderSystem
import io.github.rolodophone.ludumdare48.event.GameEventManager
import ktx.ashley.entity
import ktx.ashley.with
import kotlin.random.Random.Default.nextInt

private val tempVector = Vector2()

private const val MAX_DELTA_TIME = 1/10f

private const val NUM_COLUMNS = 6
private const val NUM_ROWS = 9

class GameScreen(game: MyGame): MyScreen(game) {
	private val gameEventManager = GameEventManager()

	@Suppress("UNUSED_VARIABLE")
	override fun show() {
		for (y in 0 until NUM_ROWS) {
			for (x in 0 until NUM_COLUMNS) {
				
				val thisTexture = textures.block_dirt.random()
				
				engine.entity { 
					with<TransformComponent> {
						setSizeFromTexture(thisTexture)
						rect.setPosition(
							(x * thisTexture.regionWidth).toFloat(), 
							(y * thisTexture.regionHeight).toFloat()
						)
					}
					with<GraphicsComponent> {
						sprite.setRegion(thisTexture)
						repeat(nextInt(4)) {
							sprite.rotate90(true)
						}
					}
					with<TileComponent> {
						xIndex = x
						yIndex = y
					}
				}
			}
		}

		val dog = engine.entity {
			with<TransformComponent> {
				setSizeFromTexture(textures.dog_rest[0])
				rect.setPosition(2f * textures.block_dirt[0].regionWidth, 9f * textures.block_dirt[0].regionHeight)
			}
			with<GraphicsComponent> {
				sprite.setRegion(textures.dog_rest[0])
			}
			with<TileComponent> {
				xIndex = 2
				yIndex = 9
			}
			with<AnimationComponent> {
				textureList = textures.dog_rest
				frameDuration = 0.25f
			}
		}

		engine.run {
			addSystem(PlayerInputSystem(gameViewport, gameEventManager))
			addSystem(AnimationSystem())
			addSystem(RenderSystem(batch, gameViewport))
			addSystem(DebugSystem(gameEventManager, gameViewport, textures))
		}
	}

	override fun hide() {
		//remove game entities and systems
		engine.removeAllEntities()
		engine.removeAllSystems()
	}

	override fun render(delta: Float) {
		val newDeltaTime = if (delta > MAX_DELTA_TIME) MAX_DELTA_TIME else delta
		engine.update(newDeltaTime)
	}

	override fun resize(width: Int, height: Int) {
		gameViewport.update(width, height, true)
	}
}