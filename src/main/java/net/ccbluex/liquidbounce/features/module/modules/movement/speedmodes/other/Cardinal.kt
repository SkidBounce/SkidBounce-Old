/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.other

import net.ccbluex.liquidbounce.event.MotionEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.IceSpeed
import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.SpeedMode
import net.ccbluex.liquidbounce.utils.MovementUtils.isMoving
import net.ccbluex.liquidbounce.utils.MovementUtils.isOnGround
import net.ccbluex.liquidbounce.utils.MovementUtils.isOnIce
import net.ccbluex.liquidbounce.utils.MovementUtils.strafe
import net.ccbluex.liquidbounce.utils.extensions.stopXZ

object Cardinal : SpeedMode("Cardinal") {

    override fun onMotion(event: MotionEvent) {
        if (mc.thePlayer.isInWater) return
        if (isMoving) {
            mc.thePlayer.jumpMovementFactor = 0.026f
            if (mc.thePlayer.onGround && !(isOnIce() && IceSpeed.handleEvents()))
                mc.thePlayer.jump()
            if (isOnGround(0.35))
                strafe()
        }
        else mc.thePlayer.stopXZ()
    }

    override fun onDisable() {
        mc.thePlayer.jumpMovementFactor = 0.02f
    }
}
