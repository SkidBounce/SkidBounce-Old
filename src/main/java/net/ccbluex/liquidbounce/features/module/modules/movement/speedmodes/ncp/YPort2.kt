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
import net.ccbluex.liquidbounce.utils.extensions.jmp

/**
 * @author CCBlueX/LiquidBounce
 */
object YPort2 : SpeedMode("YPort2") {
    override fun onMotion(event: MotionEvent) {
        if (mc.thePlayer.isOnLadder || mc.thePlayer.isInWater || mc.thePlayer.isInLava || mc.thePlayer.isInWeb || !isMoving)
            return
        if (mc.thePlayer.onGround) mc.thePlayer.jmp() else mc.thePlayer.motionY = -1.0
        strafe()
    }
}
