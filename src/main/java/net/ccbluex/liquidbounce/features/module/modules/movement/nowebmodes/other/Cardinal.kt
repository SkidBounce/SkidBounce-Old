/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.nowebmodes.other

import net.ccbluex.liquidbounce.features.module.modules.movement.nowebmodes.NoWebMode
import net.ccbluex.liquidbounce.utils.MovementUtils

/**
 * @author liquidbounceplusreborn/LiquidbouncePlus-Reborn
 */
object Cardinal : NoWebMode("Cardinal") {
    override fun onUpdate() {
        if (!mc.thePlayer.isInWeb) return
        if (mc.thePlayer.onGround) MovementUtils.strafe(0.262F)
        else MovementUtils.strafe(0.366F)
    }
}
