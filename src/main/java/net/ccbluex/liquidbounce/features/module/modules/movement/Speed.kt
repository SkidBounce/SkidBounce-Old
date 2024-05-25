/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement

import net.ccbluex.liquidbounce.event.EventState
import net.ccbluex.liquidbounce.event.EventState.PRE
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.events.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory.MOVEMENT
import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.SpeedMode
import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.aac.AACGround
import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.aac.AACGround2
import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.aac.AACPort
import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.intave.Intave2
import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.matrix.MatrixSlow
import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.ncp.UNCPYPort
import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.other.*
import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.other.Strafe
import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.vulcan.Vulcan
import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.vulcan.Vulcan2
import net.ccbluex.liquidbounce.utils.ClassUtils.getAllObjects
import net.ccbluex.liquidbounce.utils.MovementUtils.isMoving
import net.ccbluex.liquidbounce.utils.extensions.*
import net.ccbluex.liquidbounce.value.BooleanValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.ListValue

object Speed : Module("Speed", MOVEMENT) {
    private val speedModes = javaClass.`package`.getAllObjects<SpeedMode>().sortedBy { it.modeName }

    private val moduleModes = speedModes.map { it.modeName }.toTypedArray()

    private val alwaysSprint by BooleanValue("AlwaysSprint", false)
    private val whenSneaking by BooleanValue("WhenSneaking", false)
    private val inLiquid by BooleanValue("InLiquid", false)
    private val inWeb by BooleanValue("InWeb", false)
    private val onLadder by BooleanValue("OnLadder", false)
    private val whenRiding by BooleanValue("WhenRiding", false)
    private val onFly by BooleanValue("onFly", false)

    private val normalMode by ListValue("NormalMode", moduleModes, "NCPBHop")
    private val jumpingMode by ListValue("JumpingMode", arrayOf("None") + moduleModes, "None")

    val strafeAir by FloatValue("Strafe-InAir", 1f, 0f..1f) { Strafe in modes }
    val strafeGround by FloatValue("Strafe-OnGround", 1f, 0f..1f) { Strafe in modes }
    val strafeStop by BooleanValue("Strafe-WhenNoInput", true) { Strafe in modes }

    val customSpeed by FloatValue("Custom-Speed", 1.6f, 0.2f..2f) { Custom in modes }
    val customY by FloatValue("Custom-Y", 0f, 0f..4f) { Custom in modes }
    val customTimer by FloatValue("Custom-Timer", 1f, 0.1f..2f) { Custom in modes }
    val customStrafe by BooleanValue("Custom-Strafe", true) { Custom in modes }
    val resetXZ by BooleanValue("Custom-ResetXZ", false) { Custom in modes }
    val resetY by BooleanValue("Custom-ResetY", false) { Custom in modes }

    val aacPortLength by FloatValue("AACPort-PortLength", 1f, 1f..20f) { AACPort in modes }
    val aacGroundTimer by FloatValue("AACGround-Timer", 3f, 1.1f..10f) { listOf(AACGround, AACGround2) overlapsWith modes }
    val cubecraftPortLength by FloatValue("CubeCraft-PortLength", 1f, 0.1f..2f) { TeleportCubeCraft in modes }
    val mineplexGroundSpeed by FloatValue("MineplexGround-Speed", 0.5f, 0.1f..1f) { MineplexGround in modes }
    val cardinalStrafeHeight by FloatValue("Cardinal-StrafeHeight", 0.3f, 0.1f..1f) { Cardinal in modes }
    val cardinalStrafeStrength by FloatValue("Cardinal-StrafeStrength", 0.1f, 0f..0.5f) { Cardinal in modes }
    val cardinalAboveWaterMultiplier by FloatValue("Cardinal-AboveWaterMultiplier", 0.87f, 0.4f..1f) { Cardinal in modes }
    val cardinalSlimeMultiplier by FloatValue("Cardinal-SlimeMultiplier", 0.7f, 0.4f..1f) { Cardinal in modes }
    val cardinalJumpWhenIceSpeed by BooleanValue("Cardinal-JumpWhenIceSpeed", true) { Cardinal in modes }
    val uncpyportDamageBoost by BooleanValue("UNCPYPort-DamageBoost", true) { UNCPYPort in modes }
    val wavelowhopTimer by FloatValue("WaveLowHop-Timer", 1.25f, 1f..2f) { WaveLowHop in modes }
    val matrixslowSprintBypass by BooleanValue("MatrixSlow-SprintBypass", false) { MatrixSlow in modes }
    val matrixslowFast by BooleanValue("MatrixSlow-Fast", true) { MatrixSlow in modes }
    val vulcanFast by BooleanValue("Vulcan-Fast", true) { Vulcan in modes }
    val vulcan2Fast by BooleanValue("Vulcan2-Fast", true) { Vulcan2 in modes }
    val intave2GroundStrafe by BooleanValue("Intave2-GroundStrafe", false) { Intave2 in modes }

