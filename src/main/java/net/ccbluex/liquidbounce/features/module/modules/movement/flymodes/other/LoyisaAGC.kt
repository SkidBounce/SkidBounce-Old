/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.flymodes.other

import net.ccbluex.liquidbounce.event.events.MoveEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.Fly.agcHorizontal
import net.ccbluex.liquidbounce.features.module.modules.movement.Fly.agcVertical
import net.ccbluex.liquidbounce.features.module.modules.movement.Fly.handleVanillaKickBypass
import net.ccbluex.liquidbounce.features.module.modules.movement.flymodes.FlyMode
import net.ccbluex.liquidbounce.utils.MovementUtils.strafe

/**
 * @author ManInMyVan/SkidBounce
 * @author ManInMyVan
 */
object LoyisaAGC : FlyMode("LoyisaAGC") {
    override fun onMove(event: MoveEvent) {
        mc.thePlayer ?: return

        strafe(if (mc.thePlayer.ticksExisted % 3 == 0) agcHorizontal else agcHorizontal.coerceAtMost(0.7f), true, event)

        mc.thePlayer.onGround = false
        mc.thePlayer.isInWeb = false
        mc.thePlayer.capabilities.isFlying = false

        mc.thePlayer.motionY = agcVertical * yDirection
        event.y = agcVertical * yDirection

        handleVanillaKickBypass()
    }
}
