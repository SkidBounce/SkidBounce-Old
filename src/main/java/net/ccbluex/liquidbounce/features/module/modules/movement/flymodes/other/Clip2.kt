/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.flymodes.other

import net.ccbluex.liquidbounce.event.EventState
import net.ccbluex.liquidbounce.event.MotionEvent
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.Fly.clip2Delay
import net.ccbluex.liquidbounce.features.module.modules.movement.Fly.clip2GroundSpoof
import net.ccbluex.liquidbounce.features.module.modules.movement.Fly.clip2Timer
import net.ccbluex.liquidbounce.features.module.modules.movement.Fly.clip2H
import net.ccbluex.liquidbounce.features.module.modules.movement.Fly.clip2Y
import net.ccbluex.liquidbounce.features.module.modules.movement.flymodes.FlyMode
import net.ccbluex.liquidbounce.utils.extensions.*
import net.ccbluex.liquidbounce.utils.timing.MSTimer
import net.minecraft.network.play.client.C03PacketPlayer
import kotlin.math.cos
import kotlin.math.sin

/**
 * @author ManInMyVan/SkidBounce
 * @author ManInMyVan
 */
object Clip2 : FlyMode("Clip2") {
    private val timer = MSTimer()

    override fun onEnable() {
        timer.reset()
    }

    override fun onMotion(event: MotionEvent) {
        if (event.eventState != EventState.POST)
            return

        mc.timer.timerSpeed = clip2Timer
        mc.thePlayer.stop()

        if (timer.hasTimePassed(clip2Delay.toLong())) {
            val (x1, y1, z1) = mc.thePlayer
            val yaw = yaw

            val noMove = mc.thePlayer.rotationYaw.toRadians() == yaw
                    && (!mc.gameSettings.keyBindForward.isActuallyPressed || (
                    mc.gameSettings.keyBindForward.isActuallyPressed && mc.gameSettings.keyBindBack.isActuallyPressed
                            ))

            var vertical = 0f
            if (mc.gameSettings.keyBindJump.isActuallyPressed) vertical += clip2Y
            if (mc.gameSettings.keyBindSneak.isActuallyPressed) vertical -= clip2Y

            mc.thePlayer.setPosition(
                x1 - if (noMove) 0f else sin(yaw) * clip2H,
                y1 + vertical,
                z1 + if (noMove) 0f else cos(yaw) * clip2H
            )

            val (x2, y2, z2) = mc.thePlayer
            if (x1 != x2 || y1 != y2 || z1 != z2)
                timer.reset()
        }
        mc.thePlayer.jumpMovementFactor = 0f
    }

    override fun onPacket(event: PacketEvent) {
        if (event.packet is C03PacketPlayer && clip2GroundSpoof)
            event.packet.onGround = true
    }

    val yaw: Float
        get() {
            val left = mc.gameSettings.keyBindLeft.isActuallyPressed
            val right = mc.gameSettings.keyBindRight.isActuallyPressed
            val back = mc.gameSettings.keyBindBack.isActuallyPressed
            val forward = mc.gameSettings.keyBindForward.isActuallyPressed

            var yaw = mc.thePlayer.rotationYaw

            val f = when {
                back && !forward -> {
                    yaw += 180f
                    -0.5f
                }
                !back && forward -> 0.5f
                else -> 1f
            }

            if (!(right && left)) {
                if (right) yaw += 90f * f
                if (left) yaw -= 90f * f
            }

            return yaw.toRadians()
        }
}
