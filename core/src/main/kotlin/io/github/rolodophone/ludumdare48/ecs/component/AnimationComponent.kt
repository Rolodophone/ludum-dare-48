package io.github.rolodophone.ludumdare48.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

class AnimationComponent: Component, Pool.Poolable {
	companion object {
		val mapper = mapperFor<AnimationComponent>()
	}

	var animIndex = 0
	var textureList = listOf<TextureRegion>()
	var frameDuration = 0f
	var timeSinceLastFrame = 0f
	var loop = true

	override fun reset() {
		animIndex = 0
		textureList = listOf()
		frameDuration = 0f
		timeSinceLastFrame = 0f
		loop = true
	}

	fun advanceFrame(graphicsComp: GraphicsComponent) {
		if (loop || animIndex < textureList.size - 1) {
			animIndex++
		}
		if (loop && animIndex == textureList.size) {
			animIndex = 0
		}

		graphicsComp.sprite.setRegion(textureList[animIndex])
	}
}