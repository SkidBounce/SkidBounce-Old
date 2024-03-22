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

/**
 * @author SkidderMC/FDPClient
 */
object VulcanYPort2 : SpeedMode("VulcanYPort2") {
    private var wasTimer = false
    private var portSwitcher = 0

    override fun onToggle(state: Boolean) {
        wasTimer = true
        portSwitcher = 0
    }

    override fun onUpdate() {
        if (wasTimer) {
            mc.timer.timerSpeed = 1f
            wasTimer = false
        }

        if (portSwitcher > 1) {
            mc.thePlayer.motionY = -0.2784
            mc.timer.timerSpeed = 1.5f
            wasTimer = true
            portSwitcher = 0
        }

        if (speed < 0.215f && !mc.thePlayer.onGround)
            strafe(0.215f)

        if (mc.thePlayer.onGround && isMoving) {
            mc.thePlayer.jmp()
            strafe()
            if (portSwitcher >= 1) {
                mc.thePlayer.motionY = 0.2
                mc.timer.timerSpeed = 1.5f
            }
            ++portSwitcher
        } else if (speed < 0.225)
            strafe(0.225f)
    }
}
