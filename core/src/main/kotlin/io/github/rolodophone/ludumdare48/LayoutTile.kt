package io.github.rolodophone.ludumdare48

import com.badlogic.gdx.graphics.g2d.TextureRegion
import io.github.rolodophone.ludumdare48.ecs.component.DogComponent

open class LayoutTile

open class Obstacle(
	val texture: TextureRegion,
	val randomRotation: Boolean = false
): LayoutTile()

open class Item(
	val texture: TextureRegion,
	val message: List<String>,
	val effect: (DogComponent) -> Unit
): LayoutTile()

open class HurtItem(
	texture: TextureRegion,
	message: List<String>,
): Item(texture, message, {})