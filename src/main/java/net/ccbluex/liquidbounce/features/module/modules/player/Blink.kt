/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.player

import net.ccbluex.liquidbounce.event.EventState.POST
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.events.MotionEvent
import net.ccbluex.liquidbounce.event.events.PacketEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory.PLAYER
import net.ccbluex.liquidbounce.utils.blink.BlinkHandler
import net.ccbluex.liquidbounce.utils.blink.IBlink
import net.ccbluex.liquidbounce.utils.timing.MSTimer
import net.ccbluex.liquidbounce.value.BooleanValue
import net.ccbluex.liquidbounce.value.IntValue
import net.ccbluex.liquidbounce.value.ListValue

object Blink : Module("Blink", PLAYER, gameDetecting = false), IBlink {

    private val mode by ListValue("Mode", arrayOf("Sent", "Received", "Both"), "Sent")

    private val pulse by BooleanValue("Pulse", false)
    private val pulseDelay by IntValue("PulseDelay", 1000, 500..5000) { pulse }

    val fakePlayerMenu by BooleanValue("FakePlayer", true)

    private val pulseTimer = MSTimer()

    override fun onEnable() {
        pulseTimer.reset()
    }

    override fun onDisable() {
        blinkingClient = false
        blinkingServer = false
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        blinkingClient = mode == "Sent" || mode == "Both"
        blinkingServer = mode == "Received" || mode == "Both"
    }

    @EventTarget
    fun onMotion(event: MotionEvent) {
        if (event.eventState == POST) {
            val thePlayer = mc.thePlayer ?: return

            if (thePlayer.isDead || mc.thePlayer.ticksExisted <= 10) {
                release(overrideBlink = true)
            }

            if (pulse && pulseTimer.hasTimePassed(pulseDelay)) {
                release(overrideBlink = true)
                pulseTimer.reset()
            }
        }
    }

    override val tag
        get() = (BlinkHandler.packets.size).toString()

    fun blinkingSend() = handleEvents() && (mode == "Sent" || mode == "Both")
    fun blinkingReceive() = handleEvents() && (mode == "Received" || mode == "Both")
}
