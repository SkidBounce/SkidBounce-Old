/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.aac

import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.SpeedMode
import net.ccbluex.liquidbounce.utils.MovementUtils.isMoving
import net.ccbluex.liquidbounce.utils.extensions.jmp

/**
 * @author CCBlueX/LiquidBounce
 */
object AAC5 : SpeedMode("AAC5") {
    override fun onUpdate() {
        if (!isMoving)
            return

        if (mc.thePlayer.onGround) {
            mc.thePlayer.jmp()
            mc.timer.timerSpeed = 0.9385f
            mc.thePlayer.speedInAir = 0.0201f
        }

        if (mc.thePlayer.fallDistance < 2.5) {
            when {
                mc.thePlayer.fallDistance <= 0.7 -> {}
                mc.thePlayer.ticksExisted % 3 == 0 -> mc.timer.timerSpeed = 1.925f
                mc.thePlayer.fallDistance < 1.25 -> mc.timer.timerSpeed = 1.7975f
            }

            mc.thePlayer.speedInAir = 0.02f
        }
    }
}
