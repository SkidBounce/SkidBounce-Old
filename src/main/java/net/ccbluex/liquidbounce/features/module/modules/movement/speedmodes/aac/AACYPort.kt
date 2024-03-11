/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.aac

import net.ccbluex.liquidbounce.event.MotionEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.SpeedMode
import net.ccbluex.liquidbounce.utils.MovementUtils.isMoving

/**
 * @author CCBlueX/LiquidBounce
 */
object AACYPort : SpeedMode("AACYPort") {
    override fun onMotion(event: MotionEvent) {
        val thePlayer = mc.thePlayer ?: return

        if (isMoving) {
            thePlayer.cameraPitch = 0f

            if (thePlayer.onGround) {
                thePlayer.motionY = 0.3425
                thePlayer.motionX *= 1.5893
                thePlayer.motionZ *= 1.5893
            } else
                thePlayer.motionY = -0.19
        }
    }

}
