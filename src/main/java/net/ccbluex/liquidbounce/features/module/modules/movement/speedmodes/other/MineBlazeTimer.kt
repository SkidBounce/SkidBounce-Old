/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.other

import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.SpeedMode
import net.ccbluex.liquidbounce.utils.MovementUtils.isMoving
import net.ccbluex.liquidbounce.utils.extensions.jump

object MineBlazeTimer : SpeedMode("MineBlazeTimer") {
    override fun onUpdate() {
        val thePlayer = mc.thePlayer ?: return

        mc.timer.timerSpeed = 1f

        if (!isMoving || thePlayer.isInWater || thePlayer.isInLava || thePlayer.isOnLadder || thePlayer.isRiding)
            return

        if (thePlayer.onGround)
            thePlayer.jump(0.42)
        else {
            if (thePlayer.fallDistance <= 0.1)
                mc.timer.timerSpeed = 1.7f
            else if (thePlayer.fallDistance < 1.3)
                mc.timer.timerSpeed = 0.8f
            else
                mc.timer.timerSpeed = 1f
        }
    }

}
