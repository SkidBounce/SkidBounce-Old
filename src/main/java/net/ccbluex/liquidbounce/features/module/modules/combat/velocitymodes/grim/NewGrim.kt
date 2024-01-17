package net.ccbluex.liquidbounce.features.module.modules.combat.velocitymodes.grim

import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.TickEvent
import net.ccbluex.liquidbounce.features.module.modules.combat.Velocity
import net.ccbluex.liquidbounce.features.module.modules.combat.velocitymodes.VelocityMode
import net.ccbluex.liquidbounce.utils.timing.MSTimer
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C07PacketPlayerDigging
import net.minecraft.network.play.server.S08PacketPlayerPosLook
import net.minecraft.network.play.server.S12PacketEntityVelocity
import net.minecraft.network.play.server.S27PacketExplosion
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing

object NewGrim : VelocityMode("NewGrim") {

    var gotVelo = false
    var flagTimer = MSTimer()
    var timerTicks = 0

    override fun onEnable() {
        gotVelo = false
        flagTimer.reset()
        timerTicks = 0
    }

    override fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (packet is S08PacketPlayerPosLook)
            flagTimer.reset()
        if (!flagTimer.hasTimePassed(Velocity.newgrimFlagPause.toLong())) {
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
            mc.timer.timerSpeed = Velocity.newgrimTimerSpeed
            --timerTicks
        }
        else if (mc.timer.timerSpeed <= 1) mc.timer.timerSpeed = 1f

        if (!flagTimer.hasTimePassed(Velocity.newgrimFlagPause.toLong())) {
            gotVelo = false
            return
        }

        val thePlayer = mc.thePlayer ?: return
        val theWorld = mc.theWorld ?: return
        if (gotVelo || Velocity.newgrimAlways) {
            val pos = BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ)
            if (checkBlock(pos) || checkBlock(pos.up())) {
                gotVelo = false
            }
        }
    }

    private fun checkBlock(pos: BlockPos): Boolean {
        if (!Velocity.newgrimOnlyAir || mc.theWorld.isAirBlock(pos)) {
            if (Velocity.newgrimSendC03) {
                timerTicks = Velocity.newgrimTimerTicks
                if (Velocity.newgrimC06)
                    mc.netHandler.addToSendQueue(
                        C03PacketPlayer.C06PacketPlayerPosLook(
                            mc.thePlayer.posX,
                            mc.thePlayer.posY,
                            mc.thePlayer.posZ,
                            mc.thePlayer.rotationYaw,
                            mc.thePlayer.rotationPitch,
                            mc.thePlayer.onGround
                        )
                    )
                else
                    mc.netHandler.addToSendQueue(C03PacketPlayer(mc.thePlayer.onGround))
            }
            mc.netHandler.addToSendQueue(
                C07PacketPlayerDigging(
                    C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK,
                    pos,
                    EnumFacing.DOWN
                )
            )
            if (Velocity.newgrimWorld)
                mc.theWorld.setBlockToAir(pos)
            return true
        }
        return false
    }
}