    private var currentMode = normalMode
    private var wasSpeed = false

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        mc.thePlayer ?: return

        updateJumping()

        if (isMoving && alwaysSprint)
            mc.thePlayer.isSprinting = true

        modeModule.onUpdate()
    }

    @EventTarget
    fun onMotion(event: MotionEvent) {
        val thePlayer = mc.thePlayer ?: return

        updateJumping()

        if (event.eventState != PRE)
            return

        if (isMoving && alwaysSprint)
            thePlayer.isSprinting = true

        modeModule.onMotion(event)
    }

    @EventTarget
    fun onMove(event: MoveEvent) {
        updateJumping()
        modeModule.onMove(event)
    }

    @EventTarget
    fun onTick(event: TickEvent) {
        updateJumping()
        modeModule.onTick()
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        updateJumping()

        modeModule.onPacket(event)
    }

    @EventTarget
    fun onStrafe(event: StrafeEvent) {
        updateJumping()
        modeModule.onStrafe()
    }

    override fun onToggle(state: Boolean) {
        mc.thePlayer ?: return

        mc.timer.resetSpeed()
        mc.thePlayer.speedInAir = 0.02f
        mc.thePlayer.jumpMovementFactor = 0.02f

        modeModule.onToggle(state)
    }

    override fun onEnable() {
        mc.thePlayer ?: return
        modeModule.onEnable()
    }

    override fun onDisable() {
        mc.thePlayer ?: return
        modeModule.onDisable()
    }

    override val tag
        get() = currentMode

    private val modeModule
        get() = speedModes.find { it.modeName == currentMode }!!

    private val modes
        get() = speedModes.filter { it.modeName in arrayOf(normalMode, jumpingMode) }

    override fun handleEvents(): Boolean {
        val shouldSpeed = (inLiquid || !mc.thePlayer.inLiquid)
                && (whenSneaking || !mc.thePlayer.isSneaking)
                && (inWeb || !mc.thePlayer.isInWeb)
                && (onLadder || !mc.thePlayer.isOnLadder)
                && (whenRiding || !mc.thePlayer.isRiding)
                && mc.thePlayer != null
                && (onFly || !Fly.handleEvents())
                && super.handleEvents()

        if (shouldSpeed != wasSpeed) {
            onToggle(shouldSpeed)
            if (shouldSpeed) onEnable() else onDisable()
            wasSpeed = shouldSpeed
        }

        return shouldSpeed
    }

    private fun updateJumping() {
        mc.gameSettings.keyBindJump.update()
        if (jumpingMode != "None") {
            val last = modeModule
            currentMode = if (mc.gameSettings.keyBindJump.isActuallyPressed && !mc.thePlayer.inLiquid) jumpingMode else normalMode
            if (currentMode != last.modeName) {
                last.onDisable()
                modeModule.onEnable()
            }
        } else currentMode = normalMode

        if (!modeModule.allowsJumping && !mc.thePlayer.inLiquid)
            mc.gameSettings.keyBindJump.pressed = false
    }
}
