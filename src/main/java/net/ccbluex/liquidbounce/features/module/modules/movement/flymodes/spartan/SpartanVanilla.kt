/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.flymodes.spartan

import net.ccbluex.liquidbounce.event.MoveEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.Fly.spartanvanillaSpeed
import net.ccbluex.liquidbounce.features.module.modules.movement.Fly.spartanvanillaTimer
import net.ccbluex.liquidbounce.features.module.modules.movement.flymodes.FlyMode
import net.ccbluex.liquidbounce.utils.MovementUtils.strafe

/**
 * @author ManInMyVan/SkidBounce
 * @author ManInMyVan
 */
object SpartanVanilla : FlyMode("SpartanVanilla") {
	override fun onMove(event: MoveEvent) {
		mc.thePlayer ?: return

		mc.timer.timerSpeed = spartanvanillaTimer

		strafe(spartanvanillaSpeed, true, event)

		mc.thePlayer.onGround = false
		mc.thePlayer.isInWeb = false

		mc.thePlayer.capabilities.isFlying = false

		var ySpeed = 0.0

		if (mc.gameSettings.keyBindJump.isKeyDown)
			ySpeed += spartanvanillaSpeed

		if (mc.gameSettings.keyBindSneak.isKeyDown)
			ySpeed -= spartanvanillaSpeed

		mc.thePlayer.motionY = ySpeed
		event.y = ySpeed
	}
}
