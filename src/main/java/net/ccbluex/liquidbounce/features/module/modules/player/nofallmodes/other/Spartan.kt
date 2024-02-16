/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.other

import net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.NoFallMode
import net.ccbluex.liquidbounce.utils.PacketUtils.sendPackets
import net.ccbluex.liquidbounce.utils.timing.TickTimer
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition

/**
 * @author CCBlueX/LiquidBounce
 */
object Spartan : NoFallMode("Spartan") {
    private val spartanTimer = TickTimer()

    override fun onUpdate() {
        spartanTimer.update()
        if (mc.thePlayer.fallDistance > 1.5 && spartanTimer.hasTimePassed(10)) {
            sendPackets(
                C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 10, mc.thePlayer.posZ, true),
                C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - 10, mc.thePlayer.posZ, true)
            )
            spartanTimer.reset()
        }
    }
}
