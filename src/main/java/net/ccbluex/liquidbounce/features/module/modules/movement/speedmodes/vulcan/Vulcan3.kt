/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.vulcan

import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.SpeedMode
import net.ccbluex.liquidbounce.utils.MovementUtils.direction
import net.ccbluex.liquidbounce.utils.MovementUtils.isMoving
import net.ccbluex.liquidbounce.utils.MovementUtils.speed
import net.ccbluex.liquidbounce.utils.MovementUtils.strafe
import net.ccbluex.liquidbounce.utils.RotationUtils.currentRotation
import net.ccbluex.liquidbounce.utils.RotationUtils.getAngleDifference
import net.ccbluex.liquidbounce.utils.extensions.jmp
import net.ccbluex.liquidbounce.utils.extensions.stopXZ
import kotlin.math.absoluteValue

/**
 * @author SkidderMC/FDPClient
 */
object Vulcan3 : SpeedMode("Vulcan3") {
    private var wasTimer = false

    override fun onUpdate() {
        if (wasTimer) {
            mc.timer.timerSpeed = 1f
            wasTimer = false
        }

        mc.thePlayer.jumpMovementFactor = if ((currentRotation == null && mc.thePlayer.moveStrafing.absoluteValue < 0.1)
            || (currentRotation != null && getAngleDifference(direction.toFloat(), currentRotation!!.yaw).absoluteValue < 45f))
            .026499f else 0.0244f

        if (speed < 0.215f && !mc.thePlayer.onGround)
            strafe(0.215f)

        if (mc.thePlayer.onGround && isMoving) {
            mc.thePlayer.jmp()
            if (!mc.thePlayer.isAirBorne)
                return // Prevent flag with Flight
            mc.timer.timerSpeed = 1.25f
            wasTimer = true
            strafe()
            if (speed < 0.5f)
                strafe(0.4849f)
        } else if (!isMoving) {
            mc.timer.timerSpeed = 1f
            mc.thePlayer.stopXZ()
        }
    }
}
