/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.nowebmodes.aac

import net.ccbluex.liquidbounce.features.module.modules.movement.nowebmodes.NoWebMode
import net.ccbluex.liquidbounce.utils.extensions.jump

object AAC5 : NoWebMode("AAC5") {
    override fun onUpdate() {
        if (!mc.thePlayer.isInWeb) return
        mc.thePlayer.jumpMovementFactor = 0.42f

        mc.thePlayer.jump(0.42)
    }
}
