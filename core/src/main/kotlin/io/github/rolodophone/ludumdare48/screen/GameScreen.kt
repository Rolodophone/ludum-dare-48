package io.github.rolodophone.ludumdare48.screen

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import io.github.rolodophone.ludumdare48.LayoutManager
import io.github.rolodophone.ludumdare48.MyGame
import io.github.rolodophone.ludumdare48.MySounds
import io.github.rolodophone.ludumdare48.ecs.component.*
import io.github.rolodophone.ludumdare48.ecs.system.*
import io.github.rolodophone.ludumdare48.event.GameEvent
import io.github.rolodophone.ludumdare48.event.GameEventManager
import ktx.ashley.entity
import ktx.ashley.with
import kotlin.random.Random.Default.nextInt

private const val MAX_DELTA_TIME = 1/10f

const val NUM_COLUMNS = 6
const val NUM_ROWS = 18
const val LEVEL_HEIGHT = 9
const val START_LEVEL = 1
const val NUM_LEVELS = 2

const val TILE_WIDTH = 30

class GameScreen(game: MyGame): MyScreen(game) {
	private val gameEventManager = GameEventManager()
	private val layoutManager = LayoutManager(textures)
	private val shapeRenderer = ShapeRenderer()
	private val sounds = MySounds()

	private lateinit var sky: Entity
	private lateinit var tiles: Array<Array<Entity>>
	private lateinit var dog: Entity

	private var resetting = false

	private val introMusic = Gdx.audio.newMusic(Gdx.files.internal("sound/intro.ogg"))!!
	private val gameMusic = Gdx.audio.newMusic(Gdx.files.internal("sound/game.ogg"))!!
	private val outroMusic = Gdx.audio.newMusic(Gdx.files.internal("sound/outro.ogg"))!!

	private var startIntro = false

	private val tips = listOf(
		"Try to remember where\neverything is.",
		"Finding apple cores makes\ndigging quicker.",
		"You are looking for a bone.",
		"Don't get stuck!",
		"Treasure is of no use to a dog.",
		"Neither are diamonds.",
		"Always pick up as many apple\ncores as you can.",
		"There are two levels of depth.",
		"Choose your route carefully.",
		"Don't use a pen and paper. That's\ntotally cheating.",
		"I remember burying my owner's\nboot somewhere near here...",
		"It's a shame I got ill\notherwise I would sniff it out.",
		"Where is my bone?!",
		"Seriously, I'm still looking\nfor it?!",
		"I could be doing other things,\nlike playing fetch!",
		"Must... Find... Bone...",
		"Anytime now, surely?",
		"I must be so close!",
		"This is the developer speaking.\nYou're unacceptably bad at this\ngame.",
		"People like you are the reason\nI have to write so many tips.",
		"You've died 21 times now. Maybe\nit's time you gave up?",
		"And you've died again!",
		"Woof wof, woof wooof woof.\n(Stop distracting them)",
		"Who said that?",
		"Wuf wuf, wawoof.\n(Me, the dog)",
		"Okay dog, fine. By the way, what\nbreed of dog are you supposed to\nrepresent?",
		"Wuf wuf woof.\n(None of your business)",
		"Wow, I don't remember\nprogramming *that* attitude.",
		"Well, by this stage you've\nsurely completed the game so\nI guess I'll loop the tips here."
	)
	private var tipNum = 0

	@Suppress("UNUSED_VARIABLE")
	override fun show() {
		gameEventManager.listen(GameEvent.GameOver) { delayedRestart() }
		gameEventManager.listen(GameEvent.GameCompleted) { gameComplete() }
		gameEventManager.listen(GameEvent.StartGame) { startGame() }
		gameEventManager.listen(GameEvent.EndCutSceneFinished) { endCutsceneFinished() }

		addCoreSystems()
		startIntro()
	}

	override fun hide() {
		//remove game entities and systems
		engine.removeAllEntities()
		engine.removeAllSystems()
	}

	override fun render(delta: Float) {
		val newDeltaTime = if (delta > MAX_DELTA_TIME) MAX_DELTA_TIME else delta
		engine.update(newDeltaTime)

		if (resetting) {
			restartGame()
			resetting = false
		}
		if (startIntro) {
			startIntro()
			startIntro = false
		}
	}

	override fun resize(width: Int, height: Int) {
		gameViewport.update(width, height, true)
		gameEventManager.trigger(GameEvent.ViewportResized)
	}

	override fun dispose() {
		sounds.dispose()
		gameMusic.dispose()
	}

	private fun delayedRestart() {
		resetting = true
	}

	private fun startGame() {
		//start music
		introMusic.stop()
		gameMusic.volume = 0.5f
		gameMusic.position = 0f
		gameMusic.play()
		gameMusic.isLooping = true

		restartGame()
	}

	private fun gameComplete() {
		gameMusic.stop()
		outroMusic.position = 0f
		outroMusic.play()

		gameEventManager.trigger(GameEvent.Cutscene.apply { id = 1 })
	}

	private fun endCutsceneFinished() {
		outroMusic.stop()

		engine.removeAllEntities()
		engine.removeAllSystems()
		addCoreSystems()

		startIntro = true
	}

	private fun startIntro() {
		introMusic.volume = 0.7f
		introMusic.position = 0f
		introMusic.play()

		gameEventManager.trigger(GameEvent.Cutscene.apply { id = 0 })
	}

	private fun restartGame() {
		//remove event callbacks
		gameEventManager.removeAllCallbacks()

		gameEventManager.listen(GameEvent.GameOver) { delayedRestart() }
		gameEventManager.listen(GameEvent.GameCompleted) { gameComplete() }
		gameEventManager.listen(GameEvent.StartGame) { startGame() }
		gameEventManager.listen(GameEvent.EndCutSceneFinished) { endCutsceneFinished() }

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
				val thisTexture =
					if (y >= LEVEL_HEIGHT) textures.block_dirt.random()
					else textures.gravel

				engine.entity {
					with<TransformComponent> {
						setSizeFromTexture(thisTexture)
						rect.setPosition(
							(x * TILE_WIDTH).toFloat(),
							((y - START_LEVEL * LEVEL_HEIGHT) * TILE_WIDTH).toFloat()
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
				yIndex = NUM_ROWS
				type = TileComponent.Type.OTHER
			}
			with<AnimationComponent> {
				textureList = textures.dog_rest
				frameDuration = 1/4f
			}
			with<DogComponent>()
		}

		tipNum++
		if (tipNum == tips.size) tipNum = 0

		//add systems
		addCoreSystems()
		engine.run {
			addSystem(PlayerInputSystem(gameViewport, gameEventManager, dog))
			addSystem(DialogSystem(gameEventManager, gameViewport, batch as SpriteBatch, textures, dog))
			addSystem(CustomDrawSystem(shapeRenderer))
			addSystem(DigSystem(gameEventManager, layoutManager, textures, tiles, sounds, tips[tipNum], gameViewport, dog))
			addSystem(DebugSystem(gameEventManager, gameViewport, textures, dog))
		}
	}

	private fun addCoreSystems() {
		engine.run {
			addSystem(CutsceneSystem(gameEventManager, textures, sounds, gameViewport, batch as SpriteBatch))
			addSystem(AnimationSystem())
			addSystem(MoveSystem())
			addSystem(RenderSystem(batch, gameViewport))
		}
	}
}