package io.github.rolodophone.ludumdare48.event

sealed class GameEvent {
	object StartDigging: GameEvent()
	object FinishDigging: GameEvent()
	object DogRest: GameEvent()
	object ShowDialog: GameEvent() {
		var message = listOf<String>()
		var actionText = ""
		var effect = {}
	}
	object CloseDialog: GameEvent()
}