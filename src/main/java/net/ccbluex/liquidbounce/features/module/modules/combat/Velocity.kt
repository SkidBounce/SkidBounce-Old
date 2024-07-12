/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.events.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory.COMBAT
import net.ccbluex.liquidbounce.features.module.modules.combat.velocitymodes.VelocityMode
import net.ccbluex.liquidbounce.utils.ClassUtils.getAllObjects
import net.ccbluex.liquidbounce.utils.timing.MSTimer
import net.ccbluex.liquidbounce.value.*
import net.minecraft.network.play.server.S12PacketEntityVelocity
import net.minecraft.network.play.server.S27PacketExplosion

object Velocity : Module("Velocity", COMBAT) {
    private val velocityModes = javaClass.`package`.getAllObjects<VelocityMode>().sortedBy { it.modeName }

    val doAttackReduceLegit
        get() = handleEvents() && mode == "Custom" && attackReduce && attackReduceLegit && mc.thePlayer.hurtTime in attackReduceMinHurtTime..attackReduceMaxHurtTime

    private val modeModule get() = velocityModes.find { it.modeName == mode }!!
    override val tag get() = mode

    /* TODO:
     *   Motion Limits
     *   Only Combat
     *   Delayed setting in Custom
     */

    private val mode by ListValue("Mode", velocityModes.map { it.modeName }.toTypedArray(), "Vanilla")
    private val noFire by BooleanValue("NoFire", true) { mode !in arrayOf("AACv4", "AACPush") }
    private val onlyGround by BooleanValue("OnlyGround", false) { mode !in arrayOf("AACv4", "AACPush") }
    private val explosions by BooleanValue("Explosions", true) { mode !in arrayOf("AACv4", "AACPush", "Matrix") }

    val modify by BooleanValue("Modify", false) { mode == "Custom" }
    val overrideDirection by BooleanValue("OverrideDirection", true) { mode == "Custom" && modify }
    val overrideDirectionRotation by ListValue("OverrideDirectionRotation", arrayOf("Server", "Client"), "Client") { mode == "Custom" && modify }
    val modifyAddedMotion by BooleanValue("ModifyAddedMotion", true) { mode == "Custom" && modify }
    val cancelHorizontal by BooleanValue("CancelHorizontal", true) { mode == "Custom" && modify && !modifyAddedMotion }
    val cancelVertical by BooleanValue("CancelVertical", true) { mode == "Custom" && modify && !modifyAddedMotion }
    val horizontalMultiplier by FloatValue("HorizontalMultiplier", 0f, 0f..1f) { mode == "Custom" && modify && (!cancelHorizontal || modifyAddedMotion) }
    val verticalMultiplier by FloatValue("VerticalMultiplier", 0f, 0f..1f) { mode == "Custom" && modify && (!cancelVertical || modifyAddedMotion) }
    val chance by FloatValue("Chance", 100f, 0f..100f) { mode == "Custom" && modify }
    val attackReduce by BooleanValue("AttackReduce", false) { mode == "Custom" }
    val attackReduceLegit by BooleanValue("AttackReduce-Legit", true) { mode == "Custom" && attackReduce }
    val attackReduceMaxHurtTime: Int by object : IntValue("AttackReduce-MaxHurtTime", 9, 1..10) {
        override fun isSupported() = mode == "Custom" && attackReduce
        override fun onChange(oldValue: Int, newValue: Int) = newValue.coerceAtLeast(attackReduceMinHurtTime)
    }
    val attackReduceMinHurtTime by object : IntValue("AttackReduce-MinHurtTime", 1, 1..10) {
        override fun isSupported() = mode == "Custom" && attackReduce && attackReduceMaxHurtTime > 1
        override fun onChange(oldValue: Int, newValue: Int) = newValue.coerceIn(1, attackReduceMaxHurtTime)
    }
    val attackReduceMultiplier by FloatValue("AttackReduce-Multiplier", 0.8f, 0f..1f) { mode == "Custom" && attackReduce }
    val jump by BooleanValue("Jump", false) { mode == "Custom" }
    val jumpMotion by FloatValue("Jump-Motion", 0.42f, 0f..0.42f) { mode == "Custom" && jump }
    val jumpFailRate by FloatValue("Jump-FailRate", 0f, 0f..100f) { mode == "Custom" && jump }
    val tickreduce by BooleanValue("TickReduce", false) { mode == "Custom" }
    val tickreduceTicks by IntValue("TickReduce-Ticks", 1, 1..10) { mode == "Custom" && tickreduce }
    val tickreduceMultiplier by FloatValue("TickReduce-Multiplier", 0f, 0f..1f) { mode == "Custom" && tickreduce }
    val tickreduceVertical by BooleanValue("TickReduce-Vertical", false) { mode == "Custom" && tickreduce }
    val tickreduceHorizontal by BooleanValue("TickReduce-Horizontal", false) { mode == "Custom" && tickreduce }
    val reverse by BooleanValue("Reverse", false) { mode == "Custom" }
    val onLook by BooleanValue("onLook", false) { mode == "Custom" && reverse }
    val range by FloatValue("Range", 3.0F, 1F..5.0F) { onLook && reverse }
    val maxAngleDifference by DoubleValue("MaxAngleDifference", 45.0, 5.0..90.0) { onLook && reverse }
    val reverseSmooth by BooleanValue("Reverse-Smooth", false) { mode == "Custom" && reverse }
    val reverseNoGround by BooleanValue("Reverse-NoGround", true) { mode == "Custom" && reverse }
    val reverseTicks by IntValue("Reverse-StrafeTicks", 1, 1..20) { mode == "Custom" && reverse }
    val reverseStrength by FloatValue("Reverse-Strength", 1f, 0.02f..1f) { mode == "Custom" && reverse }

