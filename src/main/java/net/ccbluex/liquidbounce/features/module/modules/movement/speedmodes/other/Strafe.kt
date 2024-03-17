/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.other

import net.ccbluex.liquidbounce.features.module.modules.movement.Speed.strafeAir
import net.ccbluex.liquidbounce.features.module.modules.movement.Speed.strafeGround
import net.ccbluex.liquidbounce.features.module.modules.movement.Speed.strafeStop
import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.SpeedMode
import net.ccbluex.liquidbounce.utils.MovementUtils.isMoving
import net.ccbluex.liquidbounce.utils.MovementUtils.strafe
import net.ccbluex.liquidbounce.utils.extensions.jmp

/**
 * @author ManInMyVan/SkidBounce
 * @author ManInMyVan
 */
object Strafe : SpeedMode("Strafe") {
    override fun onStrafe() {
        mc.thePlayer ?: return

        if (isMoving)
            mc.thePlayer.jmp()

        strafe(strength = if (mc.thePlayer.onGround) strafeGround else strafeAir, stopWhenNoInput = strafeStop)
    }
}
