/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.aac

import net.ccbluex.liquidbounce.event.EventState
import net.ccbluex.liquidbounce.event.events.MotionEvent
import net.ccbluex.liquidbounce.event.events.PacketEvent
import net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.NoFallMode
import net.ccbluex.liquidbounce.utils.MovementUtils.aboveVoid
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.util.AxisAlignedBB

/**
 * @author SkidderMC/FDPClient
 */
object AAC4 : NoFallMode("AAC4") {
    private var blink = false
    private var modify = false
    private val packets = mutableListOf<C03PacketPlayer>()
    override fun onEnable() {
        packets.clear()
        modify = false
        blink = false
    }

    override fun onPacket(event: PacketEvent) {
        if (event.packet is C03PacketPlayer && blink) {
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
            if ((mc.thePlayer.onGround || aboveVoid) && blink) {
                blink = false
                if (packets.size > 0) {
                    for (packet in packets) {
                        mc.thePlayer.sendQueue.addToSendQueue(packet)
                    }
                    packets.clear()
                }
                return
            }
            if (mc.thePlayer.fallDistance > 2.5 && blink) {
                modify = true
                mc.thePlayer.fallDistance = 0f
            }
            if (!inAir())
                blink = true
        }
    }

    private fun inAir(): Boolean {
        if (mc.thePlayer.posY < 0) return false
        var off = 0
        while (off < 4) {
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
            off += 1
        }
        return false
    }
}
