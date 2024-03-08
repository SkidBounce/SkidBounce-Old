/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.nowebmodes.other

import net.ccbluex.liquidbounce.features.module.modules.movement.NoWeb
import net.ccbluex.liquidbounce.features.module.modules.movement.nowebmodes.NoWebMode
import net.ccbluex.liquidbounce.utils.MovementUtils

/**
 * @author ManInMyVan/SkidBounce
 * @author ManInMyVan
 */
object Custom : NoWebMode("Custom") {
    override fun onUpdate() {
        if (!mc.thePlayer.isInWeb) return
        MovementUtils.strafe(NoWeb.customSpeed)
        if (NoWeb.customFloat) {
            if (!mc.gameSettings.keyBindJump.isKeyDown && !mc.gameSettings.keyBindSneak.isKeyDown) mc.thePlayer.motionY =
                0.0
            else if (!mc.gameSettings.keyBindSneak.isKeyDown) mc.thePlayer.motionY = NoWeb.customUpSpeed.toDouble()
            else if (!mc.gameSettings.keyBindJump.isKeyDown) mc.thePlayer.motionY = -NoWeb.customDownSpeed.toDouble()
            else mc.thePlayer.motionY = NoWeb.customUpSpeed - NoWeb.customDownSpeed.toDouble()
        }
    }
}
