/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.aac

import net.ccbluex.liquidbounce.event.MotionEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.SpeedMode
import net.ccbluex.liquidbounce.utils.MovementUtils.isMoving
import net.ccbluex.liquidbounce.utils.MovementUtils.strafe
import net.ccbluex.liquidbounce.utils.extensions.jump

object AACLowHop : SpeedMode("AACLowHop") {
    private var legitJump = false
    override fun onEnable() {
        legitJump = true
        super.onEnable()
    }

    override fun onMotion(event: MotionEvent) {
        val thePlayer = mc.thePlayer ?: return

        if (isMoving) {
            if (thePlayer.onGround) {
                if (legitJump) {
                    thePlayer.jump(0.42)
                    legitJump = false
                    return
                }
                thePlayer.motionY = 0.343
                strafe(0.534f)
            }
        } else {
            legitJump = true
            thePlayer.motionX = 0.0
            thePlayer.motionZ = 0.0
        }
    }

}