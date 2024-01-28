/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.nowebmodes.other

import net.ccbluex.liquidbounce.features.module.modules.movement.nowebmodes.NoWebMode

object Test : NoWebMode("Test") {

    override fun onUpdate() {
        if (!mc.thePlayer.isInWeb) return
        if (mc.thePlayer.ticksExisted % 7 == 0) {
            mc.thePlayer.jumpMovementFactor = 0.42f
        }
        if (mc.thePlayer.ticksExisted % 7 == 1) {
            mc.thePlayer.jumpMovementFactor = 0.33f
        }
        if (mc.thePlayer.ticksExisted % 7 == 2) {
            mc.thePlayer.jumpMovementFactor = 0.08f
        }
    }
}
