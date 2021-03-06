package io.github.rolodophone.ludumdare48.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.viewport.Viewport
import io.github.rolodophone.ludumdare48.ecs.component.GraphicsComponent
import io.github.rolodophone.ludumdare48.ecs.component.MoveComponent
import io.github.rolodophone.ludumdare48.ecs.component.TextComponent
import io.github.rolodophone.ludumdare48.ecs.component.TransformComponent
import io.github.rolodophone.ludumdare48.util.getNotNull
import io.github.rolodophone.ludumdare48.util.setBounds
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.graphics.use
import ktx.log.error
import ktx.log.logger

private val log = logger<RenderSystem>()

/**
 * Renders the entities on the screen
 */
class RenderSystem(
	private val batch: Batch,
	private val gameViewport: Viewport
): SortedIteratingSystem(
	allOf(TransformComponent::class, GraphicsComponent::class).get(),
	compareBy { entity -> entity[TransformComponent.mapper] }
) {
	private val font = BitmapFont(Gdx.files.internal("font/pixelated.fnt"), Gdx.files.internal("font/pixelated.png"), false)

	override fun update(deltaTime: Float) {
		gameViewport.apply()
		batch.use(gameViewport.camera.combined) {
			super.update(deltaTime)
		}
	}

	override fun processEntity(entity: Entity, deltaTime: Float) {
		val transformComp = entity.getNotNull(TransformComponent.mapper)
		val graphicsComp = entity.getNotNull(GraphicsComponent.mapper)

		if (graphicsComp.sprite.texture == null) {
			log.error { "Entity $entity has no texture for rendering" }
			return
		}

		if (!graphicsComp.visible) return

		// use interpolated position if entity has MoveComponent; otherwise use TransformComponent.rect
		val moveComp = entity[MoveComponent.mapper]
		if (moveComp == null) {
			graphicsComp.sprite.setBounds(transformComp.rect)
		} else {
			graphicsComp.sprite.setBounds(
				moveComp.interpolatedPosition.x,
				moveComp.interpolatedPosition.y,
				transformComp.rect.width,
				transformComp.rect.height
			)
		}

		graphicsComp.sprite.rotation = transformComp.rotation
		graphicsComp.sprite.draw(batch)

		val textComp = entity[TextComponent.mapper]
		if (textComp != null) {
			batch.projectionMatrix = gameViewport.camera.projection.scale(1/4f, 1/4f, 1f)
			textComp.draw(font, batch as SpriteBatch)
			batch.projectionMatrix = gameViewport.camera.combined
		}
	}
}