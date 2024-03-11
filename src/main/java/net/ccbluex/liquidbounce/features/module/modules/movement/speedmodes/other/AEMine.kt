/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.other

import net.ccbluex.liquidbounce.event.MotionEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.SpeedMode
import net.ccbluex.liquidbounce.utils.MovementUtils.isMoving
import net.ccbluex.liquidbounce.utils.extensions.jmp

/**
 * @author Aspw-w/NightX-Client
 */
object AEMine : SpeedMode("AEMine") {
    override fun onMotion(event: MotionEvent) {
        if (mc.thePlayer.onGround && isMoving) {
            mc.thePlayer.jmp()
            mc.timer.timerSpeed = 1f
        } else
            mc.timer.timerSpeed = 1.3091955f
    }
}
