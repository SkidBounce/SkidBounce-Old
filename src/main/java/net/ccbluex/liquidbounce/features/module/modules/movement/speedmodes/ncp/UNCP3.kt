/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.ncp

import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.SpeedMode
import net.ccbluex.liquidbounce.utils.MovementUtils.isMoving
import net.ccbluex.liquidbounce.utils.MovementUtils.strafe
import net.ccbluex.liquidbounce.utils.extensions.jmp
import net.minecraft.potion.Potion

/**
 * @author CCBlueX/LiquidBounce
 * @author EclipsesDev
 */
object UNCP3 : SpeedMode("UNCP3") {
    private var speed = 0.0f
    private var tick = 0

    override fun onUpdate() {
        if (isMoving) {
            if (mc.thePlayer.onGround) {
                speed = if (mc.thePlayer.isPotionActive(Potion.moveSpeed)
                    && mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).amplifier >= 1)
                    0.4563f else 0.3385f

                mc.thePlayer.jmp()
            } else speed *= 0.98f

            if (mc.thePlayer.isAirBorne && mc.thePlayer.fallDistance > 2) {
                mc.timer.timerSpeed = 1f
                return
            }

            strafe(speed, false)

            if (!mc.thePlayer.onGround && ++tick % 3 == 0) {
                mc.timer.timerSpeed = 1.0815f
                tick = 0
            } else mc.timer.timerSpeed = 0.9598f
        } else mc.timer.timerSpeed = 1f
    }
}
