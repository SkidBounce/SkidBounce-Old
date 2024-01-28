/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.utils.timing

class TickTimer {
    private var tick = 0
    fun update() {
        tick++
    }

    fun reset() {
        tick = 0
    }

    fun hasTimePassed(ticks: Int) = tick >= ticks
}
