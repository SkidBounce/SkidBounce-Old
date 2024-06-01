/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.events.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory.MOVEMENT
import net.ccbluex.liquidbounce.features.module.modules.movement.flymodes.FlyMode
import net.ccbluex.liquidbounce.utils.ClassUtils.getAllObjects
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

object Fly : Module("Fly", MOVEMENT) {
    private val flyModes = javaClass.`package`.getAllObjects<FlyMode>().sortedBy { it.modeName }

    private val modes = flyModes.map { it.modeName }.toTypedArray()

    val mode by ListValue("Mode", modes, "Vanilla")

    val vanillaSpeed by FloatValue("Vanilla-Speed", 2f, 0f..10f)
    { mode in arrayOf("Vanilla", "KeepAlive", "MineSecure", "BugSpartan") }
    private val vanillaKickBypass by BooleanValue("Vanilla-KickBypass", false)
    { mode in arrayOf("Vanilla", "SmoothVanilla", "LoyisaAGC") }

    // AGC
    val agcHorizontal by FloatValue("AGC-Horizontal", 2f, 0f..10f) { mode == "LoyisaAGC" }
    val agcVertical by DoubleValue("AGC-Vertical", 2.0, 0.0..10.0) { mode == "LoyisaAGC" }

    // Spartan532
    val spartan532Speed by FloatValue("Spartan532-Speed", 5f, 0f..10f) { mode == "Spartan532" }
    val spartan532Timer by FloatValue("Spartan532-Timer", 0.4f, 0.01f..1f) { mode == "Spartan532" }

    // AAC
    val aacSpeed by FloatValue("AAC1.9.10-Speed", 0.3f, 0f..1f) { mode == "AAC1.9.10" }
    val aacFast by BooleanValue("AAC3.0.5-Fast", true) { mode == "AAC3.0.5" }
    val aacMotion by FloatValue("AAC3.3.12-Motion", 10f, 0.1f..10f) { mode == "AAC3.3.12" }
    val aacMotion2 by FloatValue("AAC3.3.13-Motion", 10f, 0.1f..10f) { mode == "AAC3.3.13" }

    // Hypixel
    val hypixelBoost by BooleanValue("Hypixel-Boost", true) { mode == "Hypixel" }
    val hypixelBoostDelay by IntValue("Hypixel-BoostDelay", 1200, 50..2000)
    { mode == "Hypixel" && hypixelBoost }
    val hypixelBoostTimer by FloatValue("Hypixel-BoostTimer", 1f, 0.1f..5f)
    { mode == "Hypixel" && hypixelBoost }

    // Vulcan
    val vulcanTimer by FloatValue("Vulcan-Timer", 2f, 1f..3f) { mode == "Vulcan" }
    val vulcanNoClip by BooleanValue("Vulcan-NoClip", true) { mode == "Vulcan" }

    // Mineplex
    val mineplexSpeed by FloatValue("Mineplex-Speed", 1f, 0.5f..10f) { mode == "Mineplex" }

    // Redesky
    val redeskyHeight by FloatValue("Redesky-Height", 4f, 1f..7f) { mode == "Redesky" }

    // Wave
    val waveUpSpeed by FloatValue("Wave-UpSpeed", 0.6f, 0f..1f) { mode == "Wave" }
    val waveDownSpeed by FloatValue("Wave-DownSpeed", 0.6f, 0f..1f) { mode == "Wave" }
    val waveTimer by FloatValue("Wave-Timer", 1.25f, 1f..2f) { mode == "Wave" }

    // Clip
    val clipX by FloatValue("Clip-X", 2f, -5f..5f) { mode == "Clip" }
    val clipY by FloatValue("Clip-Y", 2f, -5f..5f) { mode == "Clip" }
    val clipZ by FloatValue("Clip-Z", 2f, -5f..5f) { mode == "Clip" }
    val clipDelay by IntValue("Clip-Delay", 500, 0..3000) { mode == "Clip" }
    val clipTimer by FloatValue("Clip-Timer", 1f, 0.01f..3f) { mode == "Clip" }
    val clipMotionX by FloatValue("Clip-MotionX", 0f, -1f..1f) { mode == "Clip" }
    val clipMotionY by FloatValue("Clip-MotionY", 0f, -1f..1f) { mode == "Clip" }
    val clipMotionZ by FloatValue("Clip-MotionZ", 0f, -1f..1f) { mode == "Clip" }
    val clipGroundSpoof by BooleanValue("Clip-GroundSpoof", false) { mode == "Clip" }
    val clipGround by BooleanValue("Clip-GroundWhenClip", false) { mode == "Clip" }

    // Clip2
    val clip2H by FloatValue("Clip2-Horizontal", 2f, 0f..10f) { mode == "Clip2" }
    val clip2Y by FloatValue("Clip2-Vertical", 2f, 0f..10f) { mode == "Clip2" }
    val clip2Max by FloatValue("Clip2-MaxDistance", 10f, 0f..20f) { mode == "Clip2" }
    val clip2Delay by IntValue("Clip2-Delay", 500, 0..3000) { mode == "Clip2" }
    val clip2Timer by FloatValue("Clip2-Timer", 1f, 0.1f..10f) { mode == "Clip2" }
    val clip2GroundSpoof by BooleanValue("Clip2-GroundSpoof", false) { mode == "Clip2" }
    val clip2GroundSpoofOnlyClip by BooleanValue("Clip2-GroundSpoof-OnlyOnClip", false) { mode == "Clip2" && clip2GroundSpoof }

    // Verus
    val damage by BooleanValue("Damage", false) { mode == "Verus" }
    val timerSlow by BooleanValue("TimerSlow", true) { mode == "Verus" }
    val boostTicksValue by IntValue("BoostTicks", 20, 1..30) { mode == "Verus" }
    val boostMotion by FloatValue("BoostMotion", 6.5f, 1f..9.85f) { mode == "Verus" }
    val yBoost by FloatValue("YBoost", 0.42f, 0f..10f) { mode == "Verus" }

    // BlocksMC
    val stable by BooleanValue("Stable", false) { mode == "BlocksMC" || mode == "BlocksMC2" }
    val timerSlowed by BooleanValue("TimerSlowed", true) { mode == "BlocksMC" || mode == "BlocksMC2" }
    val clipDistance by DoubleValue("ClipDistance", 0.05, 0.01..0.1) { mode == "BlocksMC" || mode == "BlocksMC2" }
    val boostSpeed by FloatValue("BoostSpeed", 6f, 1f..15f) { mode == "BlocksMC" || mode == "BlocksMC2" }
    val extraBoost by FloatValue("ExtraSpeed", 1f, 0.0F..2f) { mode == "BlocksMC" || mode == "BlocksMC2" }
    val stopOnLanding by BooleanValue("StopOnLanding", true) { mode == "BlocksMC" || mode == "BlocksMC2" }
    val stopOnNoMove by BooleanValue("StopOnNoMove", false) { mode == "BlocksMC" || mode == "BlocksMC2" }
    val debugFly by BooleanValue("Debug", false) { mode == "BlocksMC" || mode == "BlocksMC2" }

    private val mark by BooleanValue("Mark", true, subjective = true)

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
