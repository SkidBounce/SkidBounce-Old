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
import net.ccbluex.liquidbounce.utils.extensions.*

/**
 * @author SkidderMC/FDPClient
 */
object VulcanLowHop : SpeedMode("VulcanLowHop") {
    private var ticks = 0
    private var launchY = 0.0

    override fun onToggle(state: Boolean) {
        ticks = 0
        launchY = mc.thePlayer.posY
    }

    override fun onUpdate() {
        ticks++

        mc.thePlayer.jumpMovementFactor = 0.0245f

        if (mc.thePlayer.onGround && isMoving) {
            mc.thePlayer.jmp()
            ticks = 0
            strafe()
            if (speed < 0.5f)
                strafe(0.484f)
            launchY = mc.thePlayer.posY
        } else if (mc.thePlayer.posY > launchY && ticks <= 1)
            mc.thePlayer.setPosition(mc.thePlayer.posX, launchY, mc.thePlayer.posZ)
        else if (ticks == 5)
            mc.thePlayer.motionY = -0.17

        if (speed < 0.215)
            strafe(0.215f)
    }
}
