/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.events.JumpEvent
import net.ccbluex.liquidbounce.event.events.MoveEvent
import net.ccbluex.liquidbounce.event.events.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory.MOVEMENT
import net.ccbluex.liquidbounce.features.module.modules.movement.longjumpmodes.LongJumpMode
import net.ccbluex.liquidbounce.utils.ClassUtils.getAllObjects
import net.ccbluex.liquidbounce.utils.MovementUtils.isMoving
import net.ccbluex.liquidbounce.utils.MovementUtils.speed
import net.ccbluex.liquidbounce.utils.extensions.jmp
import net.ccbluex.liquidbounce.value.BooleanValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.ListValue

object LongJump : Module("LongJump", MOVEMENT) {
    private val longJumpModes = javaClass.`package`.getAllObjects<LongJumpMode>().sortedBy { it.modeName }

    private val modes = longJumpModes.map { it.modeName }.toTypedArray()

    val mode by ListValue("Mode", modes, "NCP")

    val ncpBoost by FloatValue("NCP-Boost", 4.25f, 1f..10f) { mode == "NCP" }

    val redeskyJumpMovement by FloatValue("Redesky-JumpMovement", 0.15f, 0.05f..0.25f) { mode == "Redesky" }
    val redeskyMotionY by FloatValue("Redesky-MotionY", 0.5f, 0.05f..1f) { mode == "Redesky" }
    val redeskyUseTimer by BooleanValue("Redesky-UseTimer", false) { mode == "Redesky" }
    val redeskyTimer by FloatValue("Redesky-Timer", 0.7f, 0.1f..1f) { mode == "Redesky" && redeskyUseTimer }

    val redesky2YMotion by FloatValue("Redesky2YMotion", 0.08f, 0.01f..0.2f) { mode == "Redesky2" }
    val redesky2YMotionReducer by BooleanValue("Redesky2-YMotionReducer", false) { mode == "Redesky2" }
    val redesky2ReduceYMotion by FloatValue(
        "Redesky2-ReduceYMotion",
        0.15f,
        0.01f..0.2f
    ) { mode == "Redesky2" && redesky2YMotionReducer }
    val redesky2MinYMotion by FloatValue(
        "Redesky2-MinYMotion",
        0.04f,
        0.01f..0.2f
    ) { mode == "Redesky2" && redesky2YMotionReducer }
    val redesky2AirSpeed by FloatValue("Redesky2-AirSpeed", 0.1f, 0.05f..0.25f) { mode == "Redesky2" }
    val redesky2AirSpeedReducer by BooleanValue("Redesky2-AirSpeedReducer", false) { mode == "Redesky2" }
    val redesky2ReduceAirSpeed by FloatValue(
        "Redesky2-ReduceAirSpeed",
        0.16f,
        0.05f..0.25f
    ) { mode == "Redesky2" && redesky2AirSpeedReducer }
    val redesky2MinAirSpeed by FloatValue(
        "Redesky2-MinAirSpeed",
        0.08f,
        0.05f..0.25f
    ) { mode == "Redesky2" && redesky2AirSpeedReducer }

    val redesky3JumpMovement by FloatValue("Redesky3-JumpMovement", 0.13f, 0.05f..0.25f) { mode == "Redesky3" }
    val redesky3MovementReducer by BooleanValue("Redesky3-MovementReducer", true) { mode == "Redesky3" }
    val redesky3ReduceMovement by FloatValue(
        "Redesky3-ReduceMovement",
        0.08f,
        0.05f..0.25f
    ) { mode == "Redesky3" && redesky3MovementReducer }
    val redesky3MotionY by FloatValue("Redesky3-MotionY", 0.81f, 0.05f..1f) { mode == "Redesky3" }
    val redesky3YMotionReducer by BooleanValue("Redesky3-YMotionReducer", true) { mode == "Redesky3" }
    val redesky3ReduceYMotion by FloatValue(
        "Redesky3-ReduceYMotion",
        0.12f,
        0.01f..0.2f
    ) { mode == "Redesky3" && redesky3YMotionReducer }
    val redesky3UseTimer by BooleanValue("Redesky3-UseTimer", true) { mode == "Redesky3" }
    val redesky3Timer by FloatValue("Redesky3-Timer", 0.36f, 0.1f..1f) { mode == "Redesky3" && redesky3UseTimer }

    private val autoJump by BooleanValue("AutoJump", true)

    var jumped = false
    var canBoost = false
    var teleported = false
    var canMineplexBoost = false

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (LadderJump.jumped) speed *= 1.08f

        if (jumped) {
            val mode = mode

            if (mc.thePlayer.onGround || mc.thePlayer.capabilities.isFlying) {
                jumped = false
                canMineplexBoost = false

                if (mode == "NCP") {
                    mc.thePlayer.motionX = 0.0
                    mc.thePlayer.motionZ = 0.0
                }
                return
            }

            modeModule.onUpdate()
        }
        if (autoJump && mc.thePlayer.onGround && isMoving) {
            jumped = true
            mc.thePlayer.jmp()
        }
    }

    @EventTarget
    fun onMove(event: MoveEvent) {
        modeModule.onMove(event)
    }

    @EventTarget(ignoreCondition = true)
    fun onJump(event: JumpEvent) {
        jumped = true
        canBoost = true
        teleported = false

        if (handleEvents()) {
            modeModule.onJump(event)
        }
    }

    override fun onDisable() {
        modeModule.onDisable()
    }

    override val tag
        get() = mode

    private val modeModule
        get() = longJumpModes.find { it.modeName == mode }!!
}
