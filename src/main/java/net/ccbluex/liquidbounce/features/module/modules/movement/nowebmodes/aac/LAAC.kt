/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.nowebmodes.aac

import net.ccbluex.liquidbounce.features.module.modules.movement.nowebmodes.NoWebMode
import net.ccbluex.liquidbounce.utils.extensions.jump

/**
 * @author CCBlueX/LiquidBounce
 */
object LAAC : NoWebMode("LAAC") {
    override fun onUpdate() {
        if (!mc.thePlayer.isInWeb) {
            return
        }

        mc.thePlayer.jumpMovementFactor = if (mc.thePlayer.movementInput.moveStrafe != 0f) 1f else 1.21f

        if (!mc.gameSettings.keyBindSneak.isKeyDown)
            mc.thePlayer.motionY = 0.0

        mc.thePlayer.jump(0.42)
    }
}
