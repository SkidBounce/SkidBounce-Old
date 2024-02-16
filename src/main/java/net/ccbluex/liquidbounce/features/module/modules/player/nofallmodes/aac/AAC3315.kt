/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.aac

import net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.NoFallMode
import net.ccbluex.liquidbounce.utils.PacketUtils.sendPacket
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition

/**
 * @author CCBlueX/LiquidBounce
 */
object AAC3315 : NoFallMode("AAC3.3.15") {
    override fun onUpdate() {
        mc.thePlayer ?: return

        if (mc.isIntegratedServerRunning) return

        if (mc.thePlayer.fallDistance > 2) {
            sendPacket(C04PacketPlayerPosition(mc.thePlayer.posX, Double.NaN, mc.thePlayer.posZ, false))

            mc.thePlayer.fallDistance = -9999f
        }
    }
}
