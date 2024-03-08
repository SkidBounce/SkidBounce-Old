/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.longjumpmodes.other

import net.ccbluex.liquidbounce.features.module.modules.movement.LongJump.redesky3JumpMovement
import net.ccbluex.liquidbounce.features.module.modules.movement.LongJump.redesky3MotionY
import net.ccbluex.liquidbounce.features.module.modules.movement.LongJump.redesky3MovementReducer
import net.ccbluex.liquidbounce.features.module.modules.movement.LongJump.redesky3ReduceMovement
import net.ccbluex.liquidbounce.features.module.modules.movement.LongJump.redesky3ReduceYMotion
import net.ccbluex.liquidbounce.features.module.modules.movement.LongJump.redesky3Timer
import net.ccbluex.liquidbounce.features.module.modules.movement.LongJump.redesky3UseTimer
import net.ccbluex.liquidbounce.features.module.modules.movement.LongJump.redesky3YMotionReducer
import net.ccbluex.liquidbounce.features.module.modules.movement.longjumpmodes.LongJumpMode
import net.ccbluex.liquidbounce.utils.extensions.resetSpeed

/**
 * @author SkidderMC/FDPClient
 * @author liulihaocai
 */
object Redesky3 : LongJumpMode("Redesky3") {
    private var airTicks = 0
    override fun onDisable() {
        mc.timer.resetSpeed()
        airTicks = 0
    }

    override fun onUpdate() {
        if (mc.thePlayer.onGround) return

        ++airTicks

        val reduceMovement = if (redesky3MovementReducer) airTicks * redesky3ReduceMovement / 100f else 0f
        mc.thePlayer.jumpMovementFactor = redesky3JumpMovement - reduceMovement

        val reduceYMotion = if (redesky3YMotionReducer) airTicks * redesky3ReduceYMotion / 100f else 0f
        mc.thePlayer.motionY += (redesky3MotionY / 10) - reduceYMotion

        mc.timer.timerSpeed = if (redesky3UseTimer) redesky3Timer else 1f
    }
}
