/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.glidemodes.other

import net.ccbluex.liquidbounce.features.module.modules.movement.glidemodes.GlideMode

/**
 * @author CCBlueX/LiquidBounce
 */
object AAC3312 : GlideMode("AAC3.3.12") {
	private var tick = 0

	override fun onUpdate() {
		if (!mc.thePlayer.onGround)
			tick++

		when (tick) {
			2 -> mc.timer.timerSpeed = 1f
			12 -> mc.timer.timerSpeed = 0.1f
			else -> if (tick >= 12 && !mc.thePlayer.onGround) {
				tick = 0
				mc.thePlayer.motionY = .015
			}
		}
	}
}
