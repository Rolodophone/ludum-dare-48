package io.github.rolodophone.ludumdare48.event

sealed class GameEvent {
	object StartDigging: GameEvent()
	object DogHurt: GameEvent()
	object FinishDigging: GameEvent()
}