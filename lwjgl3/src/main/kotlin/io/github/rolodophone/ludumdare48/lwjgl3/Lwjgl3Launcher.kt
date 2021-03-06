package io.github.rolodophone.ludumdare48.lwjgl3

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import io.github.rolodophone.ludumdare48.MyGame

/** Launches the desktop (LWJGL3) application.  */
fun main() {
	Lwjgl3Application(MyGame(), Lwjgl3ApplicationConfiguration().apply {
		setTitle("Dig Dog Dig")
		setWindowedMode(9 * 60, 16 * 60)
		setWindowIcon("icons/icon128.png", "icons/icon64.png", "icons/icon32.png", "icons/icon16.png")
		setResizable(false)
	})
}
