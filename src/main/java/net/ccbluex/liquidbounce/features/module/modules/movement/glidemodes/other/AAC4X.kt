/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.glidemodes.other

import net.ccbluex.liquidbounce.features.module.modules.movement.glidemodes.GlideMode

/**
 * @author SkidderMC/FDPClient
 */
object AAC4X : GlideMode("AAC4.X") {
    private var delay = 0

    override fun onEnable() {
        delay = 0
    }

    override fun onUpdate() {

        if (mc.thePlayer.onGround || mc.thePlayer.isCollided) {
            mc.timer.timerSpeed = 1.0f
            delay = 2
            return
        }

        mc.timer.timerSpeed = 0.6f

        if (mc.thePlayer.motionY < 0 && delay > 0) {
            --delay
            mc.timer.timerSpeed = 0.95f
        } else {
            delay = 0
            mc.thePlayer.motionY /= 0.9800000190734863
            mc.thePlayer.motionY += 0.03
            mc.thePlayer.motionY *= 0.9800000190734863
            mc.thePlayer.jumpMovementFactor = 0.03625f
        }
    }
}
