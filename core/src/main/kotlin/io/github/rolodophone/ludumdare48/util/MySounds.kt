package io.github.rolodophone.ludumdare48.util

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.utils.Disposable
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Suppress("PrivatePropertyName")
class MySounds: Disposable {
	private val dog_dig = Gdx.audio.newSound(Gdx.files.internal("sound/dog_dig.ogg"))!!
	private val dog_hurt = Gdx.audio.newSound(Gdx.files.internal("sound/dog_hurt.ogg"))!!
	private val dog_happy = Gdx.audio.newSound(Gdx.files.internal("sound/dog_happy.ogg"))!!

	private fun playSound(sound: Sound) {
		sound.play()
	}

	fun playDogDig(length: Float) {
		val soundId = dog_dig.play()
		GlobalScope.launch {
			delay((length * 1000).toLong())
			dog_dig.stop(soundId)
		}
	}
	fun playDogHurt() = playSound(dog_hurt)
	fun playDogHappy() = playSound(dog_happy)

	override fun dispose() {
		dog_dig.dispose()
		dog_hurt.dispose()
		dog_happy.dispose()
	}
}