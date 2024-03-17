/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.ncp

import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.SpeedMode
import net.ccbluex.liquidbounce.utils.MovementUtils.isMoving
import net.ccbluex.liquidbounce.utils.MovementUtils.strafe
import net.minecraft.potion.Potion.moveSpeed

/**
 * @author Aspw-w/NightX-Client
 */
object NCPSemiStrafe : SpeedMode("NCPSemiStrafe") {
    override fun onUpdate() {
        if (isMoving) {
            if (mc.thePlayer.onGround)
                mc.thePlayer.motionY = 0.41999998688698
            else {
                strafe(if (mc.thePlayer.isPotionActive(moveSpeed)) 0.265f else 0.145f)
                mc.thePlayer.jumpMovementFactor = 0.14f // why
            }
        }
    }
}
