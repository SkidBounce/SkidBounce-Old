/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.matrix

import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.SpeedMode
import net.ccbluex.liquidbounce.utils.MovementUtils.isMoving
import net.ccbluex.liquidbounce.utils.MovementUtils.strafe
import net.ccbluex.liquidbounce.utils.extensions.jmp

object Matrix : SpeedMode("Matrix") {
    override fun onUpdate()  {
        if (isMoving) {
            if (mc.thePlayer.isAirBorne && mc.thePlayer.fallDistance > 1.215f) {
                mc.timer.timerSpeed = 1f
                return
            }

            if (mc.thePlayer.onGround) {
                strafe()
                mc.thePlayer.jmp()
                if (mc.thePlayer.motionY > 0)
                    mc.timer.timerSpeed = 1.0953f
            } else if (mc.thePlayer.motionY < 0)
                    mc.timer.timerSpeed = 0.9185f
        } else mc.timer.timerSpeed = 1f
    }
}
