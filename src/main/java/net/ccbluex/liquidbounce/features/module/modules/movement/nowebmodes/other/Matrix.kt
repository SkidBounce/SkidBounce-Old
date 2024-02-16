/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.nowebmodes.other

import net.ccbluex.liquidbounce.features.module.modules.movement.nowebmodes.NoWebMode

/**
 * @author SkidderMC/FDPClient
 */
object Matrix : NoWebMode("Matrix") {
    private var usedTimer = false
    override fun onUpdate() {
        if (usedTimer) {
            mc.timer.timerSpeed = 1F
            usedTimer = false
        }
        if (!mc.thePlayer.isInWeb) return

        mc.thePlayer.jumpMovementFactor = 0.12425f
        mc.thePlayer.motionY = -0.0125
        if (mc.gameSettings.keyBindSneak.isKeyDown) mc.thePlayer.motionY = -0.1625
        if (mc.thePlayer.ticksExisted % 40 == 0) {
            mc.timer.timerSpeed = 3.0F
            usedTimer = true
        }
    }
}
