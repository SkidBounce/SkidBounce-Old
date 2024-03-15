/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.vulcan

import net.ccbluex.liquidbounce.features.module.modules.movement.Speed.vulcanFast
import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.SpeedMode
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.extensions.jmp

/**
 * @author CCBlueX/LiquidBounce
 * @author EclipsesDev
 */
object Vulcan : SpeedMode("Vulcan") {
    override fun onUpdate() {
        if (MovementUtils.isMoving) {
            if (mc.thePlayer.isAirBorne && mc.thePlayer.fallDistance > 2) {
                mc.timer.timerSpeed = 1f
                return
            }

            if (mc.thePlayer.onGround) {
                mc.thePlayer.jmp()
                if (mc.thePlayer.motionY > 0)
                    mc.timer.timerSpeed = if (vulcanFast) 1.1453f else 1.1253f
                MovementUtils.strafe(0.4815f)
            } else if (mc.thePlayer.motionY < 0)
                mc.timer.timerSpeed = if (vulcanFast) 0.9185f else 0.8935f
        } else mc.timer.timerSpeed = 1f
    }
}