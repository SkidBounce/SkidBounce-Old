/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.spectre

import net.ccbluex.liquidbounce.event.MotionEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.SpeedMode
import net.ccbluex.liquidbounce.utils.MovementUtils.isMoving
import net.ccbluex.liquidbounce.utils.MovementUtils.strafe

object SpectreLowHop : SpeedMode("SpectreLowHop") {
    override fun onMotion(event: MotionEvent) {
        if (!isMoving || mc.thePlayer.movementInput.jump) return
        if (mc.thePlayer.onGround) {
            strafe(1.1f)
            mc.thePlayer.motionY = 0.15
            return
        }
        strafe()
    }

}
