package net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.vanilla

import net.ccbluex.liquidbounce.features.module.modules.player.NoFall.motionMotion
import net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.NoFallMode

object Motion : NoFallMode("Motion") {
    override fun onUpdate() {
        if (mc.thePlayer.fallDistance > 3)
            mc.thePlayer.motionY = motionMotion.toDouble()
    }
}