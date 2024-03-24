/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.nowebmodes.intave

import net.ccbluex.liquidbounce.features.module.modules.movement.nowebmodes.NoWebMode

/**
 * @author SkidderMC/FDPClient
 */
object Intave14 : NoWebMode("Intave14") {
    override fun onUpdate() {
        if (!mc.thePlayer.isInWeb) return
        if (mc.thePlayer.movementInput.moveStrafe == 0.0F && mc.gameSettings.keyBindForward.isKeyDown && mc.thePlayer.isCollidedVertically) {
            mc.thePlayer.jumpMovementFactor = 0.74F
        } else {
            mc.thePlayer.jumpMovementFactor = 0.2F
            mc.thePlayer.onGround = true
        }
    }
}