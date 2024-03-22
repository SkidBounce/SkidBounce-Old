/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.vulcan

import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.SpeedMode
import net.ccbluex.liquidbounce.utils.MovementUtils.isMoving
import net.ccbluex.liquidbounce.utils.MovementUtils.speed
import net.ccbluex.liquidbounce.utils.MovementUtils.strafe
import net.ccbluex.liquidbounce.utils.extensions.jmp
import net.ccbluex.liquidbounce.utils.extensions.stopXZ

/**
 * @author SkidderMC/FDPClient
 */
object VulcanYPort : SpeedMode("VulcanYPort") {
    private var wasTimer = false
    private var ticks = 0

    override fun onUpdate() {
        ticks++

        if (wasTimer) {
            mc.timer.timerSpeed = 1f
            wasTimer = false
        }

        mc.thePlayer.jumpMovementFactor = 0.0245f

        if (!mc.thePlayer.onGround && ticks > 3 && mc.thePlayer.motionY > 0)
            mc.thePlayer.motionY = -0.27

        if (speed < 0.215f && !mc.thePlayer.onGround)
            strafe(0.215f)

        if (mc.thePlayer.onGround && isMoving) {
            ticks = 0
            mc.thePlayer.jmp()
            if (!mc.thePlayer.isAirBorne)
                return // Prevent flag with Flight
            mc.timer.timerSpeed = 1.2f
            wasTimer = true
            if (speed < 0.48f) strafe(0.48f)
            else strafe(speed * 0.985)
        } else if (!isMoving) {
            mc.timer.timerSpeed = 1f
            mc.thePlayer.stopXZ()
        }
    }
}
