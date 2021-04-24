package io.github.rolodophone.ludumdare48.screen

import com.badlogic.gdx.math.Vector2
import io.github.rolodophone.ludumdare48.MyGame
import io.github.rolodophone.ludumdare48.ecs.system.DebugSystem
import io.github.rolodophone.ludumdare48.ecs.system.PlayerInputSystem
import io.github.rolodophone.ludumdare48.ecs.system.RenderSystem
import io.github.rolodophone.ludumdare48.event.GameEventManager

private val tempVector = Vector2()

private const val MAX_DELTA_TIME = 1/10f

private const val WALL_WIDTH = 3f

class GameScreen(game: MyGame): MyScreen(game) {
	private val gameEventManager = GameEventManager()

	@Suppress("UNUSED_VARIABLE")
	override fun show() {

		//add systems to engine (it is recommended to render *before* stepping the physics for some reason)
		engine.run {
			addSystem(PlayerInputSystem(gameViewport, gameEventManager))
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