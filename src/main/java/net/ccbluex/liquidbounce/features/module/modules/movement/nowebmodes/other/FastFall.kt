/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.nowebmodes.other

import net.ccbluex.liquidbounce.features.module.modules.movement.nowebmodes.NoWebMode
import net.ccbluex.liquidbounce.utils.extensions.jmp

/**
 * @author SkidderMC/FDPClient
 */
object FastFall : NoWebMode("FastFall") {
    override fun onUpdate() {
        if (!mc.thePlayer.isInWeb) return
        mc.thePlayer.jmp()
        if (mc.thePlayer.motionY > 0f)
            mc.thePlayer.motionY -= mc.thePlayer.motionY * 2
    }
}
