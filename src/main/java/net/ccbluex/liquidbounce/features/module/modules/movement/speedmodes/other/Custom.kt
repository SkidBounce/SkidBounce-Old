/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.other

import net.ccbluex.liquidbounce.event.events.MotionEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.Speed
import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.SpeedMode
import net.ccbluex.liquidbounce.utils.MovementUtils.isMoving
import net.ccbluex.liquidbounce.utils.MovementUtils.strafe

/**
 * @author CCBlueX/LiquidBounce
 */
object Custom : SpeedMode("Custom") {
    override fun onMotion(event: MotionEvent) {
        if (isMoving) {
            mc.timer.timerSpeed = Speed.customTimer
            when {
                mc.thePlayer.onGround -> {
                    strafe(Speed.customSpeed)
                    mc.thePlayer.motionY = Speed.customY.toDouble()
                }

                Speed.customStrafe -> strafe(Speed.customSpeed)
                else -> strafe()
            }
        } else {
            mc.thePlayer.motionZ = 0.0
            mc.thePlayer.motionX = mc.thePlayer.motionZ
        }
    }
    override fun onEnable() {
        if (Speed.resetXZ) {
            mc.thePlayer.motionZ = 0.0
            mc.thePlayer.motionX = mc.thePlayer.motionZ
        }
        if (Speed.resetY) mc.thePlayer.motionY = 0.0
        super.onEnable()
    }
}
