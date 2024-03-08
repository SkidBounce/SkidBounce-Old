/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory.COMBAT
import net.ccbluex.liquidbounce.features.module.modules.combat.velocitymodes.aac.*
import net.ccbluex.liquidbounce.features.module.modules.combat.velocitymodes.grim.*
import net.ccbluex.liquidbounce.features.module.modules.combat.velocitymodes.other.*
import net.ccbluex.liquidbounce.features.module.modules.combat.velocitymodes.vanilla.*
import net.ccbluex.liquidbounce.utils.timing.MSTimer
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.network.play.server.S12PacketEntityVelocity
import net.minecraft.network.play.server.S27PacketExplosion

object Velocity : Module("Velocity", COMBAT) {
    private val velocityModes = arrayOf(
        Custom,
        Vanilla,
        Phase,
        Vulcan, Vulcan2,
        Matrix,
        Grim, GrimTransaction,
        AAC, AACPush, AACv4, AACZero,
        GhostBlock,
    ).sortedBy { it.modeName }

    private val modeModule get() = velocityModes.find { it.modeName == mode }!!
    override val tag get() = mode

    /* TODO:
     *   Motion Limits
     *   Only Combat
     *   Direction Override
     *   Delayed setting in Custom
     */

    //  Settings

    private val mode by ListValue("Mode", velocityModes.map { it.modeName }.toTypedArray(), "Vanilla")
    private val noFire by BoolValue("NoFire", true) { mode !in arrayOf("AACv4", "AACPush") }
    private val onlyGround by BoolValue("OnlyGround", false) { mode !in arrayOf("AACv4", "AACPush") }
    private val explosions by BoolValue("Explosions", true) { mode !in arrayOf("AACv4", "AACPush", "Matrix") }

    val cancelHorizontal by BoolValue("CancelHorizontal", true) { mode == "Custom" }
    val cancelVertical by BoolValue("CancelVertical", true) { mode == "Custom" }
    val horizontalMultiplier by FloatValue("HorizontalMultiplier", 0f, 0f..1f) { mode == "Custom" && !cancelHorizontal }
    val verticalMultiplier by FloatValue("VerticalMultiplier", 0f, 0f..1f) { mode == "Custom" && !cancelVertical }
    val chance by IntegerValue("Chance", 100, 0..100) { mode == "Custom" }
    val attackReduce by BoolValue("AttackReduce", false) { mode == "Custom" }
    val attackReduceMultiplier by FloatValue(
        "AttackReduce-Multiplier",
        0.8f,
        0f..1f
    ) { mode == "Custom" && attackReduce }
    val jump by BoolValue("Jump", false) { mode == "Custom" }
    val jumpMotion by FloatValue("Jump-Motion", 0.42f, 0f..0.42f) { mode == "Custom" && jump }
    val jumpFailRate by IntegerValue("Jump-FailRate", 0, 0..100) { mode == "Custom" && jump }
    val tickreduce by BoolValue("TickReduce", false) { mode == "Custom" }
    val tickreduceTicks by IntegerValue("TickReduce-Ticks", 1, 1..10) { mode == "Custom" && tickreduce }
    val tickreduceMultiplier by FloatValue("TickReduce-Multiplier", 0f, 0f..1f) { mode == "Custom" && tickreduce }
    val tickreduceVertical by BoolValue("TickReduce-Vertical", false) { mode == "Custom" && tickreduce }
    val tickreduceHorizontal by BoolValue("TickReduce-Horizontal", false) { mode == "Custom" && tickreduce }
    val reverse by BoolValue("Reverse", false) { mode == "Custom" }
    val onLook by BoolValue("onLook", false) { reverse }
    val range by FloatValue("Range", 3.0F, 1F..5.0F) { onLook && reverse }
    val maxAngleDifference by FloatValue("MaxAngleDifference", 45.0f, 5.0f..90f) { onLook && reverse }
    val reverseSmooth by BoolValue("Reverse-Smooth", false) { mode == "Custom" && reverse }
    val reverseNoGround by BoolValue("Reverse-NoGround", true) { mode == "Custom" && reverse }
    val reverseTicks by IntegerValue("Reverse-StrafeTicks", 1, 1..20) { mode == "Custom" && reverse }
    val reverseStrength by FloatValue("Reverse-Strength", 1f, 0.02f..1f) { mode == "Custom" && reverse }

