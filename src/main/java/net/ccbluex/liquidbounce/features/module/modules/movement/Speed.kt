/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement

import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory.MOVEMENT
import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.SpeedMode
import net.ccbluex.liquidbounce.utils.ClassUtils.getAllObjects
import net.ccbluex.liquidbounce.utils.MovementUtils.isMoving
import net.ccbluex.liquidbounce.utils.extensions.inLiquid
import net.ccbluex.liquidbounce.utils.extensions.resetSpeed
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.ListValue

object Speed : Module("Speed", MOVEMENT) {
    private val speedModes = this.javaClass.`package`.getAllObjects<SpeedMode>().sortedBy { it.modeName }

    private val moduleModes = speedModes.map { it.modeName }.toTypedArray()

    private val alwaysSprint by BoolValue("AlwaysSprint", false)
    private val whenSneaking by BoolValue("WhenSneaking", false)
    private val inLiquid by BoolValue("InLiquid", false)
    private val inWeb by BoolValue("InWeb", false)
    private val onLadder by BoolValue("OnLadder", false)
    private val whenRiding by BoolValue("WhenRiding", false)

    private val normalMode by ListValue("NormalMode", moduleModes, "NCPBHop")
    private val jumpingMode by ListValue("JumpingMode", arrayOf("None") + moduleModes, "None")

    val customSpeed by FloatValue("Custom-Speed", 1.6f, 0.2f..2f) { modes contains "Custom" }
    val customY by FloatValue("Custom-Y", 0f, 0f..4f) { modes contains "Custom" }
    val customTimer by FloatValue("Custom-Timer", 1f, 0.1f..2f) { modes contains "Custom" }
    val customStrafe by BoolValue("Custom-Strafe", true) { modes contains "Custom" }
    val resetXZ by BoolValue("Custom-ResetXZ", false) { modes contains "Custom" }
    val resetY by BoolValue("Custom-ResetY", false) { modes contains "Custom" }

    val aacPortLength by FloatValue("AAC-PortLength", 1f, 1f..20f) { modes contains "AACPort" }
    val aacGroundTimer by FloatValue("AACGround-Timer", 3f, 1.1f..10f) { modes contains arrayOf("AACGround", "AACGround2") }
    val cubecraftPortLength by FloatValue("CubeCraft-PortLength", 1f, 0.1f..2f) { modes contains "TeleportCubeCraft" }
    val mineplexGroundSpeed by FloatValue("MineplexGround-Speed", 0.5f, 0.1f..1f) { modes contains "MineplexGround" }
    val cardinalStrafeHeight by FloatValue("Cardinal-StrafeHeight", 0.3f, 0.1f..1f) { modes contains "Cardinal" }
    val cardinalStrafeStrength by FloatValue("Cardinal-StrafeStrength", 0.1f, 0f..0.5f) { modes contains "Cardinal" }
    val cardinalAboveWaterMultiplier by FloatValue("Cardinal-AboveWaterMultiplier", 0.87f, 0.4f..1f) { modes contains "Cardinal" }
    val cardinalSlimeMultiplier by FloatValue("Cardinal-SlimeMultiplier", 0.7f, 0.4f..1f) { modes contains "Cardinal" }
    val cardinalJumpWhenIceSpeed by BoolValue("Cardinal-JumpWhenIceSpeed", true) { modes contains "Cardinal" }
    val uncpyportDamageBoost by BoolValue("UNCPYPort-DamageBoost", true) { modes contains "UNCPYPort" }
    val wavelowhopTimer by FloatValue("WaveLowHop-Timer", 1.25f, 1f..2f) { modes contains "WaveLowHop" }
    val matrix2SprintBypass by BoolValue("Matrix2-SprintBypass", false) { modes contains "Matrix2" } // Useless, but I'll keep it anyway
    val vulcanhopFast by BoolValue("VulcanHop-Fast", true) { mode == "VulcanHop" }

    var mode = normalMode

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        val thePlayer = mc.thePlayer ?: return

        if (jumpingMode != "None") {
            val last = modeModule
            mode = if (mc.gameSettings.keyBindJump.pressed && !mc.thePlayer.inLiquid) jumpingMode else normalMode
            if (mode != last.modeName) {
                last.onDisable()
                modeModule.onEnable()
            }
        } else mode = normalMode

        if (!modeModule.allowsJumping && !mc.thePlayer.inLiquid)
            mc.gameSettings.keyBindJump.pressed = false

        if (!shouldSpeed)
            return

        if (isMoving && !alwaysSprint)
            thePlayer.isSprinting = true

        modeModule.onUpdate()
    }

    @EventTarget
    fun onMotion(event: MotionEvent) {
        val thePlayer = mc.thePlayer ?: return

        if (!shouldSpeed || event.eventState != EventState.PRE)
            return

        if (isMoving && !alwaysSprint)
            thePlayer.isSprinting = true

        modeModule.onMotion(event)
    }

    @EventTarget
    fun onMove(event: MoveEvent) {
        if (!shouldSpeed)
            return

        modeModule.onMove(event)
    }

    @EventTarget
    fun onTick(event: TickEvent) {
        if (!shouldSpeed)
            return

        modeModule.onTick()
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        modeModule.onPacket(event)
    }

    @EventTarget
    fun onStrafe(event: StrafeEvent) {
        if (!shouldSpeed)
            return

        modeModule.onStrafe()
    }

    override fun onEnable() {
        mc.thePlayer ?: return

        mc.timer.resetSpeed()

        modeModule.onEnable()
    }

    override fun onDisable() {
        mc.thePlayer ?: return

        mc.timer.resetSpeed()

        modeModule.onDisable()
    }

    override val tag
        get() = mode

    private val modeModule
        get() = speedModes.find { it.modeName == mode }!!

    private val modes get() = arrayOf(normalMode, jumpingMode)

    private val shouldSpeed: Boolean
        get() {
            val shouldSpeed = (inLiquid || !mc.thePlayer.inLiquid)
                    && (whenSneaking || !mc.thePlayer.isSneaking)
                    && (inWeb || !mc.thePlayer.isInWeb)
                    && (onLadder || !mc.thePlayer.isOnLadder)
                    && (whenRiding || !mc.thePlayer.isRiding)
                    && mc.thePlayer != null

            if (!shouldSpeed)
                mc.timer.resetSpeed()

            return shouldSpeed
        }

    private infix fun Array<String>.contains(other: Array<String>): Boolean = this.any { it in other }
    private infix fun Array<String>.contains(other: String): Boolean = other in this
}
