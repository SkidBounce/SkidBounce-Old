/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.flymodes.spartan

import net.ccbluex.liquidbounce.event.MoveEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.Fly.spartan532Speed
import net.ccbluex.liquidbounce.features.module.modules.movement.Fly.spartan532Timer
import net.ccbluex.liquidbounce.features.module.modules.movement.flymodes.FlyMode
import net.ccbluex.liquidbounce.utils.MovementUtils.strafe

/**
 * @author ManInMyVan/SkidBounce
 * @author ManInMyVan
 */
object Spartan532 : FlyMode("Spartan532") {
    override fun onMove(event: MoveEvent) {
        mc.thePlayer ?: return

        mc.timer.timerSpeed = spartan532Timer

        strafe(spartan532Speed, true, event)

        mc.thePlayer.onGround = false
        mc.thePlayer.isInWeb = false

        mc.thePlayer.capabilities.isFlying = false

        var ySpeed = 0.0

        if (mc.gameSettings.keyBindJump.isKeyDown)
            ySpeed += spartan532Speed

        if (mc.gameSettings.keyBindSneak.isKeyDown)
            ySpeed -= spartan532Speed

        mc.thePlayer.motionY = ySpeed
        event.y = ySpeed
    }
}
