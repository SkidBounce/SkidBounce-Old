/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.ncp

import net.ccbluex.liquidbounce.event.MotionEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.SpeedMode
import net.ccbluex.liquidbounce.utils.MovementUtils.isMoving
import net.ccbluex.liquidbounce.utils.MovementUtils.strafe
import net.ccbluex.liquidbounce.utils.extensions.jump
import net.ccbluex.liquidbounce.utils.extensions.stopXZ

/**
 * @author ManInMyVan/SkidBounce
 * @author ManInMyVan
 */
object UNCPHop2 : SpeedMode("UNCPHop2") {
    override fun onMotion(event: MotionEvent) {
        if (!isMoving) {
            mc.timer.timerSpeed = 1f
            mc.thePlayer.stopXZ()
            return
        }

        mc.timer.timerSpeed = 1.08f
        strafe()
        mc.thePlayer.jumpMovementFactor = 0.024f
        mc.thePlayer.jump(0.39935305)
    }

    override fun onDisable() {
        mc.thePlayer.jumpMovementFactor = 0.02f
    }
}
