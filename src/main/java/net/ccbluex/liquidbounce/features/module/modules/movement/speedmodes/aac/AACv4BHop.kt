/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.aac

import net.ccbluex.liquidbounce.event.MotionEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.SpeedMode
import net.ccbluex.liquidbounce.utils.extensions.jump

object AACv4BHop : SpeedMode("AACv4BHop") {
    override fun onMotion(event: MotionEvent) {
        if (mc.thePlayer.isInWater) return
        if (mc.thePlayer.moveForward > 0) {
            if (mc.thePlayer.onGround) {
                mc.thePlayer.jump(0.42)
                mc.timer.timerSpeed = 1.6105f
                mc.thePlayer.motionX *= 1.0708
                mc.thePlayer.motionZ *= 1.0708
            } else if (mc.thePlayer.fallDistance > 0) {
                mc.timer.timerSpeed = 0.6f
            }
        }
    }
}
