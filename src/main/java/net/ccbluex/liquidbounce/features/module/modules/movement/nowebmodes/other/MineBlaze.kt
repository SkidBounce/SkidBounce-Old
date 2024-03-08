/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.nowebmodes.other

import net.ccbluex.liquidbounce.features.module.modules.movement.nowebmodes.NoWebMode

/**
 * @author CCBlueX/LiquidBounce
 */
object MineBlaze : NoWebMode("MineBlaze") {
    override fun onUpdate() {
        val thePlayer = mc.thePlayer ?: return

        if (!thePlayer.isInWeb) {
            return
        }

        if (thePlayer.movementInput.moveStrafe == 0.0F && mc.gameSettings.keyBindForward.isKeyDown && thePlayer.isCollidedVertically) {
            thePlayer.jumpMovementFactor = 0.74F
        } else {
            thePlayer.jumpMovementFactor = 0.2F
            thePlayer.onGround = true
        }
    }
}
