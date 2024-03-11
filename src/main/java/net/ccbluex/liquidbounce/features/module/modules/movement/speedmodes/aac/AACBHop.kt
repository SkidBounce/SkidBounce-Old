/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.aac

import net.ccbluex.liquidbounce.event.MotionEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.SpeedMode
import net.ccbluex.liquidbounce.utils.MovementUtils.isMoving
import net.ccbluex.liquidbounce.utils.extensions.toRadians
import kotlin.math.cos
import kotlin.math.sin

/**
 * @author CCBlueX/LiquidBounce
 */
object AACBHop : SpeedMode("AACBHop") {
    override fun onMotion(event: MotionEvent) {
        val thePlayer = mc.thePlayer ?: return

        if (isMoving) {
            mc.timer.timerSpeed = 1.08f

            if (thePlayer.onGround) {
                thePlayer.motionY = 0.399
                val f = thePlayer.rotationYaw.toRadians()
                thePlayer.motionX -= sin(f) * 0.2f
                thePlayer.motionZ += cos(f) * 0.2f
                mc.timer.timerSpeed = 2f
            } else {
                thePlayer.motionY *= 0.97
                thePlayer.motionX *= 1.008
                thePlayer.motionZ *= 1.008
            }
        } else {
            thePlayer.motionX = 0.0
            thePlayer.motionZ = 0.0
            mc.timer.timerSpeed = 1f
        }
    }

    override fun onDisable() {
        mc.timer.timerSpeed = 1f
    }
}