    val grimAlways by BoolValue("Grim-Always", false) { mode == "Grim" }
    val grimOnlyAir by BoolValue("Grim-OnlyBreakAir", true) { mode == "Grim" }
    val grimWorld by BoolValue("Grim-BreakOnWorld", false) { mode == "Grim" }
    val grimFlagPause by IntegerValue("Grim-FlagPauseTime", 10, 0..1000) { mode == "Grim" }
    val grimPacket by ListValue(
        "Grim-Packet",
        arrayOf("Flying", "Position", "Rotation", "Full", "None"),
        "Position"
    ) { mode == "Grim" }
    val grimTimerMode by ListValue(
        "Grim-TimerMode",
        arrayOf("New", "Old", "Off"),
        "New"
    ) { mode == "Grim" && grimPacket != "None" }
    val grimTimerTicks by IntegerValue(
        "Grim-TimerTicks",
        20,
        1..100
    ) { mode == "Grim" && grimPacket != "None" && grimTimerMode != "Off" }
    val grimTimerSpeed by FloatValue(
        "Grim-TimerSpeed",
        0.8f,
        0f..1f
    ) { mode == "Grim" && grimPacket != "None" && grimTimerMode != "Off" }

    val aacv4MotionReducer by FloatValue("AACv4MotionReducer", 0.62f, 0f..1f) { mode == "AACv4" }

    val aacHorizontal by FloatValue("AAC-HorizontalMultiplier", 0f, 0f..1f) { mode == "AAC" }
    val aacVertical by FloatValue("AAC-VerticalMultiplier", 0f, 0f..1f) { mode == "AAC" }

    val aacPushXZReducer by FloatValue("AACPushXZReducer", 2f, 1f..3f) { mode == "AACPush" }
    val aacPushYReducer by BoolValue("AACPushYReducer", true) { mode == "AACPush" }

    val maxHurtTime: IntegerValue = object : IntegerValue("MaxHurtTime", 9, 1..10) {
        override fun isSupported() = mode == "GhostBlock"
        override fun onChange(oldValue: Int, newValue: Int) = newValue.coerceAtLeast(minHurtTime.get())
    }

    val minHurtTime: IntegerValue = object : IntegerValue("MinHurtTime", 1, 1..10) {
        override fun isSupported() = mode == "GhostBlock" && !maxHurtTime.isMinimal()
        override fun onChange(oldValue: Int, newValue: Int) = newValue.coerceIn(0, maxHurtTime.get())
    }

    var velocityTick = 0
    val velocityTimer = MSTimer()

    override fun onDisable() {
        mc.thePlayer.speedInAir = 0.02f
        mc.timer.timerSpeed = 1f
    }

    override fun onEnable() {
        velocityTimer.reset()
        velocityTick = 0
        modeModule.onEnable()
    }

    @Suppress("UNUSED_PARAMETER")
    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        ++velocityTick
        modeModule.onUpdate()
    }

    @EventTarget
    fun onJump(event: JumpEvent) {
        modeModule.onJump(event)
    }

    @EventTarget
    fun onTick(event: TickEvent) {
        modeModule.onTick(event)
    }

    @Suppress("UNUSED_PARAMETER")
    @EventTarget
    fun onAttack(event: AttackEvent) {
        modeModule.onAttack()
    }

    @EventTarget(priority = 1)
    fun onPacket(event: PacketEvent) {
        modeModule.onPacket(event)
        val packet = event.packet
        if ((packet is S27PacketExplosion && explosions)
            || (packet is S12PacketEntityVelocity && packet.entityID == mc.thePlayer.entityId)
            && !(noFire && mc.thePlayer.isBurning)
            && !(onlyGround && !mc.thePlayer.onGround)
        ) {
            velocityTimer.reset()
            velocityTick = 0
            modeModule.onVelocityPacket(event)
        }
    }

    @EventTarget
    fun onBlockBB(event: BlockBBEvent) {
        mc.thePlayer ?: return
        modeModule.onBlockBB(event)
    }
}
