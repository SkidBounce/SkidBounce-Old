/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.other

import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.SpeedMode
import net.ccbluex.liquidbounce.utils.MovementUtils.isMoving
import net.ccbluex.liquidbounce.utils.MovementUtils.strafe

/**
 * @author CCBlueX/LiquidBounce
 */
object Hive : SpeedMode("Hive") {
    override fun onEnable() {
        mc.thePlayer.speedInAir = 0.0425f
        mc.timer.timerSpeed = 1.04f
    }

    override fun onUpdate() {
        if (isMoving) {
            if (mc.thePlayer.onGround) mc.thePlayer.motionY = 0.3
            mc.thePlayer.speedInAir = 0.0425f
            mc.timer.timerSpeed = 1.04f
            strafe()
        } else {
            mc.thePlayer.motionX = 0.0
            mc.thePlayer.motionZ = 0.0
            mc.thePlayer.speedInAir = 0.02f
            mc.timer.timerSpeed = 1f
        }
    }
}
