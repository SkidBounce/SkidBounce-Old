/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.jesusmodes.vanilla

import net.ccbluex.liquidbounce.features.module.modules.movement.jesusmodes.JesusMode
import net.ccbluex.liquidbounce.utils.extensions.inLiquid

object Dolphin : JesusMode("Dolphin", false) {
    override fun onUpdate() {
        if (mc.thePlayer.inLiquid) mc.thePlayer.motionY += 0.03999999910593033
    }
}
