package io.github.rolodophone.ludumdare48.lwjgl3

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import io.github.rolodophone.ludumdare48.MyGame

/** Launches the desktop (LWJGL3) application.  */
fun main() {
	Lwjgl3Application(MyGame(), Lwjgl3ApplicationConfiguration().apply {
		setTitle("BrickBreaker")
		setWindowedMode(9 * 60, 16 * 60)
		setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png")
	})
}
