/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.world

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.event.WorldEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory.WORLD
import net.ccbluex.liquidbounce.utils.MovementUtils.isMoving
import net.ccbluex.liquidbounce.utils.misc.RandomUtils.nextFloat
import net.ccbluex.liquidbounce.value.BooleanValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.ListValue

object Timer : Module("Timer", WORLD, gameDetecting = false) {

    private val mode by ListValue("Mode", arrayOf("OnMove", "NoMove", "Always"), "OnMove")
    private val random by BooleanValue("Random", false)
    private val speed by FloatValue("Speed", 2f, 0.1f..10f) { !random }
    private val maxspeed: Float by object : FloatValue("MaxSpeed", 2f, 0.1f..10f) {
        override fun isSupported() = random
        override fun onChange(oldValue: Float, newValue: Float) = newValue.coerceAtLeast(minspeed)
    }
    private val minspeed by object : FloatValue("MinSpeed", 1f, 0.1f..10f) {
        override fun isSupported() = random
        override fun onChange(oldValue: Float, newValue: Float) = newValue.coerceAtMost(maxspeed)
    }

    var timer = false

    override fun onDisable() {
        if (mc.thePlayer == null)
            return

        mc.timer.timerSpeed = 1f
    }

    @Suppress("UNUSED_PARAMETER")
    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (mode == "Always" || mode == "OnMove" && isMoving || mode == "NoMove" && !isMoving) {
            timer = true
            mc.timer.timerSpeed = if (random) nextFloat(minspeed, maxspeed) else speed
        } else if (timer)
            mc.timer.timerSpeed = 1f
    }

    @EventTarget
    fun onWorld(event: WorldEvent) {
        if (event.worldClient != null)
            return

        state = false
    }

    override val tag
        get() = if (random) "$minspeed - $maxspeed" else speed.toString()
}
