/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.nowebmodes.other

import net.ccbluex.liquidbounce.features.module.modules.movement.NoWeb.horizonSpeed
import net.ccbluex.liquidbounce.features.module.modules.movement.nowebmodes.NoWebMode
import net.ccbluex.liquidbounce.utils.MovementUtils


object Horizon : NoWebMode("Horizon") {
    override fun onUpdate() {
        if (!mc.thePlayer.isInWeb || !mc.thePlayer.onGround) return
        MovementUtils.strafe(horizonSpeed)
    }
}
