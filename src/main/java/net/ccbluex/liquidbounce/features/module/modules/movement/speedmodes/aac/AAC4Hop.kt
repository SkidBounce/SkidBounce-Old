/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.aac

import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.SpeedMode
import net.ccbluex.liquidbounce.utils.MovementUtils.isMoving

object AAC4Hop : SpeedMode("AAC4Hop") {
    override fun onDisable() {
        mc.timer.timerSpeed = 1f
        mc.thePlayer!!.speedInAir = 0.02f
    }
    override fun onUpdate() {
        if (mc.thePlayer!!.isInWater) return

        if (isMoving) {
            if (mc.thePlayer!!.onGround) {
                mc.thePlayer!!.jump()
                mc.thePlayer!!.speedInAir = 0.0201f
                mc.timer.timerSpeed = 0.94f
            }
            if (mc.thePlayer!!.fallDistance > 0.7 && mc.thePlayer!!.fallDistance < 1.3) {
                mc.thePlayer!!.speedInAir = 0.02f
                mc.timer.timerSpeed = 1.8f
            }
            if (mc.thePlayer!!.fallDistance >= 1.3){
                mc.timer.timerSpeed = 1f
                mc.thePlayer!!.speedInAir = 0.02f
            }
        } else {
            mc.thePlayer!!.motionX = 0.0
            mc.thePlayer!!.motionZ = 0.0
        }
    }
}
