package io.github.rolodophone.ludumdare48.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.viewport.Viewport
import io.github.rolodophone.ludumdare48.ecs.component.*
import io.github.rolodophone.ludumdare48.util.getNotNull
import ktx.ashley.allOf
import kotlin.math.absoluteValue

private const val ANGULAR_DECELERATION = 100f
private const val MAX_ANGULAR_VELOCITY = 200f

/**
 * When the ball hits the paddle, [PaddleComponent.velocity] * [ANGULAR_VELOCITY_PER_PADDLE_VELOCITY] is added to its
 * [SpinComponent.angularVelocity]
 */
private const val ANGULAR_VELOCITY_PER_PADDLE_VELOCITY = 0.3f

/**
 * Makes the ball bounce off the walls and paddle
 */
class BallBounceSystem(
	private val gameViewport: Viewport,
	private val paddle: Entity,
	private val wallWidth: Float
):
IteratingSystem(allOf(BallComponent::class, TransformComponent::class, MoveComponent::class).get()) {
	override fun processEntity(entity: Entity, deltaTime: Float) {
		val ballMoveComp = entity.getNotNull(MoveComponent.mapper)
		val ballTransformComp = entity.getNotNull(TransformComponent.mapper)
		val ballSpinComp = entity.getNotNull(SpinComponent.mapper)

		val ballLeft = ballTransformComp.rect.x
		val ballBottom = ballTransformComp.rect.y
		val ballRight = ballTransformComp.rect.x + ballTransformComp.rect.width
		val ballTop = ballTransformComp.rect.y + ballTransformComp.rect.height

		val paddleTransformComp = paddle.getNotNull(TransformComponent.mapper)
		val paddlePaddleComp = paddle.getNotNull(PaddleComponent.mapper)

		val paddleTop = paddleTransformComp.rect.y + paddleTransformComp.rect.height
		val paddleBottom = paddleTransformComp.rect.y
		val paddleLeft = paddleTransformComp.rect.x
		val paddleRight = paddleTransformComp.rect.x + paddleTransformComp.rect.width

		// bounce off left wall
		if (ballLeft < wallWidth) {
			ballMoveComp.velocity.x = ballMoveComp.velocity.x.absoluteValue
		}

		//bounce off right wall
		else if (ballRight > gameViewport.worldWidth - wallWidth) {
			ballMoveComp.velocity.x = -ballMoveComp.velocity.x.absoluteValue
		}

		//bounce off top wall
		if (ballTop > gameViewport.worldHeight - wallWidth) {
			ballMoveComp.velocity.y = -ballMoveComp.velocity.y.absoluteValue
		}

		//bounce off paddle
		else if (ballBottom < paddleTop &&
			ballTop > paddleBottom &&
			ballRight > paddleLeft &&
			ballLeft < paddleRight
		) {
			ballMoveComp.velocity.y = ballMoveComp.velocity.y.absoluteValue

			//spin the ball depending on paddle's velocity
			ballSpinComp.angularVelocity += ANGULAR_VELOCITY_PER_PADDLE_VELOCITY * paddlePaddleComp.velocity

			//avoid spinning too much
			ballSpinComp.angularVelocity = MathUtils.clamp(
				ballSpinComp.angularVelocity,
				-MAX_ANGULAR_VELOCITY,
				MAX_ANGULAR_VELOCITY
			)

			//set angular deceleration so the spin goes away after a bit
			if (ballSpinComp.angularVelocity > 0) {
				ballSpinComp.angularAcceleration = -ANGULAR_DECELERATION
			}
			else {
				ballSpinComp.angularAcceleration = ANGULAR_DECELERATION
			}
		}
	}
}