/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.nowebmodes.spartan

import net.ccbluex.liquidbounce.features.module.modules.movement.nowebmodes.NoWebMode
import net.ccbluex.liquidbounce.utils.MovementUtils



object Spartan : NoWebMode("Spartan") {
    private var usedTimer = false
    override fun onUpdate() {
        if (usedTimer && !mc.thePlayer.isInWeb) {
            mc.timer.timerSpeed = 1F
            usedTimer = false
        }
        if (!mc.thePlayer.isInWeb) return
        MovementUtils.strafe(0.27F)
        mc.timer.timerSpeed = 3.7F
        if (!mc.gameSettings.keyBindSneak.isKeyDown) {
            mc.thePlayer.motionY = 0.0
        }
        if (mc.thePlayer.ticksExisted % 2 == 0) {
            mc.timer.timerSpeed = 1.7F
        }
        if (mc.thePlayer.ticksExisted % 40 == 0) {
            mc.timer.timerSpeed = 3F
        }
        usedTimer = true
    }
}
