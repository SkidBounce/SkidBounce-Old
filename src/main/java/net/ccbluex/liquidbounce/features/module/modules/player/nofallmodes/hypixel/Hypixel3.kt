/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 *  https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.hypixel

import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.NoFallMode
import net.ccbluex.liquidbounce.utils.PacketUtils.sendPacket
import net.minecraft.network.play.client.C03PacketPlayer

/**
 * @author Aspw-w/NightX-Client
 */
object Hypixel3 : NoFallMode("Hypixel3") {
    override fun onPacket(event: PacketEvent) {
        if (event.packet is C03PacketPlayer && mc.thePlayer.fallDistance > 2.5F && mc.thePlayer.ticksExisted % 2 == 0) {
            event.packet.onGround = true
            event.packet.isMoving = false
        }
    }
}
