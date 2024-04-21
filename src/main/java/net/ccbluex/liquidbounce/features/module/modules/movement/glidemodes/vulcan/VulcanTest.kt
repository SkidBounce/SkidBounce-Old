/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.glidemodes.vulcan

import net.ccbluex.liquidbounce.event.events.MoveEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.glidemodes.GlideMode

/**
 * @author SkidderMC/FDPClient
 */
object VulcanTest : GlideMode("VulcanTest") {
    override fun onMove(event: MoveEvent) {
        if (mc.thePlayer.fallDistance > 2) {
            mc.thePlayer.onGround = true
            mc.thePlayer.fallDistance = 0f
        }

        mc.thePlayer.motionY =
            if (mc.thePlayer.ticksExisted % 3 != 0)
                -0.0991 else mc.thePlayer.motionY + 0.026
    }
}
