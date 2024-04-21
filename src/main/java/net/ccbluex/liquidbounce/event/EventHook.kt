/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.event

import java.lang.reflect.Method

internal class EventHook(val eventClass: Listenable, val method: Method, eventTarget: EventTarget) {
    val isIgnoreCondition = eventTarget.ignoreCondition
    val priority = eventTarget.priority
}
