/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.ncp

import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.SpeedMode
import net.ccbluex.liquidbounce.utils.MovementUtils.isMoving
import net.ccbluex.liquidbounce.utils.MovementUtils.speed
import net.ccbluex.liquidbounce.utils.MovementUtils.strafe
import net.ccbluex.liquidbounce.utils.extensions.jmp
import net.ccbluex.liquidbounce.utils.extensions.stopXZ
import net.minecraft.potion.Potion.moveSpeed

/**
 * @author SkidderMC/FDPClient
 */
object NCPLatest : SpeedMode("NCPLatest") {
    private var wasSlow = false
    override fun onUpdate() {
        mc.timer.timerSpeed = if (mc.thePlayer.ticksExisted % 20 <= 9)
            1.05f else 0.98f

        if (!isMoving) {
            mc.thePlayer.stopXZ()
            wasSlow = true
            return
        }

        if (mc.thePlayer.onGround) {
            wasSlow = false
            mc.thePlayer.jmp()
            strafe(0.48f)
            if (mc.thePlayer.isPotionActive(moveSpeed)) {
                strafe(0.48f * (1.0f + 0.13f * (mc.thePlayer.getActivePotionEffect(moveSpeed).amplifier + 1)))
            }
        }

        speed *= 1.007f

        if (speed < 0.277)
            wasSlow = true

        if (wasSlow)
            strafe(0.277f)
    }
}
