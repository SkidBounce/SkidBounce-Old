/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat.velocitymodes.grim

import net.ccbluex.liquidbounce.event.events.PacketEvent
import net.ccbluex.liquidbounce.event.events.TickEvent
import net.ccbluex.liquidbounce.features.module.modules.combat.Velocity.grimAlways
import net.ccbluex.liquidbounce.features.module.modules.combat.Velocity.grimFlagPause
import net.ccbluex.liquidbounce.features.module.modules.combat.Velocity.grimOnlyAir
import net.ccbluex.liquidbounce.features.module.modules.combat.Velocity.grimPacket
import net.ccbluex.liquidbounce.features.module.modules.combat.Velocity.grimTimerMode
import net.ccbluex.liquidbounce.features.module.modules.combat.Velocity.grimTimerSpeed
import net.ccbluex.liquidbounce.features.module.modules.combat.Velocity.grimTimerTicks
import net.ccbluex.liquidbounce.features.module.modules.combat.Velocity.grimWorld
import net.ccbluex.liquidbounce.features.module.modules.combat.velocitymodes.VelocityMode
import net.ccbluex.liquidbounce.utils.PacketUtils.sendPacket
import net.ccbluex.liquidbounce.utils.extensions.component1
import net.ccbluex.liquidbounce.utils.extensions.component2
import net.ccbluex.liquidbounce.utils.extensions.component3
import net.ccbluex.liquidbounce.utils.extensions.resetSpeed
import net.ccbluex.liquidbounce.utils.timing.MSTimer
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C03PacketPlayer.*
import net.minecraft.network.play.client.C07PacketPlayerDigging
import net.minecraft.network.play.client.C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK
import net.minecraft.network.play.server.S08PacketPlayerPosLook
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing.DOWN

/**
 * @author SkidderMC/FDPClient
 * @author ManInMyVan/SkidBounce
 * @author ManInMyVan
 */
object Grim : VelocityMode("Grim") {
    private var gotVelo = false
    private var flagTimer = MSTimer()
    private var timerTicks = 0

    override fun onEnable() {
        gotVelo = false
        flagTimer.reset()
        timerTicks = 0
    }

    override fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (packet is S08PacketPlayerPosLook)
            flagTimer.reset()
        if (!flagTimer.hasTimePassed(grimFlagPause.toLong())) {
            gotVelo = false
            return
        }
    }

    override fun onVelocityPacket(event: PacketEvent) {
        event.cancelEvent()
        gotVelo = true
    }

    override fun onTick(event: TickEvent) {
        if (timerTicks > 0 && mc.timer.timerSpeed <= 1) {
            val speed = if (grimTimerMode == "Old")
                grimTimerSpeed else
                grimTimerSpeed + ((1 - grimTimerSpeed) * (grimTimerTicks - timerTicks) / grimTimerTicks)
            mc.timer.timerSpeed = speed.coerceIn(0.001f..1f)

            --timerTicks
        } else if (mc.timer.timerSpeed <= 1) mc.timer.resetSpeed()

        if (!flagTimer.hasTimePassed(grimFlagPause.toLong())) {
            gotVelo = false
            return
        }

        mc.thePlayer ?: return
        mc.theWorld ?: return

        if (gotVelo || grimAlways) {
            val pos = BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ)
            if (checkBlock(pos) || checkBlock(pos.up())) {
                gotVelo = false
            }
        }
    }

    private fun checkBlock(pos: BlockPos): Boolean {
        if (grimOnlyAir && !mc.theWorld.isAirBlock(pos))
            return false

        if (grimPacket != "None") {
            timerTicks = grimTimerTicks

            val (x, y, z) = mc.thePlayer.positionVector
            val yaw = mc.thePlayer.rotationYaw
            val pitch = mc.thePlayer.rotationPitch
            val ground = mc.thePlayer.onGround

            when (grimPacket) {
                "Flying" -> sendPacket(C03PacketPlayer(ground))
                "Position" -> sendPacket(C04PacketPlayerPosition(x, y, z, ground))
                "Rotation" -> sendPacket(C05PacketPlayerLook(yaw, pitch, ground))
                "Full" -> sendPacket(C06PacketPlayerPosLook(x, y, z, yaw, pitch, ground))
                "Tick" -> mc.runGameLoop()
            }
        }

        sendPacket(
            C07PacketPlayerDigging(
                STOP_DESTROY_BLOCK,
                pos,
                DOWN
            )
        )

        if (grimWorld)
            mc.theWorld.setBlockToAir(pos)

        return true
    }
}
