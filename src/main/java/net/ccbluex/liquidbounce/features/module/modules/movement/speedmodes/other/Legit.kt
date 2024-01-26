/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.other

import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.SpeedMode
import net.ccbluex.liquidbounce.utils.MovementUtils.isMoving
import net.ccbluex.liquidbounce.utils.extensions.jump

object Legit : SpeedMode("Legit") {
    override fun onStrafe() {
        mc.thePlayer ?: return

        if (isMoving)
            mc.thePlayer.jump(0.42)
    }

    override fun onUpdate() {
        mc.thePlayer ?: return

        mc.thePlayer.isSprinting = mc.thePlayer.movementInput.moveForward > 0.8
    }
}
