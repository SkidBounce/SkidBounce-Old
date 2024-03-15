/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.aac

import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.SpeedMode
import net.ccbluex.liquidbounce.utils.MovementUtils.isMoving
import net.ccbluex.liquidbounce.utils.MovementUtils.strafe

/**
 * @author CCBlueX/LiquidBounce
 */
object AAC6BHop : SpeedMode("AAC6BHop") {
    private var legitJump = false
    override fun onUpdate() {
        val thePlayer = mc.thePlayer ?: return

        mc.timer.timerSpeed = 1f

        if (isMoving) {
            if (thePlayer.onGround) {
                if (legitJump) {
                    thePlayer.motionY = 0.4
                    strafe(0.15f)
                    thePlayer.onGround = false
                    legitJump = false

                    return
                }
                thePlayer.motionY = 0.41
                strafe(0.47458485f)
            }

            if (thePlayer.motionY < 0 && thePlayer.motionY > -0.2)
                mc.timer.timerSpeed = (1.2 + thePlayer.motionY).toFloat()

            thePlayer.speedInAir = 0.022151f
        } else {
            legitJump = true
            thePlayer.motionX = 0.0
            thePlayer.motionZ = 0.0
        }
    }

    override fun onEnable() {
        legitJump = true
    }
}
