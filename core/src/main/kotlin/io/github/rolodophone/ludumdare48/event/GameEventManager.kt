package io.github.rolodophone.ludumdare48.event

import com.badlogic.gdx.utils.ObjectMap

class GameEventManager {
	private val callbacks = ObjectMap<GameEvent, MutableSet<(GameEvent) -> Unit>>()

	fun <E: GameEvent> listen(event: E, callback: (event: E) -> Unit) {
		val callbackSet = callbacks[event]

		//casting so all values are the same type
		@Suppress("UNCHECKED_CAST")
		val castedCallback = callback as (GameEvent) -> Unit

		if (callbackSet == null) {
			// no callbacks present that take that GameEvent so add a new set
			callbacks.put(event, mutableSetOf(castedCallback))
		}
		else {
			// add to the existing set of callbacks that take that GameEvent
			callbackSet.add(castedCallback)
		}
	}

	fun trigger(event: GameEvent) {
		val listenerSet = callbacks[event]

		if (listenerSet != null) {
			for (callback in callbacks[event]) {
				callback.invoke(event)
			}
		}
	}

	fun removeAllCallbacks() {
		callbacks.clear()
	}
}