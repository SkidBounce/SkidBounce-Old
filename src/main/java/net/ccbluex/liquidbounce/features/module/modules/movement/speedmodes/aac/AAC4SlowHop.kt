/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.aac

import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.SpeedMode
import net.ccbluex.liquidbounce.utils.MovementUtils.isMoving
import net.ccbluex.liquidbounce.utils.extensions.*

/**
 * @author liquidbounceplusreborn/LiquidbouncePlus-Reborn
 */
object AAC4SlowHop : SpeedMode("AAC4SlowHop") {
    override fun onDisable() {
        mc.timer.resetSpeed()
        mc.thePlayer.speedInAir = 0.02f
    }
    override fun onUpdate() {
        mc.thePlayer.run {
            if (isInWater)
                return

            if (!isMoving) {
                stopXZ()
                return
            }

            jmp()

            if (!onGround && fallDistance <= 0.1) {
                speedInAir = 0.02f
                mc.timer.timerSpeed = 1.4f
            }
            if (fallDistance > 0.1 && fallDistance < 1.3) {
                speedInAir = 0.0205f
                mc.timer.timerSpeed = 0.65f
            }
            if (fallDistance >= 1.3) {
                mc.timer.timerSpeed = 1f
                speedInAir = 0.02f
            }
        }
    }
}
