/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement

import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.modules.movement.Speed.contains
import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.SpeedMode
import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.aac.*
import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.ncp.*
import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.other.*
import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.spartan.*
import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.spectre.*
import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.verus.*
import net.ccbluex.liquidbounce.utils.ClientUtils.displayChatMessage
import net.ccbluex.liquidbounce.utils.MovementUtils.isMoving
import net.ccbluex.liquidbounce.utils.extensions.*
import net.ccbluex.liquidbounce.value.*
import net.minecraft.client.settings.KeyBinding

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
        VerusLowHop2,
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

    private val moduleModes = speedModes.map { it.modeName }.toTypedArray()

    private val normalMode: String by object : ListValue("NormalMode", moduleModes, "NCPBHop") {
        override fun onChange(oldValue: String, newValue: String): String {
            if (state && mode == normalMode)
                onDisable()

            return super.onChange(oldValue, newValue)
        }

        override fun onChanged(oldValue: String, newValue: String) {
            if (state && mode == normalMode)
                onEnable()
        }
    }
    private val jumpingMode: String by object : ListValue("JumpingMode", arrayOf("None") + moduleModes, "None") {
        override fun onChange(oldValue: String, newValue: String): String {
            if (state && mode == jumpingMode)
                onDisable()

            return super.onChange(oldValue, newValue)
        }

        override fun onChanged(oldValue: String, newValue: String) {
            if (state && mode == jumpingMode)
                onEnable()
        }
    }

    val customSpeed by FloatValue("CustomSpeed", 1.6f, 0.2f..2f) { modes contains "Custom" }
    val customY by FloatValue("CustomY", 0f, 0f..4f) { modes contains "Custom" }
    val customTimer by FloatValue("CustomTimer", 1f, 0.1f..2f) { modes contains "Custom" }
    val customStrafe by BoolValue("CustomStrafe", true) { modes contains "Custom" }
    val resetXZ by BoolValue("CustomResetXZ", false) { modes contains "Custom" }
    val resetY by BoolValue("CustomResetY", false) { modes contains "Custom" }

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

    var mode = normalMode

    @Suppress("UNUSED_PARAMETER")
    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        val thePlayer = mc.thePlayer ?: return

        if (jumpingMode != "None" && !mc.thePlayer.inLiquid) {
            val last = modeModule
            mode = if (mc.gameSettings.keyBindJump.pressed) jumpingMode else normalMode
            if (mode != last.modeName) {
                last.onDisable()
                modeModule.onEnable()
            }
        }

        if (!modeModule.allowsJumping) {
            if (!mc.thePlayer.inLiquid)
                mc.gameSettings.keyBindJump.pressed = false
            else if (mc.thePlayer.onGround)
                mc.gameSettings.keyBindJump.pressed = false
        }

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

    @Suppress("UNUSED_PARAMETER")
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

    @Suppress("UNUSED_PARAMETER")
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

    private val modes get() = arrayOf(normalMode, jumpingMode)
    private infix fun Array<String>.contains(other: Array<String>): Boolean = this.any { it in other }
    private infix fun Array<String>.contains(other: String): Boolean = other in this
}
