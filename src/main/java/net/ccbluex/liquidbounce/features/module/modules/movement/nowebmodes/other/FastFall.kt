/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.nowebmodes.other

import net.ccbluex.liquidbounce.features.module.modules.movement.nowebmodes.NoWebMode

object FastFall : NoWebMode("FastFall") {
    //  Bypass AAC(All), Vulcan, Verus, Matrix, NCP3.17, HAWK, Spartan
    override fun onUpdate() {
        if (!mc.thePlayer.isInWeb) return
        if (mc.thePlayer.onGround) mc.thePlayer.jump()
        if (mc.thePlayer.motionY > 0f) {
            mc.thePlayer.motionY -= mc.thePlayer.motionY * 2
        }
    }
}
