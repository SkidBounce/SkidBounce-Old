/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.intave

import net.ccbluex.liquidbounce.features.module.modules.movement.Speed.intave2GroundStrafe
import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.SpeedMode
import net.ccbluex.liquidbounce.utils.MovementUtils.isMoving
import net.ccbluex.liquidbounce.utils.MovementUtils.strafe
import net.ccbluex.liquidbounce.utils.extensions.jmp

object Intave2 : SpeedMode("Intave2") {
    override fun onUpdate() {
        if (isMoving) {
            if (mc.thePlayer.onGround) {
                mc.timer.timerSpeed = 1f
                if (intave2GroundStrafe) strafe()
                mc.thePlayer.jmp()
            }
            if (mc.thePlayer.motionY > 0.003) {
                mc.thePlayer.motionX *= 1.0015
                mc.thePlayer.motionZ *= 1.0015
                mc.timer.timerSpeed = 1.06f
            }
        }
    }
}
