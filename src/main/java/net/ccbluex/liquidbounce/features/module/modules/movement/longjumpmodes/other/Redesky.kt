/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.longjumpmodes.other

import net.ccbluex.liquidbounce.features.module.modules.movement.LongJump.redeskyJumpMovement
import net.ccbluex.liquidbounce.features.module.modules.movement.LongJump.redeskyMotionY
import net.ccbluex.liquidbounce.features.module.modules.movement.LongJump.redeskyTimer
import net.ccbluex.liquidbounce.features.module.modules.movement.LongJump.redeskyUseTimer
import net.ccbluex.liquidbounce.features.module.modules.movement.longjumpmodes.LongJumpMode
import net.ccbluex.liquidbounce.utils.extensions.resetSpeed

object Redesky : LongJumpMode("Redesky") {
    override fun onDisable() {
        mc.timer.resetSpeed()
    }
    override fun onUpdate() {
        mc.thePlayer.jumpMovementFactor = redeskyJumpMovement
        mc.thePlayer.motionY += redeskyMotionY / 10
        mc.timer.timerSpeed = if (redeskyUseTimer) redeskyTimer else 1f
    }
}
