package io.github.rolodophone.ludumdare48.screen

import io.github.rolodophone.ludumdare48.MyGame
import ktx.app.KtxScreen

abstract class MyScreen(val game: MyGame): KtxScreen {
	val batch = game.batch
	val gameViewport = game.gameViewport
	val engine = game.engine
	val textures = game.myTextures
}