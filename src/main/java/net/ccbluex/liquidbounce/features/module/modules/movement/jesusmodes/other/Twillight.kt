/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.jesusmodes.other

import net.ccbluex.liquidbounce.event.events.MoveEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.jesusmodes.JesusMode
import net.ccbluex.liquidbounce.utils.MovementUtils.strafe
import net.ccbluex.liquidbounce.utils.extensions.inLiquid

object Twillight : JesusMode("Twillight", false) {
    override fun onUpdate() {
        if (mc.thePlayer.inLiquid) {
            mc.thePlayer.motionX *= 1.04
            mc.thePlayer.motionZ *= 1.04
            strafe()
        }
    }

    override fun onMove(event: MoveEvent) {
        if (mc.thePlayer.inLiquid) {
            event.y = 0.01
            mc.thePlayer.motionY = 0.01
        }
    }
}
