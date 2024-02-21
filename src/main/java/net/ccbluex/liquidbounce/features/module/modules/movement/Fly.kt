/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement

import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.modules.movement.flymodes.aac.*
import net.ccbluex.liquidbounce.features.module.modules.movement.flymodes.hypixel.*
import net.ccbluex.liquidbounce.features.module.modules.movement.flymodes.ncp.*
import net.ccbluex.liquidbounce.features.module.modules.movement.flymodes.other.*
import net.ccbluex.liquidbounce.features.module.modules.movement.flymodes.rewinside.*
import net.ccbluex.liquidbounce.features.module.modules.movement.flymodes.spartan.*
import net.ccbluex.liquidbounce.features.module.modules.movement.flymodes.vanilla.*
import net.ccbluex.liquidbounce.utils.PacketUtils.sendPacket
import net.ccbluex.liquidbounce.utils.extensions.resetSpeed
import net.ccbluex.liquidbounce.utils.extensions.stop
import net.ccbluex.liquidbounce.utils.extensions.stopXZ
import net.ccbluex.liquidbounce.utils.render.RenderUtils.drawPlatform
import net.ccbluex.liquidbounce.utils.timing.MSTimer
import net.ccbluex.liquidbounce.value.*
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition
import net.minecraft.util.AxisAlignedBB
import java.awt.Color

object Fly : Module("Fly", ModuleCategory.MOVEMENT) {
    private val flyModes = arrayOf(
        Vanilla, SmoothVanilla,

        // NCP
        OldNCP,

        // AAC
        AAC1910, AAC305, AAC316, AAC3312, AAC3313,

        // CubeCraft
        CubeCraft,

        // Hypixel
        Hypixel, BoostHypixel, FreeHypixel,

        // Rewinside
        Rewinside, TeleportRewinside,

        // Other server specific flys
        Mineplex, Minesucht, Redesky,
        // Spartan
        Spartan, Spartan2, BugSpartan,

        // Other anti-cheats
        MineSecure, HawkEye, HAC, WatchCat, Verus, Vulcan, Wave,

        // Other
        Jetpack, KeepAlive, Collide, Jump, Flag, Clip
    ).sortedBy { it.modeName }

    private val modes = flyModes.map { it.modeName }.toTypedArray()

    val mode by ListValue("Mode", modes, "Vanilla")

    val vanillaSpeed by FloatValue("Vanilla-Speed", 2f, 0f..10f)
        { mode in arrayOf("Vanilla", "KeepAlive", "MineSecure", "BugSpartan") }
    private val vanillaKickBypass by BoolValue("Vanilla-KickBypass", false)
        { mode in arrayOf("Vanilla", "SmoothVanilla") }

    // AAC
    val aacSpeed by FloatValue("AAC1.9.10-Speed", 0.3f, 0f..1f) { mode == "AAC1.9.10" }
    val aacFast by BoolValue("AAC3.0.5-Fast", true) { mode == "AAC3.0.5" }
    val aacMotion by FloatValue("AAC3.3.12-Motion", 10f, 0.1f..10f) { mode == "AAC3.3.12" }
    val aacMotion2 by FloatValue("AAC3.3.13-Motion", 10f, 0.1f..10f) { mode == "AAC3.3.13" }

    // Hypixel
    val hypixelBoost by BoolValue("Hypixel-Boost", true) { mode == "Hypixel" }
    val hypixelBoostDelay by IntegerValue("Hypixel-BoostDelay", 1200, 50..2000)
        { mode == "Hypixel" && hypixelBoost }
    val hypixelBoostTimer by FloatValue("Hypixel-BoostTimer", 1f, 0.1f..5f)
        { mode == "Hypixel" && hypixelBoost }

    // Vulcan
    val vulcanTimer by FloatValue("Vulcan-Timer", 2f, 1f..3f) { mode == "Vulcan" }
    val vulcanNoClip by BoolValue("Vulcan-NoClip", true) { mode == "Vulcan" }

    // Mineplex
    val mineplexSpeed by FloatValue("Mineplex-Speed", 1f, 0.5f..10f) { mode == "Mineplex" }

    // Redesky
    val redeskyHeight by FloatValue("Redesky-Height", 4f, 1f..7f) { mode == "Redesky" }

    // Wave
    val waveUpSpeed by FloatValue("Wave-UpSpeed", 0.6f, 0f..1f)  { mode == "Wave" }
    val waveDownSpeed by FloatValue("Wave-DownSpeed", 0.6f, 0f..1f)  { mode == "Wave" }
    val waveTimer by FloatValue("Wave-Timer", 1.25f, 1f..2f) { mode == "Wave" }

