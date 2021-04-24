package io.github.rolodophone.ludumdare48.screen

import com.badlogic.ashley.core.Entity
import io.github.rolodophone.ludumdare48.LayoutManager
import io.github.rolodophone.ludumdare48.MyGame
import io.github.rolodophone.ludumdare48.ecs.component.*
import io.github.rolodophone.ludumdare48.ecs.system.*
import io.github.rolodophone.ludumdare48.event.GameEventManager
import ktx.ashley.entity
import ktx.ashley.with
import kotlin.random.Random.Default.nextInt

private const val MAX_DELTA_TIME = 1/10f

const val NUM_COLUMNS = 6
const val NUM_ROWS = 9

const val TILE_WIDTH = 30

class GameScreen(game: MyGame): MyScreen(game) {
	private val gameEventManager = GameEventManager()
	private val layoutManager = LayoutManager(textures)

	private lateinit var sky: Entity
	private lateinit var tiles: Array<Array<Entity>>
	private lateinit var dog: Entity

	@Suppress("UNUSED_VARIABLE")
	override fun show() {
		reset()
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

	fun reset() {
		//remove game entities and systems
		engine.removeAllEntities()
		engine.removeAllSystems()

		//add entities
		sky = engine.entity {
			with<TransformComponent> {
				setSizeFromTexture(textures.background)
				rect.setPosition(0f, gameViewport.worldHeight - textures.background.regionHeight)
			}
			with<GraphicsComponent> {
				sprite.setRegion(textures.background)
			}
		}
		tiles = Array(NUM_ROWS) { y ->
			Array(NUM_COLUMNS) { x ->
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
						type = TileComponent.Type.DIGGABLE
					}
				}
			}
		}
		dog = engine.entity {
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
				type = TileComponent.Type.OTHER
			}
			with<AnimationComponent> {
				textureList = textures.dog_rest
				frameDuration = 1/4f
			}
			with<DogComponent>()
		}

		//add systems
		engine.run {
			addSystem(PlayerInputSystem(gameViewport, gameEventManager, dog))
			addSystem(AnimationSystem())
			addSystem(DialogSystem(gameEventManager))
			addSystem(RenderSystem(batch, gameViewport))
			addSystem(DigSystem(gameEventManager, layoutManager, textures, tiles, ::reset, dog))
			addSystem(DebugSystem(gameEventManager, gameViewport, textures, dog))
		}
	}
}