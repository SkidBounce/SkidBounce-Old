/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.event

open class Event

open class CancellableEvent : Event() {

    /**
     * Let you know if the event is cancelled
     *
     * @return state of cancel
     */
    var isCancelled = false
        private set

    /**
     * Allows you to cancel an event
     */
    fun cancelEvent() {
        isCancelled = true
    }

}

enum class EventState {
    PRE, POST, // MotionEvent
    SEND, RECEIVE // PacketEvent
}
