/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.nowebmodes.aac

import net.ccbluex.liquidbounce.features.module.modules.movement.nowebmodes.NoWebMode
import net.ccbluex.liquidbounce.event.JumpEvent

object AAC4 : NoWebMode("AAC4") {
    private var usedTimer = false
    override fun onUpdate() {
        if (usedTimer && !mc.thePlayer.isInWeb) {
            mc.timer.timerSpeed = 1F
            usedTimer = false
        }
        if (!mc.thePlayer.isInWeb) return
        mc.timer.timerSpeed = 0.99F
        mc.thePlayer.jumpMovementFactor = 0.02958f
        mc.thePlayer.motionY -= 0.00775
        if (mc.thePlayer.onGround) {
            // mc.thePlayer.jump()
            mc.thePlayer.motionY = 0.4050
            mc.timer.timerSpeed = 1.35F
        }
        usedTimer = true
    }
    fun onJump(event: JumpEvent) {
        event.cancelEvent()
    }
}
