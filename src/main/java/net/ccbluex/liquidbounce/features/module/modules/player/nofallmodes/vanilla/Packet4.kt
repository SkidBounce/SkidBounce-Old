package net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.vanilla

import net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.NoFallMode
import net.ccbluex.liquidbounce.utils.PacketUtils
import net.minecraft.network.play.client.C03PacketPlayer

object Packet4 : NoFallMode("Packet4") {
    override fun onUpdate() {
        if (mc.thePlayer.fallDistance - mc.thePlayer.motionY > 3f) {
            PacketUtils.sendPacket(C03PacketPlayer(true))
            mc.thePlayer.fallDistance = 0f
        }
    }
}