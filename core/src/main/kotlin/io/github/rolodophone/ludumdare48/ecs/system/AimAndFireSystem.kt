package io.github.rolodophone.ludumdare48.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.math.MathUtils
import io.github.rolodophone.ludumdare48.ecs.component.GraphicsComponent
import io.github.rolodophone.ludumdare48.ecs.component.PaddleComponent
import io.github.rolodophone.ludumdare48.ecs.component.PhysicsComponent
import io.github.rolodophone.ludumdare48.ecs.component.TransformComponent
import io.github.rolodophone.ludumdare48.event.GameEvent
import io.github.rolodophone.ludumdare48.event.GameEventManager
import io.github.rolodophone.ludumdare48.util.getNotNull
import io.github.rolodophone.ludumdare48.util.halfHeight
import io.github.rolodophone.ludumdare48.util.halfWidth

private const val BALL_MOMENTUM = 5000f

/**
 * Responsible for controlling the logic of aiming and firing the ball
 */
class AimAndFireSystem(gameEventManager: GameEventManager, paddle: Entity, ball: Entity, firingLine: Entity): EntitySystem() {
	private val paddleComp = paddle.getNotNull(PaddleComponent.mapper)
	private val paddleTransformComp = paddle.getNotNull(TransformComponent.mapper)
	private val ballBody = ball.getNotNull(PhysicsComponent.mapper).body!!
	private val firingLineTransformComp = firingLine.getNotNull(TransformComponent.mapper)
	private val firingLineGraphicsComp = firingLine.getNotNull(GraphicsComponent.mapper)

	init {
		gameEventManager.listen(GameEvent.StartAiming) {
			paddleComp.state = PaddleComponent.State.AIMING
			firingLineGraphicsComp.visible = true
		}

		gameEventManager.listen(GameEvent.AdjustAimAngle) { event ->
			paddleComp.firingAngle = event.newAngle
			firingLineTransformComp.rotation = event.newAngle
		}

		gameEventManager.listen(GameEvent.ShootBall) {
			paddleComp.state = PaddleComponent.State.DEFLECTING
			firingLineGraphicsComp.visible = false

			// subtracting from 90 converts the angle into anticlockwise from horizontal so we can use sin and cos
			val firingAngle = 90 + firingLineTransformComp.rotation

			ballBody.applyLinearImpulse(
				MathUtils.cosDeg(firingAngle) * BALL_MOMENTUM,
				MathUtils.sinDeg(firingAngle) * BALL_MOMENTUM,
				0f, 0f, true
			)
		}

		gameEventManager.listen(GameEvent.CatchBall) {
			paddleComp.state = PaddleComponent.State.WAITING_TO_FIRE

			ballBody.setLinearVelocity(0f, 0f)
			ballBody.angularVelocity = 0f

			ballBody.setTransform(
				paddleTransformComp.rect.x + paddleTransformComp.rect.halfWidth(),
				PaddleComponent.Y + paddleTransformComp.rect.halfHeight() + ballBody.fixtureList.first().shape.radius,
				0f
			)

			firingLineTransformComp.rect.x = paddleTransformComp.rect.x + paddleTransformComp.rect.halfWidth() - firingLineTransformComp
				.rect.halfWidth()
		}
	}
}