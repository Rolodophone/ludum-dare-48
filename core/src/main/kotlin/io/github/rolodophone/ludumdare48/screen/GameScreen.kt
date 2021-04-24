package io.github.rolodophone.ludumdare48.screen

import com.badlogic.gdx.math.Vector2
import io.github.rolodophone.ludumdare48.MyGame
import io.github.rolodophone.ludumdare48.ecs.component.*
import io.github.rolodophone.ludumdare48.ecs.system.*
import io.github.rolodophone.ludumdare48.event.GameEventManager
import ktx.ashley.entity
import ktx.ashley.with
import kotlin.random.Random.Default.nextInt

private val tempVector = Vector2()

private const val MAX_DELTA_TIME = 1/10f

const val NUM_COLUMNS = 6
const val NUM_ROWS = 9

const val TILE_WIDTH = 30

class GameScreen(game: MyGame): MyScreen(game) {
	private val gameEventManager = GameEventManager()

	@Suppress("UNUSED_VARIABLE")
	override fun show() {
		engine.entity {
			with<TransformComponent> {
				setSizeFromTexture(textures.background)
				rect.setPosition(0f, gameViewport.worldHeight - textures.background.regionHeight)
			}
			with<GraphicsComponent> {
				sprite.setRegion(textures.background)
			}
		}

		for (y in 0 until NUM_ROWS) {
			for (x in 0 until NUM_COLUMNS) {
				
				val thisTexture = textures.block_dirt.random()
				
				engine.entity { 
					with<TransformComponent> {
						setSizeFromTexture(thisTexture)
						rect.setPosition(
							(x * TILE_WIDTH).toFloat(),
							(y * TILE_WIDTH).toFloat()
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
				rect.setPosition(2f * TILE_WIDTH, 9f * TILE_WIDTH)
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
				frameDuration = 1/4f
			}
			with<DogComponent>()
		}

		engine.run {
			addSystem(PlayerInputSystem(gameViewport, gameEventManager, dog))
			addSystem(AnimationSystem())
			addSystem(RenderSystem(batch, gameViewport))
			addSystem(DigSystem(gameEventManager, textures, dog))
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