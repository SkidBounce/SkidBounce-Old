/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.aac

import net.ccbluex.liquidbounce.event.events.MotionEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.SpeedMode
import net.ccbluex.liquidbounce.utils.extensions.jmp

/**
 * @author liquidbounceplusreborn/LiquidbouncePlus-Reborn
 */
object AACv4BHop : SpeedMode("AACv4BHop") {
    override fun onMotion(event: MotionEvent) {
        mc.thePlayer.run {
            if (moveForward > 0) {
                if (onGround) {
                    jmp()
                    mc.timer.timerSpeed = 1.6105f
                    motionX *= 1.0708
                    motionZ *= 1.0708
                } else if (fallDistance > 0)
                    mc.timer.timerSpeed = 0.6f
            }
        }
    }
}
