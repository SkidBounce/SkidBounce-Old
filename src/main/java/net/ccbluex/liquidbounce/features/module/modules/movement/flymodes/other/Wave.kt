/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.flymodes.other

import net.ccbluex.liquidbounce.features.module.modules.movement.Fly.waveDownSpeed
import net.ccbluex.liquidbounce.features.module.modules.movement.Fly.waveUpSpeed
import net.ccbluex.liquidbounce.features.module.modules.movement.Fly.waveTimer
import net.ccbluex.liquidbounce.features.module.modules.movement.flymodes.FlyMode
import net.ccbluex.liquidbounce.utils.MovementUtils.isMoving
import net.ccbluex.liquidbounce.utils.MovementUtils.strafe
import net.ccbluex.liquidbounce.utils.extensions.jump
import net.minecraft.potion.Potion.moveSpeed

/**
 * @author ManInMyVan/SkidBounce
 * @author ManInMyVan
 */
object Wave : FlyMode("Wave") {
	override fun onUpdate() {
		mc.timer.timerSpeed = if (isMoving) waveTimer else 1f

		if (mc.thePlayer.onGround) {
			mc.thePlayer.jump(0.0798)
			return
		}

		mc.thePlayer.motionY = when {
			mc.gameSettings.keyBindJump.isKeyDown && !mc.gameSettings.keyBindSneak.isKeyDown -> waveUpSpeed
			mc.gameSettings.keyBindSneak.isKeyDown && !mc.gameSettings.keyBindJump.isKeyDown -> -waveDownSpeed
			else -> if (mc.thePlayer.motionY <= -0.0399) 0.0399 else -0.0399
		}.toDouble()

		strafe(if (mc.thePlayer.isPotionActive(moveSpeed)) 1.32f else 0.588f, true)
	}
}
