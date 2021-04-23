package io.github.rolodophone.ludumdare48.event

sealed class GameEvent {
	object StartAiming: GameEvent()
	object ShootBall: GameEvent()
	object AdjustAimAngle: GameEvent() {
		var newAngle = 0f
	}
	object CatchBall: GameEvent()
}