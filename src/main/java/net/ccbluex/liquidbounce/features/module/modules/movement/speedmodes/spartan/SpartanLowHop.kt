/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.spartan

import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.SpeedMode
import net.ccbluex.liquidbounce.utils.MovementUtils.isMoving
import net.ccbluex.liquidbounce.utils.MovementUtils.speed
import net.ccbluex.liquidbounce.utils.MovementUtils.strafe
import net.ccbluex.liquidbounce.utils.extensions.component1
import net.ccbluex.liquidbounce.utils.extensions.component2
import net.ccbluex.liquidbounce.utils.extensions.component3
import net.ccbluex.liquidbounce.utils.extensions.jmp

/**
 * @author SkidderMC/FDPClient
 */
object SpartanLowHop : SpeedMode("SpartanLowHop") {
    private var ticks = 0
    private var launchY = 0.0

    override fun onUpdate() {
        ticks++
        val (x, y, z) = mc.thePlayer
        if (mc.thePlayer.onGround && isMoving) {
            mc.thePlayer.jmp()
            ticks = 0
            strafe(0.48f)
            launchY = y
        } else if (y > launchY)
            mc.thePlayer.setPosition(x, launchY, z)

        strafe(speed.coerceAtLeast(0.225f))
    }
}
