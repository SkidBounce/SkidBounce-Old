/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.modules.combat.velocitymodes.aac.*
import net.ccbluex.liquidbounce.features.module.modules.combat.velocitymodes.grim.*
import net.ccbluex.liquidbounce.features.module.modules.combat.velocitymodes.other.*
import net.ccbluex.liquidbounce.utils.timing.MSTimer
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.network.play.server.S12PacketEntityVelocity

object Velocity : Module("Velocity", ModuleCategory.COMBAT) {
    private val velocityModes = arrayOf(
        Vanilla,
        Phase,
        Vulcan,
        NewGrim,
        OldGrim,
        AAC,
        AACPush,
        AACv4,
        AACZero,
    )

    private val modeModule get() = velocityModes.find { it.modeName == mode }!!
    override val tag get() = mode

    /* TODO:
         Motion Limits
         Only Combat
         Direction Override
         Delayed setting in Vanilla
     */

    //  Settings

    private val mode by ListValue("Mode", velocityModes.map { it.modeName }.toTypedArray(), "Vanilla")
    private val noFire by BoolValue("NoFire", true)
    private val onlyGround by BoolValue("OnlyGround", false)

    val cancelHorizontal by BoolValue("CancelHorizontal", true) { mode == "Vanilla" }
    val cancelVertical by BoolValue("CancelVertical", true) { mode == "Vanilla" }
    val horizontalMultiplier by FloatValue("HorizontalMultiplier", 0f, 0f..1f) { mode == "Vanilla" && !cancelHorizontal }
    val verticalMultiplier by FloatValue("VerticalMultiplier", 0f, 0f..1f) { mode == "Vanilla" && !cancelVertical }
    val chance by IntegerValue("Chance", 100, 0..100) { mode == "Vanilla" }
    val attackReduce by BoolValue("AttackReduce", false) { mode == "Vanilla" }
    val attackReduceMultiplier by FloatValue("AttackReduce-Multiplier", 0.8f, 0f..1f) { mode == "Vanilla" && attackReduce }
    val jump by BoolValue("Jump", false) { mode == "Vanilla" }
    val jumpMotion by FloatValue("Jump-Motion", 0.42f, 0f..0.42f) { mode == "Vanilla" && jump }
    val jumpFailRate by IntegerValue("Jump-FailRate", 0, 0..100) { mode == "Vanilla" && jump }
    val tickreduce by BoolValue("TickReduce", false) { mode == "Vanilla" }
    val tickreduceTicks by IntegerValue("TickReduce-Ticks", 1, 1..10) { mode == "Vanilla" && tickreduce }
    val tickreduceMultiplier by FloatValue("TickReduce-Multiplier", 0f, 0f..1f) { mode == "Vanilla" && tickreduce }
    val tickreduceVertical by BoolValue("TickReduce-Vertical", false) { mode == "Vanilla" && tickreduce }
    val tickreduceHorizontal by BoolValue("TickReduce-Horizontal", false) { mode == "Vanilla" && tickreduce }
    val reverse by BoolValue("Reverse", false) { mode == "Vanilla" }
    val reverseSmooth by BoolValue("Reverse-Smooth", false) { mode == "Vanilla" && reverse }
    val reverseNoGround by BoolValue("Reverse-NoGround", true) { mode == "Vanilla" && reverse }
    val reverseTicks by IntegerValue("Reverse-StrafeTicks", 1, 1..20) { mode == "Vanilla" && reverse }
    val reverseStrength by FloatValue("Reverse-Strength", 1f, 0.02f..1f) { mode == "Vanilla" && reverse }

    val newgrimAlways by BoolValue("Always", false) { mode == "NewGrim" }
    val newgrimOnlyAir by BoolValue("OnlyBreakAir", true) { mode == "NewGrim" }
    val newgrimWorld by BoolValue("BreakOnWorld", false) { mode == "NewGrim" }
    val newgrimFlagPause by IntegerValue("FlagPauseTime", 10, 0..1000) { mode == "NewGrim" }
    val newgrimSendC03 by BoolValue("SendC03", true) { mode == "NewGrim" }
    val newgrimC06 by BoolValue("Send1.17C06", false) { mode == "NewGrim" && newgrimSendC03 } // need via to 1.17+
    val newgrimTimerTicks by IntegerValue("TimerTicks", 20, 0..100) { mode == "NewGrim" && newgrimSendC03 }
    val newgrimTimerSpeed by FloatValue("TimerSpeed", 0.8f, 0f..1f) { mode == "NewGrim" && newgrimSendC03 }

    val aacv4MotionReducer by FloatValue("AACv4MotionReducer", 0.62f, 0f..1f) { mode == "AACv4" }

    val aacHorizontal by FloatValue("AAC-HorizontalMultiplier", 0f, 0f..1f) { mode == "AAC" }
    val aacVertical by FloatValue("AAC-VerticalMultiplier", 0f, 0f..1f) { mode == "AAC" }

    val aacPushXZReducer by FloatValue("AACPushXZReducer", 2f, 1f..3f) { mode == "AACPush" }
    val aacPushYReducer by BoolValue("AACPushYReducer", true) { mode == "AACPush" }

    var velocityTick = 0
    val velocityTimer = MSTimer()

    override fun onDisable() {
        mc.thePlayer.speedInAir = 0.02f
        mc.timer.timerSpeed = 1f
    }

    override fun onEnable() {
        velocityTimer.reset()
        velocityTick = 0
        Phase.hasVelocity = false
        AAC.hasVelocity = false
        AACZero.hasVelocity = false
        OldGrim.grimTCancel = 0
        NewGrim.gotVelo = false
        NewGrim.flagTimer.reset()
        NewGrim.timerTicks = 0
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        ++velocityTick
        modeModule.onUpdate()
    }
    @EventTarget
    fun onJump(event: JumpEvent) { modeModule.onJump(event) }
    @EventTarget
    fun onTick(event: TickEvent) { modeModule.onTick(event) }
    @EventTarget
    fun onAttack(event: AttackEvent) { modeModule.onAttack() }
    @EventTarget(priority = 1)
    fun onPacket(event: PacketEvent) {
        modeModule.onPacket(event)
        if (event.packet is S12PacketEntityVelocity && event.packet.entityID == mc.thePlayer.entityId) {
            if ((noFire && mc.thePlayer.isBurning) || (onlyGround && !mc.thePlayer.onGround)) return
            velocityTimer.reset()
            velocityTick = 0
            modeModule.onVelocityPacket(event)
        }
    }
}
