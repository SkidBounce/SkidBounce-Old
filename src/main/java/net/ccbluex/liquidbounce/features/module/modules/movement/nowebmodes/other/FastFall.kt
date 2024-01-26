/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.nowebmodes.other

import net.ccbluex.liquidbounce.features.module.modules.movement.nowebmodes.NoWebMode
import net.ccbluex.liquidbounce.utils.extensions.jump

object FastFall : NoWebMode("FastFall") {
    override fun onUpdate() {
        if (!mc.thePlayer.isInWeb) return
        mc.thePlayer.jump(0.42)
        if (mc.thePlayer.motionY > 0f)
            mc.thePlayer.motionY -= mc.thePlayer.motionY * 2
    }
}