    // Clip
    val clipX by FloatValue("Clip-X", 2f, -5f..5f) { mode == "Clip" }
    val clipY by FloatValue("Clip-Y", 2f, -5f..5f) { mode == "Clip" }
    val clipZ by FloatValue("Clip-Z", 2f, -5f..5f) { mode == "Clip" }
    val clipDelay by IntegerValue("Clip-Delay", 500, 0..3000) { mode == "Clip" }
    val clipTimer by FloatValue("Clip-Timer", 1f, 0.01f..3f) { mode == "Clip" }
    val clipMotionX by FloatValue("Clip-MotionX", 0f, -1f..1f) { mode == "Clip" }
    val clipMotionY by FloatValue("Clip-MotionY", 0f, -1f..1f) { mode == "Clip" }
    val clipMotionZ by FloatValue("Clip-MotionZ", 0f, -1f..1f) { mode == "Clip" }
    val clipGroundSpoof by BoolValue("Clip-GroundSpoof", false) { mode == "Clip" }
    val clipGround by BoolValue("Clip-GroundWhenClip", false) { mode == "Clip" }

    // Verus
    val damage by BoolValue("Damage", false)
    val timerSlow by BoolValue("TimerSlow", true)
    val boostTicksValue by IntegerValue("BoostTicks", 20, 1..30)
    val boostMotion by FloatValue("BoostMotion", 6.5f, 1f..9.85f)
    val yBoost by FloatValue("YBoost", 0.42f, 0f..10f)

    private val mark by BoolValue("Mark", true, subjective = true)

    var jumpY = 0.0

    var startY = 0.0
        private set

    private val groundTimer = MSTimer()
    private var wasFlying = false

    override fun onEnable() {
        val thePlayer = mc.thePlayer ?: return

        startY = thePlayer.posY
        jumpY = thePlayer.posY
        wasFlying = mc.thePlayer.capabilities.isFlying

        modeModule.onEnable()
    }

    override fun onDisable() {
        val thePlayer = mc.thePlayer ?: return

        if (!mode.startsWith("AAC") && mode != "Hypixel" && mode != "SmoothVanilla" && mode != "Vanilla" && mode != "Rewinside" && mode != "Collide" && mode != "Jump") {
            if (mode == "CubeCraft") thePlayer.stopXZ()
            else thePlayer.stop()
        }

        thePlayer.capabilities.isFlying = wasFlying
        mc.timer.resetSpeed()
        thePlayer.speedInAir = 0.02f

        modeModule.onDisable()
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        modeModule.onUpdate()
    }

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        if (!mark || mode == "Vanilla" || mode == "SmoothVanilla")
            return

        val y = startY + 2.0 + (if (mode == "BoostHypixel") 0.42 else 0.0)
        drawPlatform(
            y,
            if (mc.thePlayer.entityBoundingBox.maxY < y) Color(0, 255, 0, 90) else Color(255, 0, 0, 90),
            1.0
        )

        modeModule.onRender3D(event)
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        mc.thePlayer ?: return

        modeModule.onPacket(event)
    }

    @EventTarget
    fun onBB(event: BlockBBEvent) {
        mc.thePlayer ?: return

        modeModule.onBB(event)
    }

    @EventTarget
    fun onJump(event: JumpEvent) {
        modeModule.onJump(event)
    }

    @EventTarget
    fun onStep(event: StepEvent) {
        modeModule.onStep(event)
    }

    @EventTarget
    fun onMotion(event: MotionEvent) {
        modeModule.onMotion(event)
    }

    @EventTarget
    fun onMove(event: MoveEvent) {
        modeModule.onMove(event)
    }
    @EventTarget
    fun onWorld(event: WorldEvent) {
        // breaks when you join a server with it on for some reason
        if (mode == "Vulcan")
            state = false
    }

    fun handleVanillaKickBypass() {
        if (!vanillaKickBypass || !groundTimer.hasTimePassed(1000)) return
        val ground = calculateGround() + 0.5
        run {
            var posY = mc.thePlayer.posY
            while (posY > ground) {
                sendPacket(C04PacketPlayerPosition(mc.thePlayer.posX, posY, mc.thePlayer.posZ, true))
                if (posY - 8.0 < ground) break // Prevent next step
                posY -= 8.0
            }
        }
        sendPacket(C04PacketPlayerPosition(mc.thePlayer.posX, ground, mc.thePlayer.posZ, true))
        var posY = ground
        while (posY < mc.thePlayer.posY) {
            sendPacket(C04PacketPlayerPosition(mc.thePlayer.posX, posY, mc.thePlayer.posZ, true))
            if (posY + 8.0 > mc.thePlayer.posY) break // Prevent next step
            posY += 8.0
        }
        sendPacket(C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true))
        groundTimer.reset()
    }

    // TODO: Make better and faster calculation lol
    private fun calculateGround(): Double {
        val playerBoundingBox = mc.thePlayer.entityBoundingBox
        var blockHeight = 0.05
        var ground = mc.thePlayer.posY
        while (ground > 0.0) {
            val customBox = AxisAlignedBB.fromBounds(
                playerBoundingBox.maxX,
                ground + blockHeight,
                playerBoundingBox.maxZ,
                playerBoundingBox.minX,
                ground,
                playerBoundingBox.minZ
            )
            if (mc.theWorld.checkBlockCollision(customBox)) {
                if (blockHeight <= 0.05) return ground + blockHeight
                ground += blockHeight
                blockHeight = 0.05
            }
            ground -= blockHeight
        }
        return 0.0
    }

    override val tag
        get() = mode

    private val modeModule
        get() = flyModes.find { it.modeName == mode }!!
}
