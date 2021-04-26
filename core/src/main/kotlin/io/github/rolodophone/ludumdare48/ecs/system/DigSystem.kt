package io.github.rolodophone.ludumdare48.ecs.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.utils.viewport.Viewport
import io.github.rolodophone.ludumdare48.*
import io.github.rolodophone.ludumdare48.ecs.component.*
import io.github.rolodophone.ludumdare48.event.GameEvent
import io.github.rolodophone.ludumdare48.event.GameEventManager
import io.github.rolodophone.ludumdare48.screen.*
import io.github.rolodophone.ludumdare48.util.getNotNull
import ktx.ashley.entity
import ktx.ashley.with
import kotlin.random.Random.Default.nextInt

class DigSystem(
	private val gameEventManager: GameEventManager,
	private val layoutManager: LayoutManager,
	private val textures: MyTextures,
	private val tiles: Array<Array<Entity>>,
	private val sounds: MySounds,
	private val tip: String,
	private val gameViewport: Viewport,
	dog: Entity
):
	EntitySystem() {
	private val dogTransformComp = dog.getNotNull(TransformComponent.mapper)
	private val dogAnimationComp = dog.getNotNull(AnimationComponent.mapper)
	private val dogTileComp = dog.getNotNull(TileComponent.mapper)
	private val dogComp = dog.getNotNull(DogComponent.mapper)

	private var timeSinceStartedDigging = 0f

	private var level = START_LEVEL

	override fun addedToEngine(engine: Engine) {
		super.addedToEngine(engine)

		// add initial tile highlights
		var tileHighlights = MutableList(NUM_COLUMNS) {
			createTileHighlight(it, NUM_ROWS - 1)
		}
		dogComp.diggableTiles = MutableList(NUM_COLUMNS) {
			Pair(it, NUM_ROWS - 1)
		}

		gameEventManager.listen(GameEvent.DescendLevel) {
			level--
			gameEventManager.trigger(GameEvent.ViewportResized)
		}

		gameEventManager.listen(GameEvent.ViewportResized) {
			if (dogComp.state != DogComponent.State.DIALOG) {
//				gameViewport.camera.center()
				gameViewport.camera.translate(0f, -((NUM_LEVELS-level-1) * LEVEL_HEIGHT * TILE_WIDTH).toFloat(), 0f)
			}
		}

		gameEventManager.listen(GameEvent.StartDigging) {
			//play sound
			sounds.playDogDig(dogComp.digDuration)

			// move to tile
			if (dogComp.diggingY < dogTileComp.yIndex) { // digging down
				dogTileComp.xIndex = dogComp.diggingX
			}
			else {
				if (dogComp.diggingX < dogTileComp.xIndex) { // digging left
					dogTileComp.xIndex = dogComp.diggingX + 1
				}
				else { // digging right
					dogTileComp.xIndex = dogComp.diggingX - 1
				}
			}

			dogTransformComp.rect.setPosition(tileToPosX(dogTileComp.xIndex), tileToPosY(dogTileComp.yIndex))

			//remove tile highlights
			tileHighlights.forEach { engine.removeEntity(it) }
			tileHighlights.clear()

			//animation
			dogComp.state = DogComponent.State.DIGGING
			timeSinceStartedDigging = 0f

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
		}

		gameEventManager.listen(GameEvent.FinishDigging) {
			val thisLayoutTile = layoutManager.layout[dogComp.diggingY][dogComp.diggingX]

			//play sound
			if (thisLayoutTile is HurtItem) {
				sounds.playDogHurt()
			}
			else if (thisLayoutTile is Item) {
				sounds.playDogHappy()
			}

			if (thisLayoutTile !is Obstacle) {
				//move to tile dug
				dogTransformComp.rect.setPosition(tileToPosX(dogComp.diggingX), tileToPosY(dogComp.diggingY))

				dogTileComp.xIndex = dogComp.diggingX
				dogTileComp.yIndex = dogComp.diggingY
			}

			// replace diggable with new tile
			engine.removeEntity(tiles[dogComp.diggingY][dogComp.diggingX])

			tiles[dogComp.diggingY][dogComp.diggingX] = engine.entity {
				with<TransformComponent> {
					setSizeFromTexture(textures.block_background)
					rect.setPosition(tileToPosX(dogComp.diggingX), tileToPosY(dogComp.diggingY))
					z = when (thisLayoutTile) {
						is Obstacle -> 0
						else -> -10
					}
				}
				with<GraphicsComponent> {
					sprite.setRegion(
						when (thisLayoutTile) {
							is Obstacle -> thisLayoutTile.texture
							else -> if (dogComp.diggingY >= 9) textures.block_background
								else textures.gravel_dark
						}
					)
					if (thisLayoutTile !is Obstacle || thisLayoutTile.randomRotation) {
						repeat(nextInt(4)) {
							sprite.rotate90(true)
						}
					}
				}
				with<TileComponent> {
					xIndex = dogComp.diggingX
					yIndex = dogComp.diggingY
					type = when (thisLayoutTile) {
						is Obstacle -> TileComponent.Type.OBSTACLE
						else -> TileComponent.Type.BACKGROUND
					}
				}
			}

			if (thisLayoutTile is Item) {
				//show item image
				dogAnimationComp.textureList = listOf(thisLayoutTile.texture)
				dogAnimationComp.animIndex = 0

				//show dialog
				gameEventManager.trigger(GameEvent.ShowDialog.apply {

					val newMessage = mutableListOf<String>()
					newMessage.addAll(thisLayoutTile.message)

					when (thisLayoutTile) {
						is HurtItem -> {
							newMessage.add("\nTip: $tip")
							actionText = "Tap to try again."
							effect = {
								gameEventManager.trigger(GameEvent.GameOver)
							}
						}
						is BoneItem -> {
							actionText = "Tap to continue."
							effect = {
								gameEventManager.trigger(GameEvent.GameCompleted)
							}
						}
						else -> {
							actionText = "Tap to continue."
							effect = {
								thisLayoutTile.effect.invoke(dogComp)
								gameEventManager.trigger(GameEvent.DogRest)
							}
						}
					}

					message = newMessage
				})
			}
			else {
				//begin resting
				gameEventManager.trigger(GameEvent.DogRest)
			}
		}

		gameEventManager.listen(GameEvent.DogRest) {
			dogComp.state = DogComponent.State.RESTING

			//animation
			dogAnimationComp.textureList = textures.dog_rest
			dogAnimationComp.animIndex = 0
			dogAnimationComp.frameDuration = 1/4f

			//descending level
			if (dogTileComp.yIndex % LEVEL_HEIGHT == 0 && dogTileComp.yIndex != NUM_ROWS && dogTileComp.yIndex != 0) {
				gameEventManager.trigger(GameEvent.DescendLevel)
			}

			if (tileHighlights.isEmpty()) {
				//tile highlights
				findDiggableTiles()

				if (dogComp.diggableTiles.isEmpty()) {
					gameEventManager.trigger(GameEvent.ShowDialog.apply {
						message = listOf("Oh no! I'm stuck!\nTip: $tip")
						actionText = "Tap to try again."
						effect = {
							gameEventManager.trigger(GameEvent.GameOver)
						}
					})
				}

				tileHighlights = MutableList(dogComp.diggableTiles.size) { i ->
					createTileHighlight(dogComp.diggableTiles[i].first, dogComp.diggableTiles[i].second)
				}
			}
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
				rect.setPosition(tileToPosX(x), tileToPosY(y))
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
				type = TileComponent.Type.OTHER
			}
		}
	}

	private fun findDiggableTiles() {
		dogComp.diggableTiles.clear()

		var left: Int? = null
		var right: Int? = null

		var bottomLeft = 0
		var bottomRight = NUM_COLUMNS - 1

		if (dogTileComp.yIndex < NUM_ROWS) {
			// find left
			for (x in dogTileComp.xIndex downTo 0) {
				val tile = tiles[dogTileComp.yIndex][x]
				if (tile.getNotNull(TileComponent.mapper).type == TileComponent.Type.DIGGABLE) {
					left = x
					bottomLeft = x + 1
					break
				} else if (tile.getNotNull(TileComponent.mapper).type == TileComponent.Type.OBSTACLE) {
					bottomLeft = x + 1
					break
				}
			}
			//find right
			for (x in dogTileComp.xIndex until NUM_COLUMNS) {
				val tile = tiles[dogTileComp.yIndex][x]
				if (tile.getNotNull(TileComponent.mapper).type == TileComponent.Type.DIGGABLE) {
					right = x
					bottomRight = x - 1
					break
				} else if (tile.getNotNull(TileComponent.mapper).type == TileComponent.Type.OBSTACLE) {
					bottomRight = x - 1
					break
				}
			}
		}

		if (left != null) dogComp.diggableTiles.add(Pair(left, dogTileComp.yIndex))
		if (right != null) dogComp.diggableTiles.add(Pair(right, dogTileComp.yIndex))

		//find bottom
		if (dogTileComp.yIndex > 0) {
			for (x in bottomLeft..bottomRight) {
				val tile = tiles[dogTileComp.yIndex - 1][x]
				if (tile.getNotNull(TileComponent.mapper).type == TileComponent.Type.DIGGABLE) {
					dogComp.diggableTiles.add(Pair(x, dogTileComp.yIndex - 1))
				}
			}
		}
	}

	private fun tileToPosX(x: Int) = (x * TILE_WIDTH).toFloat()
	private fun tileToPosY(y: Int) = ((y - ((NUM_LEVELS - 1) * LEVEL_HEIGHT)) * TILE_WIDTH).toFloat()
}