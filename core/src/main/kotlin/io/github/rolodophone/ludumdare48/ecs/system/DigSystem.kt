package io.github.rolodophone.ludumdare48.ecs.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import io.github.rolodophone.ludumdare48.MyTextures
import io.github.rolodophone.ludumdare48.ecs.component.*
import io.github.rolodophone.ludumdare48.event.GameEvent
import io.github.rolodophone.ludumdare48.event.GameEventManager
import io.github.rolodophone.ludumdare48.screen.NUM_COLUMNS
import io.github.rolodophone.ludumdare48.screen.TILE_WIDTH
import io.github.rolodophone.ludumdare48.util.getNotNull
import ktx.ashley.entity
import ktx.ashley.with
import kotlin.random.Random.Default.nextInt

class DigSystem(
	private val gameEventManager: GameEventManager,
	private val textures: MyTextures,
	private val tiles: Array<Array<Entity>>,
	dog: Entity
):
	EntitySystem() {
	private val dogTransformComp = dog.getNotNull(TransformComponent.mapper)
	private val dogAnimationComp = dog.getNotNull(AnimationComponent.mapper)
	private val dogTileComp = dog.getNotNull(TileComponent.mapper)
	private val dogComp = dog.getNotNull(DogComponent.mapper)

	var timeSinceStartedDigging = 0f

	override fun addedToEngine(engine: Engine) {
		// add initial tile highlights
		var tileHighlights = MutableList(6) {
			createTileHighlight(it, 8)
		}
		dogComp.diggableTiles = MutableList(6) {
			Pair(it, 8)
		}

		gameEventManager.listen(GameEvent.StartDigging) {
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

			dogTransformComp.rect.setPosition(
				(dogTileComp.xIndex * TILE_WIDTH).toFloat(),
				(dogTileComp.yIndex * TILE_WIDTH).toFloat()
			)

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
			//move to tile dug
			dogTransformComp.rect.setPosition(
				(dogComp.diggingX * TILE_WIDTH).toFloat(),
				(dogComp.diggingY * TILE_WIDTH).toFloat()
			)

			dogTileComp.xIndex = dogComp.diggingX
			dogTileComp.yIndex = dogComp.diggingY

			//animation
			dogAnimationComp.textureList = textures.dog_rest
			dogAnimationComp.animIndex = 0
			dogAnimationComp.frameDuration = 1/4f

			dogComp.state = DogComponent.State.RESTING

			// replace diggable with background tile
			engine.removeEntity(tiles[dogComp.diggingY][dogComp.diggingX])

			tiles[dogComp.diggingY][dogComp.diggingX] = engine.entity {
				with<TransformComponent> {
					setSizeFromTexture(textures.block_background)
					rect.setPosition((dogComp.diggingX * TILE_WIDTH).toFloat(), (dogComp.diggingY * TILE_WIDTH).toFloat())
					z = -10
				}
				with<GraphicsComponent> {
					sprite.setRegion(textures.block_background)
					repeat(nextInt(4)) {
						sprite.rotate90(true)
					}
				}
				with<TileComponent> {
					xIndex = dogComp.diggingX
					yIndex = dogComp.diggingY
					type = TileComponent.Type.BACKGROUND
				}
			}

			//tile highlights
			findDiggableTiles()

			tileHighlights = MutableList(dogComp.diggableTiles.size) { i ->
				createTileHighlight(dogComp.diggableTiles[i].first, dogComp.diggableTiles[i].second)
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
				type = TileComponent.Type.OTHER
			}
		}
	}

	private fun findDiggableTiles() {
		var left: Int? = null

		for (x in dogTileComp.xIndex downTo 0) {
			val tile = tiles[dogTileComp.yIndex][x]
			if (tile.getNotNull(TileComponent.mapper).type == TileComponent.Type.DIGGABLE) {
				left = x
				break
			}
		}

		var right: Int? = null

		for (x in dogTileComp.xIndex until NUM_COLUMNS) {
			val tile = tiles[dogTileComp.yIndex][x]
			if (tile.getNotNull(TileComponent.mapper).type == TileComponent.Type.DIGGABLE) {
				right = x
				break
			}
		}

		dogComp.diggableTiles.clear()
		if (left != null) dogComp.diggableTiles.add(Pair(left, dogTileComp.yIndex))
		if (right != null) dogComp.diggableTiles.add(Pair(right, dogTileComp.yIndex))

		if (left == null) left = -1
		if (right == null) right = NUM_COLUMNS

		dogComp.diggableTiles.addAll(List(right - left - 1) { i ->
			Pair(left + i + 1, dogTileComp.yIndex - 1)
		})
	}
}