/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat.criticalsmodes.other

import net.ccbluex.liquidbounce.event.events.PacketEvent
import net.ccbluex.liquidbounce.features.module.modules.combat.criticalsmodes.CriticalsMode
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C07PacketPlayerDigging
import net.minecraft.network.play.client.C07PacketPlayerDigging.Action.*

/**
 * @author Aspw-w/NightX-Client
 */
object Redesky : CriticalsMode("Redesky") {
    private var crit = false

    override fun onPacket(event: PacketEvent) {
        when (val packet = event.packet) {
            is C03PacketPlayer -> {
                if (mc.thePlayer.onGround && crit) {
                    packet.y += 0.000001
                    packet.onGround = false
                }
                if (mc.theWorld.getCollidingBoundingBoxes(
                        mc.thePlayer,
                        mc.thePlayer.entityBoundingBox.offset(
                            0.0,
                            (mc.thePlayer.motionY - 0.08) * 0.98,
                            0.0
                        )
                    ).isEmpty()
                ) packet.onGround = true
            }
            is C07PacketPlayerDigging -> when (packet.status) {
                START_DESTROY_BLOCK -> crit = false
                ABORT_DESTROY_BLOCK, STOP_DESTROY_BLOCK -> crit = true
                else -> {}
            }
        }
    }
}
