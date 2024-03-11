/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.other

import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.SpeedMode
import net.ccbluex.liquidbounce.utils.MovementUtils.isMoving
import net.ccbluex.liquidbounce.utils.MovementUtils.strafe
import net.ccbluex.liquidbounce.utils.extensions.jmp

/**
 * @author CCBlueX/LiquidBounce
 * @author EclipsesDev
 */
object VulcanHop2 : SpeedMode("VulcanHop2") {
    override fun onUpdate() {
        if (isMoving) {
            if (mc.thePlayer.isAirBorne && mc.thePlayer.fallDistance > 2) {
                mc.timer.timerSpeed = 1f
                return
            }

            if (mc.thePlayer.onGround) {
                mc.thePlayer.jmp()
                if (mc.thePlayer.motionY > 0)
                    mc.timer.timerSpeed = 1.1253f
                strafe(0.4815f)
            } else if (mc.thePlayer.motionY < 0)
                mc.timer.timerSpeed = 0.8935f
        } else mc.timer.timerSpeed = 1f
    }

    override fun onDisable() {
        mc.timer.timerSpeed = 1f
    }
}
