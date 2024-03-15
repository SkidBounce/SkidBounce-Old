/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.other

import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.SpeedMode
import net.ccbluex.liquidbounce.utils.MovementUtils.isMoving
import net.ccbluex.liquidbounce.utils.extensions.jmp

/**
 * @author CCBlueX/LiquidBounce
 */
object MineBlaze : SpeedMode("MineBlaze") {
    override fun onUpdate() {
        mc.thePlayer ?: return
        mc.thePlayer.run {
            if (onGround && isMoving)
                jmp()
            if (motionY > 0.003) {
                motionX *= 1.0015
                motionZ *= 1.0015
                mc.timer.timerSpeed = 1.06f
            }
        }
    }
}
