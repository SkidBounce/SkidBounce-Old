/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.vulcan

import net.ccbluex.liquidbounce.event.EventState
import net.ccbluex.liquidbounce.event.MotionEvent
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.modules.player.NoFall.vulcan2Motion
import net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.NoFallMode
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.util.AxisAlignedBB

/**
 * @author ManInMyVan/SkidBounce
 * @author ManInMyVan
 */
object Vulcan2 : NoFallMode("Vulcan2") {
    private var lag = false
    private var modify = false
    private val packets = mutableListOf<C03PacketPlayer>()
    override fun onEnable() {
        packets.clear()
        modify = false
        lag = false
    }

    override fun onUpdate() {
        if (mc.thePlayer.motionY <= 0.0 && mc.thePlayer.fallDistance <= 1f && lag)
            mc.thePlayer.motionY = -vulcan2Motion.toDouble()
    }

    override fun onPacket(event: PacketEvent) {
        if (event.packet is C03PacketPlayer && lag) {
            event.cancelEvent()
            if (modify) {
                event.packet.onGround = true
                modify = false
            }
            packets.add(event.packet)
        }
    }

    override fun onMotion(event: MotionEvent) {
        if (event.eventState == EventState.PRE) {
            if (MovementUtils.aboveVoid) {
                if (lag) {
                    lag = false
                    if (packets.size > 0) {
                        for (packet in packets) {
                            mc.thePlayer.sendQueue.addToSendQueue(packet)
                        }
                        packets.clear()
                    }
                }
                return
            }
            if (mc.thePlayer.onGround && lag) {
                lag = false
                if (packets.size > 0) {
                    for (packet in packets) {
                        mc.thePlayer.sendQueue.addToSendQueue(packet)
                    }
                    packets.clear()
                }
                return
            }
            if (mc.thePlayer.fallDistance > 2.5 && lag) {
                modify = true
                mc.thePlayer.fallDistance = 0f
            }
            if (inAir(4.0, 1.0)) {
                return
            }
            if (!lag) {
                lag = true
            }
        }
    }

    private fun inAir(height: Double, plus: Double): Boolean {
        if (mc.thePlayer.posY < 0) return false
        var off = 0
        while (off < height) {
            val bb = AxisAlignedBB(
                mc.thePlayer.posX,
                mc.thePlayer.posY,
                mc.thePlayer.posZ,
                mc.thePlayer.posX,
                mc.thePlayer.posY - off,
                mc.thePlayer.posZ
            )
            if (mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, bb).isNotEmpty()) {
                return true
            }
            off += plus.toInt()
        }
        return false
    }
}
