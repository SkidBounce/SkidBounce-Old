/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.vulcan

import net.ccbluex.liquidbounce.features.module.modules.movement.Speed.vulcan2Fast
import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.SpeedMode
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.extensions.jmp

/**
 * @author CCBlueX/LiquidBounce
 * @author EclipsesDev
 */
object Vulcan2 : SpeedMode("Vulcan2") {
    override fun onUpdate() {
        if (MovementUtils.isMoving) {
            if (mc.thePlayer.onGround) {
                mc.thePlayer.jmp()
                mc.timer.timerSpeed = if (vulcan2Fast) 0.4645f else 0.45f
                MovementUtils.strafe(if (vulcan2Fast) 0.475f else 0.45f)
            } else mc.timer.timerSpeed = if (vulcan2Fast) 1.1425f else 1.125f
        } else mc.timer.timerSpeed = 1f
    }
}