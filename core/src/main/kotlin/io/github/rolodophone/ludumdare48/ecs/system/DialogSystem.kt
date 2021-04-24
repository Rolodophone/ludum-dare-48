package io.github.rolodophone.ludumdare48.ecs.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.EntitySystem
import io.github.rolodophone.ludumdare48.event.GameEvent
import io.github.rolodophone.ludumdare48.event.GameEventManager

class DialogSystem(private val gameEventManager: GameEventManager): EntitySystem() {
	override fun addedToEngine(engine: Engine?) {
		gameEventManager.listen(GameEvent.ShowDialog) { event ->

		}
	}
}
