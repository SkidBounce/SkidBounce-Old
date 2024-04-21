/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.flymodes.other

import net.ccbluex.liquidbounce.event.EventState
import net.ccbluex.liquidbounce.event.events.MotionEvent
import net.ccbluex.liquidbounce.event.events.PacketEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.Fly.clip2Delay
import net.ccbluex.liquidbounce.features.module.modules.movement.Fly.clip2GroundSpoof
import net.ccbluex.liquidbounce.features.module.modules.movement.Fly.clip2GroundSpoofOnlyClip
import net.ccbluex.liquidbounce.features.module.modules.movement.Fly.clip2Timer
import net.ccbluex.liquidbounce.features.module.modules.movement.Fly.clip2H
import net.ccbluex.liquidbounce.features.module.modules.movement.Fly.clip2Max
import net.ccbluex.liquidbounce.features.module.modules.movement.Fly.clip2Y
import net.ccbluex.liquidbounce.features.module.modules.movement.flymodes.FlyMode
import net.ccbluex.liquidbounce.utils.extensions.*
import net.ccbluex.liquidbounce.utils.timing.MSTimer
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.util.Vec3
import kotlin.math.cos
import kotlin.math.sin

/**
 * @author ManInMyVan/SkidBounce
 * @author ManInMyVan
 */
object Clip2 : FlyMode("Clip2") {
    private val timer = MSTimer()
    private var clip = false

    override fun onEnable() {
        timer.reset()
    }

    override fun onMotion(event: MotionEvent) {
        if (event.eventState != EventState.POST)
            return

        mc.thePlayer.jumpMovementFactor = 0f
        mc.timer.timerSpeed = clip2Timer
        mc.thePlayer.stop()
        mc.thePlayer.isSneaking = false

        if (!timer.hasTimePassed(clip2Delay.toLong()))
            return

        val (x1, y1, z1) = mc.thePlayer
        val yaw = mc.thePlayer.rotationYaw

        // Input
        val forwards = mc.gameSettings.keyBindForward.isActuallyPressed
        val left = mc.gameSettings.keyBindLeft.isActuallyPressed
        val backwards = mc.gameSettings.keyBindBack.isActuallyPressed
        val right = mc.gameSettings.keyBindRight.isActuallyPressed
        val up = mc.gameSettings.keyBindJump.isActuallyPressed
        val down = mc.gameSettings.keyBindSneak.isActuallyPressed

        // moveYaw
        var moveYaw = yaw
        val direction = when {
            backwards && !forwards -> {
                moveYaw += 180f
                -0.5f
            }
            !backwards && forwards -> 0.5f
            else -> 1f
        }

        if (!left && right) moveYaw += 90f * direction
        if (left && !right) moveYaw -= 90f * direction
        moveYaw = moveYaw.toRadians()

        // If we didn't do this you would move if no keys
        // are pressed, or forwards and backwards are pressed
        val shouldMoveHorizontally = yaw.toRadians() != moveYaw || forwards && !backwards

        Vec3(
            if (shouldMoveHorizontally) sin(moveYaw.toDouble()) * clip2H else 0.0,
            when {
                up && !down -> clip2Y.toDouble()
                !up && down -> -clip2Y.toDouble()
                else -> 0.0
            },
            if (shouldMoveHorizontally) cos(moveYaw.toDouble()) * clip2H else 0.0
        ).run {
            (normalize() * lengthVector().coerceAtMost(clip2Max.toDouble())).apply {
                mc.thePlayer.setPosition(
                    x1 - xCoord,
                    y1 + yCoord,
                    z1 + zCoord,
                )
            }
        }

        val (x2, y2, z2) = mc.thePlayer

        if (x1 != x2 || y1 != y2 || z1 != z2)
            clip = true

        timer.reset()
    }

    override fun onPacket(event: PacketEvent) {
        if (event.packet is C03PacketPlayer && clip2GroundSpoof && (!clip2GroundSpoofOnlyClip || clip)) {
            event.packet.onGround = true
            clip = false
        }
    }
}
