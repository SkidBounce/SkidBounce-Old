/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.nowebmodes.other

import net.ccbluex.liquidbounce.features.module.modules.movement.nowebmodes.NoWebMode

object Negativity : NoWebMode("Negativity") {

    override fun onUpdate() {
        if (!mc.thePlayer.isInWeb) return
        mc.thePlayer.jumpMovementFactor = 0.4f
        if (mc.thePlayer.ticksExisted % 2 == 0) {
            mc.thePlayer.jumpMovementFactor = 0.53F
        }
        if (!mc.gameSettings.keyBindSneak.isKeyDown)
            mc.thePlayer.motionY = 0.0
    }
}
