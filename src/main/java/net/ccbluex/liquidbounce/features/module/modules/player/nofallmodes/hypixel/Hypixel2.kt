/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 *  https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.hypixel

import net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.NoFallMode
import net.ccbluex.liquidbounce.utils.PacketUtils.sendPacket
import net.minecraft.network.play.client.C03PacketPlayer

/**
 * @author Aspw-w/NightX-Client
 */
object Hypixel2 : NoFallMode("Hypixel2") {
    var ticks = 0
    override fun onEnable() { ticks = 0 }
    override fun onUpdate() {
        if (!mc.thePlayer.onGround && mc.thePlayer.fallDistance - ticks * 2.5 >= 0.0) {
            sendPacket(C03PacketPlayer(true))
            ticks++
        } else if (mc.thePlayer.onGround) ticks = 1
    }
}
