/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.other

import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.SpeedMode
import net.ccbluex.liquidbounce.utils.MovementUtils.isMoving
import net.ccbluex.liquidbounce.utils.MovementUtils.strafe
import net.ccbluex.liquidbounce.utils.extensions.jump

object HypixelHop : SpeedMode("HypixelHop") {
    override fun onStrafe() {
        if (mc.thePlayer.isInWater || mc.thePlayer.isInLava)
            return

        if (mc.thePlayer.onGround && isMoving) {
            if (mc.thePlayer.isUsingItem) {
                mc.thePlayer.jump(0.42)
            } else {
                mc.thePlayer.jump(0.42)
                strafe(0.4f)
            }
        }

    }
}
