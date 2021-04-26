package io.github.rolodophone.ludumdare48.event

sealed class GameEvent {
	object StartDigging: GameEvent()
	object FinishDigging: GameEvent()
	object DogRest: GameEvent()
	object ShowDialog: GameEvent() {
		var message = listOf<String>()
		var actionText = ""
		var effect = {}
		var gameOver = false
	}
	object DialogActionable: GameEvent()
	object ShowActionText: GameEvent()
	object CloseDialog: GameEvent()
	object Cutscene: GameEvent() {
		var id = 0
	}
	object GameOver: GameEvent()
	object GameCompleted: GameEvent()
	object StartGame: GameEvent()
	object EndCutSceneFinished: GameEvent()
	object ViewportResized: GameEvent()
	object DescendLevel: GameEvent()
}