/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.longjumpmodes.other

import net.ccbluex.liquidbounce.features.module.modules.movement.LongJump.redesky2AirSpeed
import net.ccbluex.liquidbounce.features.module.modules.movement.LongJump.redesky2AirSpeedReducer
import net.ccbluex.liquidbounce.features.module.modules.movement.LongJump.redesky2MinAirSpeed
import net.ccbluex.liquidbounce.features.module.modules.movement.LongJump.redesky2MinYMotion
import net.ccbluex.liquidbounce.features.module.modules.movement.LongJump.redesky2ReduceAirSpeed
import net.ccbluex.liquidbounce.features.module.modules.movement.LongJump.redesky2ReduceYMotion
import net.ccbluex.liquidbounce.features.module.modules.movement.LongJump.redesky2YMotion
import net.ccbluex.liquidbounce.features.module.modules.movement.LongJump.redesky2YMotionReducer
import net.ccbluex.liquidbounce.features.module.modules.movement.longjumpmodes.LongJumpMode

/**
 * @author SkidderMC/FDPClient
 * @author liulihaocai
 */
object Redesky2 : LongJumpMode("Redesky2") {
    private var airTicks = 0

    override fun onDisable() {
        mc.thePlayer.speedInAir = 0.02f
        airTicks = 0
    }

    override fun onUpdate() {
        if (!mc.thePlayer.onGround) {
            ++airTicks

            mc.thePlayer.motionY += when {
                !redesky2YMotionReducer -> redesky2YMotion
                redesky2YMotion - (airTicks * (redesky2ReduceYMotion / 100)) < redesky2MinYMotion -> redesky2MinYMotion
                else -> redesky2YMotion - (airTicks * (redesky2ReduceYMotion / 100))
            }

            mc.thePlayer.speedInAir = when {
                !redesky2AirSpeedReducer -> redesky2AirSpeed
                redesky2AirSpeed - (airTicks * (redesky2ReduceAirSpeed / 100)) < redesky2MinAirSpeed -> redesky2MinAirSpeed
                else -> redesky2AirSpeed - (airTicks * (redesky2ReduceAirSpeed / 100))
            }
        }
    }
}