    val spoofDelay by IntValue("SpoofDelay", 500, 0..5000) { mode == "Delayed" }

    val grimAlways by BooleanValue("Grim-Always", false) { mode == "Grim" }
    val grimOnlyAir by BooleanValue("Grim-OnlyBreakAir", true) { mode == "Grim" }
    val grimWorld by BooleanValue("Grim-BreakOnWorld", false) { mode == "Grim" }
    val grimFlagPause by LongValue("Grim-FlagPauseTime", 10, 0L..1000L) { mode == "Grim" }
    val grimPacket by ListValue("Grim-Packet", arrayOf("Flying", "Position", "Rotation", "Full", "Tick", "None"), "Tick") { mode == "Grim" }
    val grimTimerMode by ListValue("Grim-TimerMode", arrayOf("New", "Old", "Off"), "New") { mode == "Grim" && grimPacket != "None" && grimPacket != "Tick" }
    val grimTimerTicks by IntValue("Grim-TimerTicks", 20, 1..100) { mode == "Grim" && grimPacket != "None" && grimPacket != "Tick" && grimTimerMode != "Off" }
    val grimTimerSpeed by FloatValue("Grim-TimerSpeed", 0.8f, 0f..1f) { mode == "Grim" && grimPacket != "None" && grimPacket != "Tick" && grimTimerMode != "Off" }

    val aacv4MotionReducer by FloatValue("AACv4MotionReducer", 0.62f, 0f..1f) { mode == "AACv4" }

    val aacHorizontal by FloatValue("AAC-HorizontalMultiplier", 0f, 0f..1f) { mode == "AAC" }
    val aacVertical by FloatValue("AAC-VerticalMultiplier", 0f, 0f..1f) { mode == "AAC" }

    val aacPushXZReducer by FloatValue("AACPushXZReducer", 2f, 1f..3f) { mode == "AACPush" }
    val aacPushYReducer by BooleanValue("AACPushYReducer", true) { mode == "AACPush" }

    val maxHurtTime: IntValue = object : IntValue("MaxHurtTime", 9, 1..10) {
        override fun isSupported() = mode == "GhostBlock"
        override fun onChange(oldValue: Int, newValue: Int) = newValue.coerceAtLeast(minHurtTime.get())
    }

    val minHurtTime: IntValue = object : IntValue("MinHurtTime", 1, 1..10) {
        override fun isSupported() = mode == "GhostBlock" && !maxHurtTime.isMinimal
        override fun onChange(oldValue: Int, newValue: Int) = newValue.coerceIn(0, maxHurtTime.get())
    }

    var velocityTick = 0
    val velocityTimer = MSTimer()

    val delayMode get() = mode == "Delayed"

    override fun onDisable() {
        mc.thePlayer.speedInAir = 0.02f
        mc.timer.timerSpeed = 1f
        modeModule.onDisable()
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
        modeModule.onAttack(event)
    }

    @EventTarget(priority = 1)
    fun onPacket(event: PacketEvent) {
        modeModule.onPacket(event)
        val packet = event.packet

        if (noFire && mc.thePlayer.isBurning) return
        if (onlyGround && !mc.thePlayer.onGround) return

        if (packet is S27PacketExplosion && explosions && (packet.field_149152_f != 0f || packet.field_149153_g != 0f || packet.field_149159_h != 0f)
            || packet is S12PacketEntityVelocity && packet.entityID == mc.thePlayer.entityId
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

    @EventTarget
    fun onWorld(event: WorldEvent) {
        modeModule.onWorld(event)
    }

    @EventTarget
    fun onGameLoop(event: GameLoopEvent) {
        modeModule.onGameLoop(event)
    }
}
