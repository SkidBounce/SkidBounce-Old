/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.nowebmodes.horizon

import net.ccbluex.liquidbounce.features.module.modules.movement.nowebmodes.NoWebMode
import net.ccbluex.liquidbounce.features.module.modules.movement.NoWeb.horizonSpeed
import net.ccbluex.liquidbounce.utils.MovementUtils



object Horizon : NoWebMode("Horizon") {
    override fun onUpdate() {
        if (!mc.thePlayer.isInWeb || !mc.thePlayer.onGround) return
        MovementUtils.strafe(horizonSpeed)
    }
}
