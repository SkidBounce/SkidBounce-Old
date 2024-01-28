/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.aac

import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.NoFallMode
import net.ccbluex.liquidbounce.utils.PacketUtils.sendPackets
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition

object AAC504 : NoFallMode("AAC5.0.4") {
    private var isDmgFalling = false
    override fun onUpdate() {
        if (mc.thePlayer.fallDistance > 3) {
            isDmgFalling = true
        }
    }

    override fun onPacket(event: PacketEvent) {
        if (event.packet is C03PacketPlayer && isDmgFalling && event.packet.onGround && mc.thePlayer.onGround) {
            isDmgFalling = false
            event.packet.onGround = true
            mc.thePlayer.onGround = false
            event.packet.y += 1.0
            sendPackets(
                C04PacketPlayerPosition(
                    event.packet.x,
                    event.packet.y - 1.0784,
                    event.packet.z,
                    false
                ),
                C04PacketPlayerPosition(
                    event.packet.x,
                    event.packet.y - 0.5,
                    event.packet.z,
                    true
                ),
                triggerEvents = false
            )
        }
    }
}
