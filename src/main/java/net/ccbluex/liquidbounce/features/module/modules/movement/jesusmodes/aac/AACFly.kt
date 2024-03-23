/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.jesusmodes.aac

import net.ccbluex.liquidbounce.event.MoveEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.Jesus.aacFly
import net.ccbluex.liquidbounce.features.module.modules.movement.jesusmodes.JesusMode
import net.ccbluex.liquidbounce.utils.extensions.inLiquid

object AACFly : JesusMode("AACFly", false) {
    override fun onMove(event: MoveEvent) {
        if (mc.thePlayer.inLiquid) {
            event.y = aacFly.toDouble()
            mc.thePlayer.motionY = aacFly.toDouble()
        }
    }
}
