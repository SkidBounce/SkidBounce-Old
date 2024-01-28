/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.aac

import net.ccbluex.liquidbounce.event.MotionEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.SpeedMode
import net.ccbluex.liquidbounce.utils.MovementUtils.isMoving
import net.ccbluex.liquidbounce.utils.extensions.jump

object AACYPort2 : SpeedMode("AACYPort2") {
    override fun onMotion(event: MotionEvent) {
        if (isMoving) {
            val thePlayer = mc.thePlayer ?: return

            thePlayer.cameraPitch = 0f
            if (thePlayer.onGround) {
                thePlayer.jump(0.3851)
                thePlayer.motionX *= 1.01
                thePlayer.motionZ *= 1.01
            } else thePlayer.motionY = -0.21
        }
    }

}
