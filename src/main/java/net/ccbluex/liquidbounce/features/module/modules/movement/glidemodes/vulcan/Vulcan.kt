/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.glidemodes.vulcan

import net.ccbluex.liquidbounce.features.module.modules.movement.glidemodes.GlideMode

object Vulcan : GlideMode("Vulcan") {
    override fun onUpdate() {
        if (!mc.thePlayer.onGround && mc.thePlayer.fallDistance > 0) {
            mc.thePlayer.motionY =
                if (mc.thePlayer.ticksExisted % 2 == 0) -0.155 else -0.1
        }
    }
}