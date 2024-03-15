/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.other

import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.SpeedMode
import net.ccbluex.liquidbounce.utils.MovementUtils.isMoving
import net.ccbluex.liquidbounce.utils.MovementUtils.strafe
import net.ccbluex.liquidbounce.utils.extensions.jmp

/**
 * @author CCBlueX/LiquidBounce
 */
object Hypixel : SpeedMode("Hypixel") {
    override fun onStrafe() {
        if (mc.thePlayer.onGround && isMoving) {
            if (mc.thePlayer.isUsingItem) {
                mc.thePlayer.jmp()
            } else {
                mc.thePlayer.jmp()
                strafe(0.4f)
            }
        }
    }
}
