/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.vanilla

import net.ccbluex.liquidbounce.features.module.modules.player.NoFall.motionMotion
import net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.NoFallMode

/**
 * @author SkidderMC/FDPClient
 */
object Motion : NoFallMode("Motion") {
    override fun onUpdate() {
        if (mc.thePlayer.fallDistance > 3)
            mc.thePlayer.motionY = motionMotion.toDouble()
    }
}
