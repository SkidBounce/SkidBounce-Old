/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.flymodes.other

import net.ccbluex.liquidbounce.event.EventState
import net.ccbluex.liquidbounce.event.events.MotionEvent
import net.ccbluex.liquidbounce.event.events.PacketEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.Fly.clipDelay
import net.ccbluex.liquidbounce.features.module.modules.movement.Fly.clipGround
import net.ccbluex.liquidbounce.features.module.modules.movement.Fly.clipGroundSpoof
import net.ccbluex.liquidbounce.features.module.modules.movement.Fly.clipMotionX
import net.ccbluex.liquidbounce.features.module.modules.movement.Fly.clipMotionY
import net.ccbluex.liquidbounce.features.module.modules.movement.Fly.clipMotionZ
import net.ccbluex.liquidbounce.features.module.modules.movement.Fly.clipTimer
import net.ccbluex.liquidbounce.features.module.modules.movement.Fly.clipX
import net.ccbluex.liquidbounce.features.module.modules.movement.Fly.clipY
import net.ccbluex.liquidbounce.features.module.modules.movement.Fly.clipZ
import net.ccbluex.liquidbounce.features.module.modules.movement.flymodes.FlyMode
import net.ccbluex.liquidbounce.utils.timing.MSTimer
import net.minecraft.network.play.client.C03PacketPlayer
import kotlin.math.cos
import kotlin.math.sin

/**
 * @author SkidderMC/FDPClient
 */
object Clip : FlyMode("Clip") {
    private val timer = MSTimer()
    private var lastJump = false

    override fun onEnable() {
        timer.reset()
        lastJump = false
    }

    override fun onMotion(event: MotionEvent) {
        if (event.eventState != EventState.POST)
            return

        mc.timer.timerSpeed = clipTimer
        mc.thePlayer.motionX = clipMotionX.toDouble()
        mc.thePlayer.motionY = clipMotionY.toDouble()
        mc.thePlayer.motionZ = clipMotionZ.toDouble()
        if (timer.hasTimePassed(clipDelay.toLong())) {
            val yaw = Math.toRadians(mc.thePlayer.rotationYaw.toDouble())
            val pitch = Math.toRadians(mc.thePlayer.rotationPitch.toDouble())
            mc.thePlayer.setPosition(
                mc.thePlayer.posX - sin(yaw) * clipX,
                mc.thePlayer.posY - pitch * clipY,
                mc.thePlayer.posZ + cos(yaw) * clipZ
            )
            timer.reset()
            lastJump = true
        }
        mc.thePlayer.jumpMovementFactor = 0f
    }

    override fun onPacket(event: PacketEvent) {
        val packet = event.packet

        if (packet is C03PacketPlayer) {
            if (clipGroundSpoof)
                packet.onGround = true
            if (clipGround && (timer.hasTimePassed(clipDelay.toLong()) || lastJump)) {
                packet.onGround = true
                lastJump = false
            }
        }
    }
}
