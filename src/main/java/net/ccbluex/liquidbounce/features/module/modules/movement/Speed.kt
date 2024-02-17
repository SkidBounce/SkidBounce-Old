/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement

import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.aac.*
import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.ncp.*
import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.other.*
import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.spartan.*
import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.spectre.*
import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.verus.*
import net.ccbluex.liquidbounce.utils.MovementUtils.isMoving
import net.ccbluex.liquidbounce.utils.extensions.resetSpeed
import net.ccbluex.liquidbounce.value.*

object Speed : Module("Speed", ModuleCategory.MOVEMENT) {

    private val speedModes = arrayOf(

        // NCP
        NCPBHop,
        NCPFHop,
        SNCPBHop,
        NCPHop,
        NCPYPort,
        UNCPHop,
        UNCPYPort,
        UNCPHop2,

        // YPort
        YPort,
        YPort2,

        // AAC
        AACBHop,
        AAC2BHop,
        AAC3BHop,
        AACv4BHop,
        AAC4BHop,
        AAC4Hop,
        AAC4SlowHop,
        AAC5BHop,
        AAC6BHop,
        AAC7BHop,
        AACHop3310,
        AACHop3313,
        AACHop350,
        AACHop438,
        OldAACBHop,
        AACLowHop,
        AACLowHop2,
        AACLowHop3,
        AACGround,
        AACGround2,
        AACYPort,
        AACYPort2,
        AACPort,

        // Spartan
        SpartanYPort,

        // Spectre
        SpectreLowHop,
        SpectreBHop,
        SpectreOnGround,

        // Verus
        VerusHop,
        VerusLowHop,
        NewVerusLowHop,

        // Server specific
        HypixelHop,
        TeleportCubeCraft,
        HiveHop,
        Mineplex,
        MineplexGround,

        // Other
        Cardinal,
        Matrix,
        Boost,
        Frame,
        MiJump,
        OnGround,
        SlowHop,
        Legit,
        CustomSpeed,
        MineBlazeHop,
        MineBlazeTimer,
        WaveLowHop,
        AEMine,
    ).sortedBy { it.modeName }

    private val modes = speedModes.map { it.modeName }.toTypedArray()

    val mode by object : ListValue("Mode", modes, "NCPBHop") {
        override fun onChange(oldValue: String, newValue: String): String {
            if (state)
                onDisable()

            return super.onChange(oldValue, newValue)
        }

        override fun onChanged(oldValue: String, newValue: String) {
            if (state)
                onEnable()
        }
    }
    val customSpeed by FloatValue("CustomSpeed", 1.6f, 0.2f..2f) { mode == "Custom" }
    val customY by FloatValue("CustomY", 0f, 0f..4f) { mode == "Custom" }
    val customTimer by FloatValue("CustomTimer", 1f, 0.1f..2f) { mode == "Custom" }
    val customStrafe by BoolValue("CustomStrafe", true) { mode == "Custom" }
    val resetXZ by BoolValue("CustomResetXZ", false) { mode == "Custom" }
    val resetY by BoolValue("CustomResetY", false) { mode == "Custom" }

    val aacPortLength by FloatValue("AAC-PortLength", 1f, 1f..20f) { mode == "AACPort" }
    val aacGroundTimer by FloatValue("AACGround-Timer", 3f, 1.1f..10f) { mode in arrayOf("AACGround", "AACGround2") }
    val cubecraftPortLength by FloatValue("CubeCraft-PortLength", 1f, 0.1f..2f) { mode == "TeleportCubeCraft" }
    val mineplexGroundSpeed by FloatValue("MineplexGround-Speed", 0.5f, 0.1f..1f) { mode == "MineplexGround" }
    val cardinalStrafeHeight by FloatValue("Cardinal-StrafeHeight", 0.3f, 0.1f..1f) { mode == "Cardinal" }
    val cardinalStrafeStrength by FloatValue("Cardinal-StrafeStrength", 0.1f, 0f..0.5f) { mode == "Cardinal" }
    val cardinalAboveWaterMultiplier by FloatValue("Cardinal-AboveWaterMultiplier", 0.87f, 0.4f..1f) { mode == "Cardinal" }
    val cardinalSlimeMultiplier by FloatValue("Cardinal-SlimeMultiplier", 0.7f, 0.4f..1f) { mode == "Cardinal" }
    val cardinalJumpWhenIceSpeed by BoolValue("Cardinal-JumpWhenIceSpeed", true) { mode == "Cardinal" }
    val uncpyportDamageBoost by BoolValue("UNCPYPort-DamageBoost", true) { mode == "UNCPYPort" }
    val wavelowhopTimer by FloatValue("WaveLowHop-Timer", 1.25f, 1f..2f) { mode == "WaveLowHop" }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        val thePlayer = mc.thePlayer ?: return

        if (thePlayer.isSneaking)
            return

        if (isMoving && !sprintManually)
            thePlayer.isSprinting = true

        modeModule.onUpdate()
    }

    @EventTarget
    fun onMotion(event: MotionEvent) {
        val thePlayer = mc.thePlayer ?: return

        if (thePlayer.isSneaking || event.eventState != EventState.PRE)
            return

        if (isMoving && !sprintManually)
            thePlayer.isSprinting = true

        modeModule.onMotion(event)
    }

    @EventTarget
    fun onMove(event: MoveEvent) {
        if (mc.thePlayer.isSneaking)
            return

        modeModule.onMove(event)
    }

    @EventTarget
    fun onTick(event: TickEvent) {
        if (mc.thePlayer.isSneaking)
            return

        modeModule.onTick()
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        modeModule.onPacket(event)
    }

    @EventTarget
    fun onStrafe(event: StrafeEvent) {
        if (mc.thePlayer.isSneaking)
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

    private val sprintManually
        // Maybe there are more but for now there's the Legit mode.
        get() = modeModule in arrayOf(Legit)
}
