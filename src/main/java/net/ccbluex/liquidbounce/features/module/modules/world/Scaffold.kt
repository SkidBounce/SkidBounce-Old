/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.world

import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.event.events.*
import net.ccbluex.liquidbounce.event.EventState.POST
import net.ccbluex.liquidbounce.event.EventState.PRE
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory.WORLD
import net.ccbluex.liquidbounce.features.module.modules.movement.Fly
import net.ccbluex.liquidbounce.features.module.modules.movement.Speed
import net.ccbluex.liquidbounce.features.module.modules.render.BlockOverlay
import net.ccbluex.liquidbounce.ui.font.Fonts.font40
import net.ccbluex.liquidbounce.utils.*
import net.ccbluex.liquidbounce.utils.CPSCounter.MouseButton.RIGHT
import net.ccbluex.liquidbounce.utils.CPSCounter.registerClick
import net.ccbluex.liquidbounce.utils.MovementUtils.JUMP_HEIGHT
import net.ccbluex.liquidbounce.utils.MovementUtils.isMoving
import net.ccbluex.liquidbounce.utils.MovementUtils.speed
import net.ccbluex.liquidbounce.utils.MovementUtils.strafe
import net.ccbluex.liquidbounce.utils.PacketUtils.sendPacket
import net.ccbluex.liquidbounce.utils.PacketUtils.sendPackets
import net.ccbluex.liquidbounce.utils.RotationUtils.currentRotation
import net.ccbluex.liquidbounce.utils.RotationUtils.faceBlock
import net.ccbluex.liquidbounce.utils.RotationUtils.getRotationDifference
import net.ccbluex.liquidbounce.utils.RotationUtils.getVectorForRotation
import net.ccbluex.liquidbounce.utils.RotationUtils.keepLength
import net.ccbluex.liquidbounce.utils.RotationUtils.setTargetRotation
import net.ccbluex.liquidbounce.utils.RotationUtils.toRotation
import net.ccbluex.liquidbounce.utils.block.BlockUtils
import net.ccbluex.liquidbounce.utils.block.BlockUtils.canBeClicked
import net.ccbluex.liquidbounce.utils.block.BlockUtils.isReplaceable
import net.ccbluex.liquidbounce.utils.block.PlaceInfo
import net.ccbluex.liquidbounce.utils.extensions.*
import net.ccbluex.liquidbounce.utils.inventory.InventoryUtils.BLOCK_BLACKLIST
import net.ccbluex.liquidbounce.utils.inventory.InventoryUtils.findBlockInHotbar
import net.ccbluex.liquidbounce.utils.inventory.InventoryUtils.findLargestBlockStackInHotbar
import net.ccbluex.liquidbounce.utils.inventory.InventoryUtils.serverSlot
import net.ccbluex.liquidbounce.utils.misc.RandomUtils.nextFloat
import net.ccbluex.liquidbounce.utils.misc.RandomUtils.nextInt
import net.ccbluex.liquidbounce.utils.render.RenderUtils.drawBlockBox
import net.ccbluex.liquidbounce.utils.render.RenderUtils.drawBorderedRect
import net.ccbluex.liquidbounce.utils.timing.*
import net.ccbluex.liquidbounce.utils.timing.TimeUtils.randomClickDelay
import net.ccbluex.liquidbounce.utils.timing.TimeUtils.randomDelay
import net.ccbluex.liquidbounce.value.*
import net.minecraft.block.BlockBush
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager.resetColor
import net.minecraft.init.Blocks.air
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition
import net.minecraft.network.play.client.C0BPacketEntityAction
import net.minecraft.network.play.client.C0BPacketEntityAction.Action.START_SNEAKING
import net.minecraft.network.play.client.C0BPacketEntityAction.Action.STOP_SNEAKING
import net.minecraft.util.*
import net.minecraft.util.EnumFacing.Axis.Y
import net.minecraft.util.EnumFacing.Axis.Z
import net.minecraft.util.EnumFacing.UP
import net.minecraft.util.MathHelper.wrapAngleTo180_float
import net.minecraft.util.MovingObjectPosition.MovingObjectType.BLOCK
import net.minecraft.world.WorldSettings.GameType.SPECTATOR
import net.minecraftforge.event.ForgeEventFactory
import org.lwjgl.opengl.GL11.*
import java.awt.Color
import java.awt.Color.BLACK
import java.awt.Color.WHITE
import java.lang.System.currentTimeMillis
import javax.vecmath.Color3f
import kotlin.math.*

object Scaffold : Module("Scaffold", WORLD) {

    /**
     * TOWER MODES & SETTINGS
     */

    // TODO: (Scaffold & Tower) Optimize & Improve Code

    // -->

    private val towerMode by ListValue(
        "TowerMode",
        arrayOf(
            "None",
            "Jump",
            "MotionJump",
            "Motion",
            "ConstantMotion",
            "MotionTP",
            "Packet",
            "Teleport",
            "AAC3.3.9",
            "AAC3.6.4"
        ),
        "None"
    )

    private val stopWhenBlockAbove by BooleanValue("StopWhenBlockAbove", false) { towerMode != "None" }

    private val onJump by BooleanValue("TowerOnJump", true) { towerMode != "None" }
    private val matrix by BooleanValue("TowerMatrix", false) { towerMode != "None" }
    private val placeMode by ListValue(
        "TowerPlaceTiming",
        arrayOf("Pre", "Post"),
        "Post"
    ) { towerMode != "Packet" && towerMode != "None" }

    // Jump mode
    private val jumpMotion by FloatValue("JumpMotion", 0.42f, 0.3681289f..0.79f) { towerMode == "MotionJump" }
    private val jumpDelay by IntValue("JumpDelay", 0, 0..20) { towerMode == "MotionJump" || towerMode == "Jump" }

    // ConstantMotion
    private val constantMotion by FloatValue("ConstantMotion", 0.42f, 0.1f..1f) { towerMode == "ConstantMotion" }
    private val constantMotionJumpGround by FloatValue(
        "ConstantMotionJumpGround",
        0.79f,
        0.76f..1f
    ) { towerMode == "ConstantMotion" }
    private val constantMotionJumpPacket by BooleanValue("JumpPacket", true) { towerMode == "ConstantMotion" }

    // Teleport
    private val teleportHeight by FloatValue("TeleportHeight", 1.15f, 0.1f..5f) { towerMode == "Teleport" }
    private val teleportDelay by IntValue("TeleportDelay", 0, 0..20) { towerMode == "Teleport" }
    private val teleportGround by BooleanValue("TeleportGround", true) { towerMode == "Teleport" }
    private val teleportNoMotion by BooleanValue("TeleportNoMotion", false) { towerMode == "Teleport" }

    // <--

    /**
     * SCAFFOLD MODES & SETTINGS
     */

    // -->

    private val scaffoldMode by ListValue(
        "ScaffoldMode",
        arrayOf("Normal", "Rewinside", "Expand", "Telly", "GodBridge"),
        "Normal"
    )

    // Expand
    private val omniDirectionalExpand by BooleanValue("OmniDirectionalExpand", false) { scaffoldMode == "Expand" }
    private val expandLength by IntValue("ExpandLength", 1, 1..6) { scaffoldMode == "Expand" }

    // Placeable delay
    private val placeDelayValue = BooleanValue("PlaceDelay", true) { scaffoldMode != "GodBridge" }
    private val maxDelayValue: IntValue = object : IntValue("MaxDelay", 0, 0..1000) {
        override fun onChange(oldValue: Int, newValue: Int) = newValue.coerceAtLeast(minDelay)
        override fun isSupported() = placeDelayValue.isActive()
    }
    private val maxDelay by maxDelayValue

    private val minDelayValue = object : IntValue("MinDelay", 0, 0..1000) {
        override fun onChange(oldValue: Int, newValue: Int) = newValue.coerceAtMost(maxDelay)
        override fun isSupported() = placeDelayValue.isActive() && !maxDelayValue.isMinimal
    }
    private val minDelay by minDelayValue

    // Extra clicks
    private val extraClicks by BooleanValue("DoExtraClicks", false)
    private val simulateDoubleClicking by BooleanValue("SimulateDoubleClicking", false) { extraClicks }
    private val extraClickMaxCPSValue: IntValue = object : IntValue("ExtraClickMaxCPS", 7, 0..50) {
        override fun onChange(oldValue: Int, newValue: Int) = newValue.coerceAtLeast(extraClickMinCPS)
        override fun isSupported() = extraClicks
    }
    private val extraClickMaxCPS by extraClickMaxCPSValue

    private val extraClickMinCPS by object : IntValue("ExtraClickMinCPS", 3, 0..50) {
        override fun onChange(oldValue: Int, newValue: Int) = newValue.coerceAtMost(extraClickMaxCPS)
        override fun isSupported() = extraClicks && !extraClickMaxCPSValue.isMinimal
    }

    private val placementAttempt by ListValue(
        "PlacementAttempt",
        arrayOf("Fail", "Independent"),
        "Fail"
    ) { extraClicks }

    // Autoblock
    private val autoBlock by ListValue("AutoBlock", arrayOf("Off", "Pick", "Spoof", "Switch"), "Spoof")
    private val sortByHighestAmount by BooleanValue("SortByHighestAmount", false) { autoBlock != "Off" }

    // Settings
    private val autoF5 by BooleanValue("AutoF5", false)

    // Basic stuff
    val sprint by BooleanValue("Sprint", false)
    private val swing by SwingValue()
    private val down by BooleanValue("Down", true) { scaffoldMode !in arrayOf("GodBridge", "Telly") }

    private val ticksUntilRotation: IntValue = object : IntValue("TicksUntilRotation", 3, 1..5) {
        override fun isSupported() = scaffoldMode == "Telly"
        override fun onChange(oldValue: Int, newValue: Int) = newValue.coerceIn(minimum, maximum)
    }

    // GodBridge mode subvalues
    private val useStaticRotation by BooleanValue("GodBridge-UseStaticRotation", false) { scaffoldMode == "GodBridge" }
    private val godBridgeAutoJump by BooleanValue("GodBridge-AutoJump", true) { scaffoldMode == "GodBridge" }
    private val jumpAutomatically by BooleanValue("GodBridge-JumpAutomatically", true) { scaffoldMode == "GodBridge" && godBridgeAutoJump }
    private val maxBlocksToJump: IntValue = object : IntValue("GodBridge-MaxBlocksToJump", 4, 1..8) {
        override fun isSupported() = scaffoldMode == "GodBridge" && !jumpAutomatically && godBridgeAutoJump
        override fun onChange(oldValue: Int, newValue: Int) = newValue.coerceAtLeast(minBlocksToJump.get())
    }

    private val minBlocksToJump: IntValue = object : IntValue("GodBridge-MinBlocksToJump", 4, 1..8) {
        override fun isSupported() =
            scaffoldMode == "GodBridge" && !jumpAutomatically && !maxBlocksToJump.isMinimal && godBridgeAutoJump

        override fun onChange(oldValue: Int, newValue: Int) = newValue.coerceAtMost(maxBlocksToJump.get())
    }

    // Telly mode subvalues
    private val startHorizontally by BooleanValue("Telly-StartHorizontally", true) { scaffoldMode == "Telly" }
    private val maxHorizontalPlacements: IntValue = object : IntValue("Telly-MaxHorizontalPlacements", 1, 1..10) {
        override fun isSupported() = scaffoldMode == "Telly"
        override fun onChange(oldValue: Int, newValue: Int) = newValue.coerceAtLeast(minHorizontalPlacements.get())
    }
    private val minHorizontalPlacements: IntValue = object : IntValue("Telly-MinHorizontalPlacements", 1, 1..10) {
        override fun isSupported() = scaffoldMode == "Telly"
        override fun onChange(oldValue: Int, newValue: Int) = newValue.coerceAtMost(maxHorizontalPlacements.get())
    }
    private val maxVerticalPlacements: IntValue = object : IntValue("Telly-MaxVerticalPlacements", 1, 1..10) {
        override fun isSupported() = scaffoldMode == "Telly"
        override fun onChange(oldValue: Int, newValue: Int) = newValue.coerceAtLeast(minVerticalPlacements.get())
    }

    private val minVerticalPlacements: IntValue = object : IntValue("Telly-MinVerticalPlacements", 1, 1..10) {
        override fun isSupported() = scaffoldMode == "Telly"
        override fun onChange(oldValue: Int, newValue: Int) = newValue.coerceAtMost(maxVerticalPlacements.get())
    }

    private val maxJumpTicks: IntValue = object : IntValue("Telly-MaxJumpTicks", 0, 0..10) {
        override fun isSupported() = scaffoldMode == "Telly"
        override fun onChange(oldValue: Int, newValue: Int) = newValue.coerceAtLeast(minJumpTicks.get())
    }
    private val minJumpTicks: IntValue = object : IntValue("Telly-MinJumpTicks", 0, 0..10) {
        override fun isSupported() = scaffoldMode == "Telly"
        override fun onChange(oldValue: Int, newValue: Int) = newValue.coerceAtMost(maxJumpTicks.get())
    }

    private val allowClutching by BooleanValue("AllowClutching", true) { scaffoldMode !in arrayOf("Telly", "Expand") }
    private val horizontalClutchBlocks: IntValue = object : IntValue("HorizontalClutchBlocks", 3, 1..5) {
        override fun isSupported() = allowClutching && scaffoldMode !in arrayOf("Telly", "Expand")
        override fun onChange(oldValue: Int, newValue: Int) = newValue.coerceIn(minimum, maximum)
    }
    private val verticalClutchBlocks: IntValue = object : IntValue("VerticalClutchBlocks", 2, 1..3) {
        override fun isSupported() = allowClutching && scaffoldMode !in arrayOf("Telly", "Expand")
        override fun onChange(oldValue: Int, newValue: Int) = newValue.coerceIn(minimum, maximum)
    }

    // Eagle
    private val eagleValue = ListValue("Eagle", arrayOf("Normal", "Silent", "Off"), "Normal") { scaffoldMode != "GodBridge" }
    val eagle by eagleValue
    val eagleInAir by BooleanValue("EagleInAir", true) { eagleValue.isSupported() && eagle != "Off" }
    private val adjustedSneakSpeed by BooleanValue("AdjustedSneakSpeed", true) { eagle == "Silent" }
    private val eagleSpeed by FloatValue("EagleSpeed", 0.3f, 0.3f..1.0f) { eagleValue.isSupported() && eagle != "Off" }
    val eagleSprint by BooleanValue("EagleSprint", false) { eagleValue.isSupported() && eagle == "Normal" }
    private val blocksToEagle by IntValue("BlocksToEagle", 0, 0..10) { eagleValue.isSupported() && eagle != "Off" }
    private val edgeDistance by FloatValue("EagleEdgeDistance", 0f, 0f..0.5f) { eagleValue.isSupported() && eagle != "Off" }

    // Rotation Options
    private val rotationMode by ListValue("Rotations", arrayOf("Off", "Normal", "Stabilized", "GodBridge"), "Normal")
    private val smootherMode by ListValue(
        "SmootherMode",
        arrayOf("Linear", "Relative"),
        "Relative"
    ) { rotationMode != "Off" }
    private val silentRotation by BooleanValue("SilentRotation", true) { rotationMode != "Off" }
    private val strafe by BooleanValue("Strafe", false) { rotationMode != "Off" && silentRotation }
    private val keepRotation by BooleanValue("KeepRotation", true) { rotationMode != "Off" && silentRotation }
    private val keepTicks by object : IntValue("KeepTicks", 1, 1..20) {
        override fun onChange(oldValue: Int, newValue: Int) = newValue.coerceAtLeast(minimum)
        override fun isSupported() = rotationMode != "Off" && scaffoldMode != "Telly" && silentRotation
    }

    // Search options
    private val searchMode by ListValue("SearchMode", arrayOf("Area", "Center"), "Area") { scaffoldMode != "GodBridge" }
    private val minDist by FloatValue("MinDist", 0f, 0f..0.2f) { scaffoldMode !in arrayOf("GodBridge", "Telly") }

    // Turn Speed
    private val maxTurnSpeedValue: FloatValue = object : FloatValue("MaxTurnSpeed", 180f, 1f..180f) {
        override fun onChange(oldValue: Float, newValue: Float) = newValue.coerceAtLeast(minTurnSpeed)
        override fun isSupported() = rotationMode != "Off"
    }
    private val maxTurnSpeed by maxTurnSpeedValue
    private val minTurnSpeed by object : FloatValue("MinTurnSpeed", 180f, 1f..180f) {
        override fun onChange(oldValue: Float, newValue: Float) = newValue.coerceIn(minimum, maxTurnSpeed)
        override fun isSupported() = !maxTurnSpeedValue.isMinimal && rotationMode != "Off"
    }

    private val angleThresholdUntilReset by FloatValue(
        "AngleThresholdUntilReset",
        5f,
        0.1f..180f
    ) { rotationMode != "Off" && silentRotation }

    // Zitter
    private val zitterMode by ListValue("Zitter", arrayOf("Off", "Teleport", "Smooth"), "Off")
    private val zitterSpeed by FloatValue("ZitterSpeed", 0.13f, 0.1f..0.3f) { zitterMode == "Teleport" }
    private val zitterStrength by FloatValue("ZitterStrength", 0.05f, 0f..0.2f) { zitterMode == "Teleport" }

    private val maxZitterTicksValue: IntValue = object : IntValue("MaxZitterTicks", 3, 0..6) {
        override fun isSupported() = zitterMode == "Smooth"
        override fun onChange(oldValue: Int, newValue: Int) = newValue.coerceAtLeast(minZitterTicks)
    }
    private val maxZitterTicks by maxZitterTicksValue

    private val minZitterTicksValue: IntValue = object : IntValue("MinZitterTicks", 2, 0..6) {
        override fun isSupported() = zitterMode == "Smooth" && !maxZitterTicksValue.isMinimal
        override fun onChange(oldValue: Int, newValue: Int) = newValue.coerceAtMost(maxZitterTicks)
    }
    private val minZitterTicks by minZitterTicksValue

    private val useSneakMidAir by BooleanValue("UseSneakMidAir", false) { zitterMode == "Smooth" }

    // Game
    private val timer by FloatValue("Timer", 1f, 0.1f..10f)
    private val speedModifier by FloatValue("SpeedModifier", 1f, 0f..2f)
    private val speedLimiter by BooleanValue("SpeedLimiter", false) { !slow }
    private val speedLimit by FloatValue("SpeedLimit", 0.11f, 0.01f..0.12f) { !slow && speedLimiter }
    private val slow by BooleanValue("Slow", false)
    private val slowGround by BooleanValue("SlowOnlyGround", false) { slow }
    private val slowSpeed by FloatValue("SlowSpeed", 0.6f, 0.2f..0.8f) { slow }

    // Safety
    private val keepY by ListValue("KeepY", arrayOf("Always", "Never", "Smart"), "Smart") { scaffoldMode != "GodBridge" }
    private val autoJump by BooleanValue("AutoJump", false) { scaffoldMode != "GodBridge" }
    private val autoJumpMotion by DoubleValue("AutoJump-Motion", JUMP_HEIGHT, 0.0..JUMP_HEIGHT) { scaffoldMode != "GodBridge" && autoJump }
    private val autoJumpIngoreJumpBoost by BooleanValue("AutoJump-IngoreJumpBoost", false) { scaffoldMode != "GodBridge" && autoJump }
    private val autoJumpNoBoost by BooleanValue("AutoJump-NoBoost", false) { scaffoldMode != "GodBridge" && autoJump }
    private val autoJumpNoBoostForce by BooleanValue("AutoJump-NoBoost-Force", true) { scaffoldMode != "GodBridge" && autoJump && autoJumpNoBoost }
    private val safeWalkValue = BooleanValue("SafeWalk", true) { scaffoldMode != "GodBridge" }
    private val airSafe by BooleanValue("AirSafe", false) { safeWalkValue.isActive() }

    // Visuals
    private val counterDisplay by BooleanValue("Counter", true, subjective = true)
    private val mark by BooleanValue("Mark", false, subjective = true)
    private val trackCPS by BooleanValue("TrackCPS", false, subjective = true)
    private val safetyLines by BooleanValue("SafetyLines", false, subjective = true) { isGodBridgeEnabled }
    private val itemSwapAnimation by BooleanValue("ItemSwapAnimation", false, subjective = true)

    // Target placement
    private var placeRotation: PlaceRotation? = null

    // Launch position
    private var launchY = 0
    private val shouldKeepLaunchPosition
        get() = doKeepY && scaffoldMode != "GodBridge"

    // Zitter
    private var zitterDirection = false

    // Delay
    private val delayTimer = object : DelayTimer(minDelayValue, maxDelayValue, MSTimer()) {
        override fun hasTimePassed() = !placeDelayValue.isActive() || super.hasTimePassed()
    }

    private val zitterTickTimer = TickDelayTimer(minZitterTicksValue, maxZitterTicksValue)

    // Eagle
    private var placedBlocksWithoutEagle = 0
    var eagleSneaking = false
    private val isEagleEnabled
        get() = eagle != "Off" && !shouldGoDown && scaffoldMode != "GodBridge"

    // Downwards
    private val shouldGoDown
        get() = down && !doKeepY && mc.gameSettings.keyBindSneak.isActuallyPressed && scaffoldMode !in arrayOf(
            "GodBridge",
            "Telly"
        ) && blocksAmount > 1

    // Current rotation
    private val currRotation
        get() = currentRotation ?: mc.thePlayer.rotation

    // Extra clicks
    private var extraClick =
        ExtraClickInfo(randomClickDelay(extraClickMinCPS, extraClickMaxCPS), 0L, 0)

    // GodBridge
    private var blocksPlacedUntilJump = 0

    private val isManualJumpOptionActive
        get() = scaffoldMode == "GodBridge" && !jumpAutomatically

    private var blocksToJump = randomDelay(minBlocksToJump.get(), maxBlocksToJump.get())

    private val isGodBridgeEnabled
        get() = scaffoldMode == "GodBridge" || scaffoldMode == "Normal" && rotationMode == "GodBridge"

    private val isLookingDiagonally: Boolean
        get() {
            val player = mc.thePlayer ?: return false

            // Round the rotation to the nearest multiple of 45 degrees so that way we check if the player faces diagonally
            val yaw = round(abs(wrapAngleTo180_float(player.rotationYaw)).roundToInt() / 45f) * 45f

            return floatArrayOf(
                45f,
                135f
            ).any { yaw == it } && player.movementInput.moveForward != 0f && player.movementInput.moveStrafe == 0f
        }

    // Telly
    private var offGroundTicks = 0
    private var ticksUntilJump = 0
    private var blocksUntilAxisChange = 0
    private var jumpTicks = randomDelay(minJumpTicks.get(), maxJumpTicks.get())
    private var horizontalPlacements =
        randomDelay(minHorizontalPlacements.get(), maxHorizontalPlacements.get())
    private var verticalPlacements = randomDelay(minVerticalPlacements.get(), maxVerticalPlacements.get())
    private val shouldPlaceHorizontally
        get() = scaffoldMode == "Telly" && isMoving && (startHorizontally && blocksUntilAxisChange <= horizontalPlacements || !startHorizontally && blocksUntilAxisChange > verticalPlacements)

    // <--

    // Enabling module
    override fun onEnable() {
        val player = mc.thePlayer ?: return

        launchY = player.posY.roundToInt()
        blocksUntilAxisChange = 0
    }

    /**
     * TOWER SETTINGS
     */

    // Target block
    private var placeInfo: PlaceInfo? = null

    // Rotation lock
    private var lockRotation: Rotation? = null

    // Mode stuff
    private val tickTimer = TickTimer()
    private var jumpGround = 0.0

    // Events
    @EventTarget
    private fun onUpdate(event: UpdateEvent) {
        val player = mc.thePlayer ?: return

        if (mc.playerController.currentGameType == SPECTATOR)
            return

        if (isMoving && autoJump) {
            mc.thePlayer.jmp(autoJumpMotion, !autoJumpNoBoost, autoJumpIngoreJumpBoost)
        }

        mc.timer.timerSpeed = timer

        // Telly
        if (mc.thePlayer.onGround) {
            offGroundTicks = 0
            ticksUntilJump++
        } else {
            offGroundTicks++
        }

        if (shouldGoDown) {
            mc.gameSettings.keyBindSneak.pressed = false
        }

        if (slow) {
            if (!slowGround || slowGround && mc.thePlayer.onGround) {
                player.motionX *= slowSpeed
                player.motionZ *= slowSpeed
            }
        }

        // Eagle
        if (isEagleEnabled) {
            var dif = 0.5
            val blockPos = BlockPos(player).down()

            for (side in EnumFacing.entries) {
                if (side.axis == Y) {
                    continue
                }

                val neighbor = blockPos.offset(side)

                if (isReplaceable(neighbor)) {
                    val calcDif = (if (side.axis == Z) {
                        abs(neighbor.z + 0.5 - player.posZ)
                    } else {
                        abs(neighbor.x + 0.5 - player.posX)
                    }) - 0.5

                    if (calcDif < dif) {
                        dif = calcDif
                    }
                }
            }

            if (placedBlocksWithoutEagle >= blocksToEagle) {
                val shouldEagle = (isReplaceable(blockPos) || dif < edgeDistance) && (mc.thePlayer.onGround || eagleInAir)
                if (eagle == "Silent") {
                    if (eagleSneaking != shouldEagle) {
                        sendPacket(C0BPacketEntityAction(player, if (shouldEagle) START_SNEAKING else STOP_SNEAKING))

                        // Adjust speed when silent sneaking
                        if (adjustedSneakSpeed && shouldEagle) {
                            player.motionX *= eagleSpeed
                            player.motionZ *= eagleSpeed
                        }
                    }

                    eagleSneaking = shouldEagle
                } else {
                    mc.gameSettings.keyBindSneak.pressed = shouldEagle
                    eagleSneaking = shouldEagle
                }
                placedBlocksWithoutEagle = 0
            } else {
                placedBlocksWithoutEagle++
            }
        }

        if (player.onGround) {
            // Still a thing?
            if (scaffoldMode == "Rewinside") {
                strafe(0.2F)
                player.motionY = 0.0
            }
        }
    }

    @EventTarget
    fun onStrafe(event: StrafeEvent) {
        val player = mc.thePlayer

        // Jumping needs to be done here, so it doesn't get detected by movement-sensitive anti-cheats.
        if (scaffoldMode == "Telly" && player.onGround && isMoving && currRotation == player.rotation && ticksUntilJump >= jumpTicks) {
            player.jmp()

            ticksUntilJump = 0
            jumpTicks = randomDelay(minJumpTicks.get(), maxJumpTicks.get())
        }
    }

    @EventTarget
    fun onMotion(event: MotionEvent) {
        val rotation = currentRotation

        if (event.eventState == POST) {
            update()

            if (rotationMode != "Off" && rotation != null) {
                // Keep aiming at the target spot even if we have already placed
                // Prevents rotation correcting itself after a bit of bridging
                // Instead of doing it in the first place.
                // Normally a rotation utils recode is needed to rotate regardless of placeRotation being null or not, but whatever.
                val placeRotation = placeRotation?.rotation ?: rotation

                val pitch = if (scaffoldMode == "GodBridge" && useStaticRotation) {
                    if (placeRotation == this.placeRotation?.rotation) {
                        if (isLookingDiagonally) 75.6f else 73.5f
                    } else placeRotation.pitch
                } else {
                    placeRotation.pitch
                }

                val targetRotation = Rotation(placeRotation.yaw, pitch).fixedSensitivity()

                val limitedRotation = RotationUtils.limitAngleChange(
                    rotation,
                    targetRotation,
                    nextFloat(minTurnSpeed, maxTurnSpeed),
                    smootherMode
                )

                val ticks = if (keepRotation) {
                    if (scaffoldMode == "Telly") 1 else keepTicks
                } else {
                    keepLength
                }

                if (keepLength != 0 || keepRotation) {
                    setRotation(limitedRotation, ticks)
                }
            }
        }

        /**
         * TOWER FUNCTION
         */
        if (towerMode == "None") return
        if (onJump && !mc.gameSettings.keyBindJump.isKeyDown) return

        // Lock Rotation
        if (keepRotation && lockRotation != null) setTargetRotation(lockRotation!!)

        mc.timer.timerSpeed = timer
        val eventState = event.eventState

        // Force use of POST event when Packet mode is selected, it doesn't work with PRE mode
        if (eventState.name == (if (towerMode == "Packet") "POST" else placeMode.uppercase()))
            placeInfo?.let { place(it) }

        if (eventState == PRE) {
            update()
            //placeInfo = null
            tickTimer.update()

            if (!stopWhenBlockAbove || BlockPos(mc.thePlayer).up(2).getBlock() == air) move()

            val blockPos = BlockPos(mc.thePlayer).down()
            if (blockPos.getBlock() == air) {
                if (search(blockPos)) {
                    val vecRotation = faceBlock(blockPos)
                    if (vecRotation != null) {
                        setTargetRotation(vecRotation.rotation)
                        placeInfo!!.vec3 = vecRotation.vec
                    }
                }
            }
        }
    }

    /**
     *
     * TOWER FUNCTION
     *
     * Move player
     */
    private fun move() {
        val thePlayer = mc.thePlayer ?: return

        if (blocksAmount <= 0)
            return

        when (towerMode.lowercase()) {
            "jump" -> if (thePlayer.onGround && tickTimer.hasTimePassed(jumpDelay)) {
                thePlayer.fakeJump()
                thePlayer.jmp()
            } else if (!thePlayer.onGround) {
                thePlayer.isAirBorne = false
                tickTimer.reset()
            }

            "motion" -> if (thePlayer.onGround) {
                thePlayer.fakeJump()
                thePlayer.motionY = 0.42
            } else if (thePlayer.motionY < 0.1) {
                thePlayer.motionY = -0.3
            }

            // Old Name (Jump)
            "motionjump" -> if (thePlayer.onGround && tickTimer.hasTimePassed(jumpDelay)) {
                thePlayer.fakeJump()
                thePlayer.motionY = jumpMotion.toDouble()
                tickTimer.reset()
            }

            "motiontp" -> if (thePlayer.onGround) {
                thePlayer.fakeJump()
                thePlayer.motionY = 0.42
            } else if (thePlayer.motionY < 0.23) {
                thePlayer.setPosition(thePlayer.posX, truncate(thePlayer.posY), thePlayer.posZ)
            }

            "packet" -> if (thePlayer.onGround && tickTimer.hasTimePassed(2)) {
                thePlayer.fakeJump()
                sendPackets(
                    C04PacketPlayerPosition(
                        thePlayer.posX,
                        thePlayer.posY + 0.42,
                        thePlayer.posZ,
                        false
                    ),
                    C04PacketPlayerPosition(
                        thePlayer.posX,
                        thePlayer.posY + 0.753,
                        thePlayer.posZ,
                        false
                    )
                )
                thePlayer.setPosition(thePlayer.posX, thePlayer.posY + 1.0, thePlayer.posZ)
                tickTimer.reset()
            }

            "teleport" -> {
                if (teleportNoMotion) {
                    thePlayer.motionY = 0.0
                }
                if ((thePlayer.onGround || !teleportGround) && tickTimer.hasTimePassed(teleportDelay)) {
                    thePlayer.fakeJump()
                    thePlayer.setPositionAndUpdate(
                        thePlayer.posX, thePlayer.posY + teleportHeight, thePlayer.posZ
                    )
                    tickTimer.reset()
                }
            }

            "constantmotion" -> {
                if (thePlayer.onGround) {
                    if (constantMotionJumpPacket) {
                        thePlayer.fakeJump()
                    }
                    jumpGround = thePlayer.posY
                    thePlayer.motionY = constantMotion.toDouble()
                }
                if (thePlayer.posY > jumpGround + constantMotionJumpGround) {
                    if (constantMotionJumpPacket) {
                        thePlayer.fakeJump()
                    }
                    thePlayer.setPosition(
                        thePlayer.posX, truncate(thePlayer.posY), thePlayer.posZ
                    ) // TODO: toInt() required?
                    thePlayer.motionY = constantMotion.toDouble()
                    jumpGround = thePlayer.posY
                }
            }

            "aac3.3.9" -> {
                if (thePlayer.onGround) {
                    thePlayer.fakeJump()
                    thePlayer.motionY = 0.4001
                }
                mc.timer.timerSpeed = 1f
                if (thePlayer.motionY < 0) {
                    thePlayer.motionY -= 0.00000945
                    mc.timer.timerSpeed = 1.6f
                }
            }

            "aac3.6.4" -> if (thePlayer.ticksExisted % 4 == 1) {
                thePlayer.motionY = 0.4195464
                thePlayer.setPosition(thePlayer.posX - 0.035, thePlayer.posY, thePlayer.posZ)
            } else if (thePlayer.ticksExisted % 4 == 0) {
                thePlayer.motionY = -0.5
                thePlayer.setPosition(thePlayer.posX + 0.035, thePlayer.posY, thePlayer.posZ)
            }
        }
    }

    /**
     *
     * TOWER FUNCTION
     *
     * Search for placeable block
     *
     * @param blockPosition pos
     * @return
     */
    private fun search(blockPosition: BlockPos): Boolean {
        val thePlayer = mc.thePlayer ?: return false
        if (!isReplaceable(blockPosition)) {
            return false
        }

        val eyesPos = thePlayer.eyes
        var placeRotation: PlaceRotation? = null
        for (facingType in EnumFacing.entries) {
            val neighbor = blockPosition.offset(facingType)
            if (!canBeClicked(neighbor)) {
                continue
            }
            val dirVec = Vec3(facingType.directionVec)

            for (x in 0.1..0.9) {
                for (y in 0.1..0.9) {
                    for (z in 0.1..0.9) {
                        val posVec = Vec3(blockPosition).addVector(
                            if (matrix) 0.5 else x, if (matrix) 0.5 else y, if (matrix) 0.5 else z
                        )

                        val distanceSqPosVec = eyesPos.squareDistanceTo(posVec)
                        val hitVec = posVec + (dirVec * 0.5)

                        if (eyesPos.distanceTo(hitVec) > 4.25
                            || distanceSqPosVec > eyesPos.squareDistanceTo(posVec + dirVec)
                            || mc.theWorld.rayTraceBlocks(eyesPos, hitVec, false, true, false) != null
                        ) continue

                        // face block
                        val rotation = toRotation(hitVec, false)

                        val rotationVector = getVectorForRotation(rotation)
                        val vector = eyesPos + (rotationVector * 4.25)

                        val obj = mc.theWorld.rayTraceBlocks(eyesPos, vector, false, false, true) ?: continue

                        if (obj.typeOfHit != BLOCK || obj.blockPos != neighbor)
                            continue

                        if (placeRotation == null || getRotationDifference(rotation) < getRotationDifference(
                                placeRotation.rotation
                            )
                        )
                            placeRotation = PlaceRotation(PlaceInfo(neighbor, facingType.opposite, hitVec), rotation)
                    }
                }
            }
        }

        placeRotation ?: return false

        //if (rotations) {
        val fixedSensitivityRotation = placeRotation.rotation.fixedSensitivity()
        setTargetRotation(fixedSensitivityRotation)
        lockRotation = fixedSensitivityRotation
        //}
        placeInfo = placeRotation.placeInfo
        return true
    }

    @EventTarget
    fun onTick(event: TickEvent) {
        val target = placeRotation?.placeInfo

        if (extraClicks) {
            val doubleClick = if (simulateDoubleClicking) nextInt(-1, 1) else 0

            repeat(extraClick.clicks + doubleClick) {
                extraClick.clicks--

                doPlaceAttempt()
            }
        }

        if (target == null) {
            if (placeDelayValue.isActive()) {
                delayTimer.reset()
            }
            return
        }

        val raycastProperly = !(scaffoldMode == "Expand" && expandLength > 1 || shouldGoDown) && rotationMode != "Off"

        performBlockRaytrace(currRotation, mc.playerController.blockReachDistance).let {
            if (rotationMode == "Off" || it != null && it.blockPos == target.blockPos && (!raycastProperly || it.sideHit == target.enumFacing)) {
                val result = if (raycastProperly && it != null) {
                    PlaceInfo(it.blockPos, it.sideHit, it.hitVec)
                } else {
                    target
                }

                place(result)
            }
        }
    }

    @EventTarget
    fun onSneakSlowDown(event: SneakSlowDownEvent) {
        if (!isEagleEnabled || eagle != "Normal") {
            return
        }

        event.forward *= eagleSpeed / 0.3f
        event.strafe *= eagleSpeed / 0.3f
    }

    fun update() {
        val player = mc.thePlayer ?: return
        val holdingItem = player.heldItem?.item is ItemBlock

        if (!holdingItem && (autoBlock == "Off" || findBlockInHotbar() == null)) {
            return
        }

        findBlock(scaffoldMode == "Expand" && expandLength > 1, searchMode == "Area")
    }

    private fun setRotation(rotation: Rotation, ticks: Int) {
        val player = mc.thePlayer ?: return

        if (silentRotation) {
            if (scaffoldMode == "Telly" && isMoving) {
                if (offGroundTicks < ticksUntilRotation.get() && ticksUntilJump >= jumpTicks) {
                    return
                }
            }

            setTargetRotation(
                rotation,
                ticks,
                strafe,
                resetSpeed = minTurnSpeed to maxTurnSpeed,
                angleThresholdForReset = angleThresholdUntilReset,
                smootherMode = smootherMode
            )

        } else {
            rotation.toPlayer(player)
        }
    }

    // Search for new target block
    private fun findBlock(expand: Boolean, area: Boolean) {
        val player = mc.thePlayer ?: return

        if (!shouldKeepLaunchPosition)
            launchY = player.posY.roundToInt()

        val blockPosition = if (shouldGoDown) {
            if (player.posY == player.posY.roundToInt() + 0.5) {
                BlockPos(player.posX, player.posY - 0.6, player.posZ)
            } else {
                BlockPos(player.posX, player.posY - 0.6, player.posZ).down()
            }
        } else if (shouldKeepLaunchPosition && launchY <= player.posY) {
            BlockPos(player.posX, launchY - 1.0, player.posZ)
        } else if (player.posY == player.posY.roundToInt() + 0.5) {
            BlockPos(player)
        } else {
            BlockPos(player).down()
        }

        if (!expand && (!isReplaceable(blockPosition) || search(
                blockPosition,
                !shouldGoDown,
                area,
                shouldPlaceHorizontally
            ))
        ) {
            /*if (mode != "GodBridge" || wrapAngleTo180_float(currRotation.yaw.toInt().toFloat()) in arrayOf(-135f, -45f, 45f, 135f)) {
                placeRotation = null
            }*/
            return
        }

        if (expand) {
            val yaw = player.rotationYaw.toRadiansD()
            val x = if (omniDirectionalExpand) -sin(yaw).roundToInt() else player.horizontalFacing.directionVec.x
            val z = if (omniDirectionalExpand) cos(yaw).roundToInt() else player.horizontalFacing.directionVec.z

            repeat(expandLength) {
                if (search(blockPosition.add(x * it, 0, z * it), false, area))
                    return
            }
            return
        }

        val (f, g) = if (scaffoldMode == "Telly") 5 to 3 else if (allowClutching) horizontalClutchBlocks.get() to verticalClutchBlocks.get() else 1 to 1

        (-f..f).flatMap { x ->
            (0 downTo -g).flatMap { y ->
                (-f..f).map { z ->
                    Vec3i(x, y, z)
                }
            }
        }.sortedBy {
            BlockUtils.getCenterDistance(blockPosition.add(it))
        }.forEach {
            if (canBeClicked(blockPosition.add(it)) || search(
                    blockPosition.add(it),
                    !shouldGoDown,
                    area,
                    shouldPlaceHorizontally
                )
            ) {
                return
            }
        }
    }

    private fun place(placeInfo: PlaceInfo) {
        val player = mc.thePlayer ?: return
        val world = mc.theWorld ?: return

        if (!delayTimer.hasTimePassed() || shouldKeepLaunchPosition && launchY - 1 != placeInfo.vec3.yCoord.toInt())
            return

        var stack = player.inventoryContainer.getSlot(serverSlot + 36).stack

        //TODO: blacklist more blocks than only bushes
        if (stack == null || stack.item !is ItemBlock || (stack.item as ItemBlock).block is BlockBush || stack.stackSize <= 0 || sortByHighestAmount) {
            val blockSlot = if (sortByHighestAmount) {
                findLargestBlockStackInHotbar() ?: return
            } else {
                findBlockInHotbar() ?: return
            }

            when (autoBlock.lowercase()) {
                "off" -> return

                "pick" -> {
                    player.inventory.currentItem = blockSlot - 36
                    mc.playerController.updateController()
                }

                "spoof", "switch" -> serverSlot = blockSlot - 36
            }
            stack = player.inventoryContainer.getSlot(blockSlot).stack
        }

        // Line 437-440
        if ((stack.item as? ItemBlock)?.canPlaceBlockOnSide(
                world,
                placeInfo.blockPos,
                placeInfo.enumFacing,
                player,
                stack
            ) == false
        ) {
            return
        }

        tryToPlaceBlock(stack, placeInfo.blockPos, placeInfo.enumFacing, placeInfo.vec3)

        if (autoBlock == "Switch")
            serverSlot = player.inventory.currentItem

        // Since we violate vanilla slot switch logic if we send the packets now, we arrange them for the next tick
        switchBlockNextTickIfPossible(stack)

        if (trackCPS) {
            registerClick(RIGHT)
        }
    }

    private fun doPlaceAttempt() {
        val player = mc.thePlayer ?: return
        val world = mc.theWorld ?: return

        val stack = player.inventoryContainer.getSlot(serverSlot + 36).stack ?: return

        if (stack.item !is ItemBlock || BLOCK_BLACKLIST.contains((stack.item as ItemBlock).block)) {
            return
        }

        val block = stack.item as ItemBlock

        val raytrace = performBlockRaytrace(currRotation, mc.playerController.blockReachDistance) ?: return

        val canPlaceOnUpperFace = block.canPlaceBlockOnSide(
            world, raytrace.blockPos, UP, player, stack
        )

        val shouldPlace = if (placementAttempt == "Fail") {
            !block.canPlaceBlockOnSide(world, raytrace.blockPos, raytrace.sideHit, player, stack)
        } else {
            if (shouldKeepLaunchPosition) {
                raytrace.blockPos.y == launchY - 1 && !canPlaceOnUpperFace
            } else if (shouldPlaceHorizontally) {
                !canPlaceOnUpperFace
            } else {
                raytrace.blockPos.y <= player.posY.toInt() - 1 && !(raytrace.blockPos.y == player.posY.toInt() - 1 && canPlaceOnUpperFace && raytrace.sideHit == UP)
            }
        }

        if (raytrace.typeOfHit != BLOCK || !shouldPlace) {
            return
        }

        tryToPlaceBlock(stack, raytrace.blockPos, raytrace.sideHit, raytrace.hitVec, attempt = true)

        // Since we violate vanilla slot switch logic if we send the packets now, we arrange them for the next tick
        switchBlockNextTickIfPossible(stack)

        if (trackCPS) {
            registerClick(RIGHT)
        }
    }

    // Disabling module
    override fun onDisable() {
        val player = mc.thePlayer ?: return

        if (!mc.gameSettings.keyBindSneak.isActuallyPressed) {
            mc.gameSettings.keyBindSneak.pressed = false
            if (eagleSneaking && player.isSneaking) {
                sendPacket(C0BPacketEntityAction(player, STOP_SNEAKING))
            }
        }

        if (!mc.gameSettings.keyBindRight.isActuallyPressed) {
            mc.gameSettings.keyBindRight.pressed = false
        }
        if (!mc.gameSettings.keyBindLeft.isActuallyPressed) {
            mc.gameSettings.keyBindLeft.pressed = false
        }

        if (autoF5) mc.gameSettings.thirdPersonView = 0

        lockRotation = null
        placeRotation = null
        mc.timer.timerSpeed = 1f

        TickScheduler += {
            serverSlot = player.inventory.currentItem
        }
    }

    // Entity movement event
    @EventTarget
    fun onMove(event: MoveEvent) {
        val player = mc.thePlayer ?: return

        if (!safeWalkValue.isActive() || shouldGoDown) {
            return
        }

        if (airSafe || player.onGround) {
            event.isSafeWalk = true
        }
    }

    @EventTarget
    fun onJump(event: JumpEvent) {
        if (autoJump && scaffoldMode != "GodBridge" && autoJumpNoBoost && autoJumpNoBoostForce) {
            event.sprintBoost = 0f
        }

        if (onJump) {
            if (scaffoldMode == "GodBridge" && (godBridgeAutoJump || jumpAutomatically) || doKeepY)
                return
            if (towerMode == "None" || towerMode == "Jump")
                return
            if (Speed.state || Fly.state)
                return

            event.cancelEvent()
        }
    }

    // Scaffold visuals
    @EventTarget
    fun onRender2D(event: Render2DEvent) {
        if (counterDisplay) {
            glPushMatrix()

            if (BlockOverlay.handleEvents() && BlockOverlay.info && BlockOverlay.currentBlock != null) glTranslatef(
                0f,
                15f,
                0f
            )

            val info = "Blocks: §7$blocksAmount"
            val (width, height) = ScaledResolution(mc)

            drawBorderedRect(
                width / 2 - 2,
                height / 2 + 5,
                width / 2 + font40.getStringWidth(info) + 2,
                height / 2 + 16,
                3,
                BLACK.rgb,
                BLACK.rgb
            )

            resetColor()

            font40.drawString(
                info, width / 2, height / 2 + 7, WHITE.rgb
            )
            glPopMatrix()
        }
    }

    // Visuals
    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        val player = mc.thePlayer ?: return

        val shouldBother =
            !(shouldGoDown || scaffoldMode == "Expand" && expandLength > 1) && extraClicks && (isMoving || speed > 0.03)

        if (shouldBother) {
            currRotation.let {
                performBlockRaytrace(it, mc.playerController.blockReachDistance)?.let { raytrace ->
                    val timePassed = currentTimeMillis() - extraClick.lastClick >= extraClick.delay

                    if (raytrace.typeOfHit == BLOCK && timePassed) {
                        extraClick = ExtraClickInfo(
                            randomClickDelay(extraClickMinCPS, extraClickMaxCPS),
                            currentTimeMillis(),
                            extraClick.clicks + 1
                        )
                    }
                }
            }
        }

        displaySafetyLinesIfEnabled()

        if (!mark) {
            return
        }

        repeat(if (scaffoldMode == "Expand") expandLength + 1 else 2) {
            val yaw = player.rotationYaw.toRadiansD()
            val x = if (omniDirectionalExpand) -sin(yaw).roundToInt() else player.horizontalFacing.directionVec.x
            val z = if (omniDirectionalExpand) cos(yaw).roundToInt() else player.horizontalFacing.directionVec.z
            val blockPos = BlockPos(
                player.posX + x * it,
                if (shouldKeepLaunchPosition && launchY <= player.posY) launchY - 1.0 else player.posY - (if (player.posY == player.posY + 0.5) 0.0 else 1.0) - if (shouldGoDown) 1.0 else 0.0,
                player.posZ + z * it
            )
            val placeInfo = PlaceInfo.get(blockPos)

            if (isReplaceable(blockPos) && placeInfo != null) {
                drawBlockBox(blockPos, Color(68, 117, 255, 100), false)
                return
            }
        }
    }

    /**
     * Search for placeable block
     *
     * @param blockPosition pos
     * @param raycast visible
     * @param area spot
     * @return
     */

    private fun search(
        blockPosition: BlockPos,
        raycast: Boolean,
        area: Boolean,
        horizontalOnly: Boolean = false,
    ): Boolean {
        val player = mc.thePlayer ?: return false

        if (!isReplaceable(blockPosition)) {
            if (autoF5) mc.gameSettings.thirdPersonView = 0
            return false
        } else {
            if (autoF5 && mc.gameSettings.thirdPersonView != 1) mc.gameSettings.thirdPersonView = 1
        }

        val maxReach = mc.playerController.blockReachDistance

        val eyes = player.eyes
        var placeRotation: PlaceRotation? = null

        var currPlaceRotation: PlaceRotation?

        var considerStableRotation: PlaceRotation? = null

        for (side in EnumFacing.entries.filter { !horizontalOnly || it.axis != Y }) {
            val neighbor = blockPosition.offset(side)

            if (!canBeClicked(neighbor)) {
                continue
            }

            if (isGodBridgeEnabled) {
                // Selection of these values only. Mostly used by Godbridgers.
                val list = floatArrayOf(-135f, -45f, 45f, 135f)

                // Selection of pitch values that should be OK in non-complex situations.
                val pitchList = 55.0..75.7 + if (isLookingDiagonally) 1.0 else 0.0

                for (yaw in list) {
                    for (pitch in pitchList step 0.1) {
                        val rotation = Rotation(yaw, pitch.toFloat())

                        val raytrace = performBlockRaytrace(rotation, maxReach) ?: continue

                        currPlaceRotation =
                            PlaceRotation(PlaceInfo(raytrace.blockPos, raytrace.sideHit, raytrace.hitVec), rotation)

                        if (raytrace.blockPos == neighbor && raytrace.sideHit == side.opposite) {
                            val isInStablePitchRange = if (isLookingDiagonally) {
                                pitch >= 75.6
                            } else {
                                pitch in 73.5..75.7
                            }

                            // The module should be looking to aim at (nearly) the upper face of the block. Provides stable bridging most of the time.
                            if (isInStablePitchRange) {
                                considerStableRotation = compareDifferences(currPlaceRotation, considerStableRotation)
                            }

                            placeRotation = compareDifferences(currPlaceRotation, placeRotation)
                        }
                    }
                }

                continue
            }

            if (!area) {
                currPlaceRotation =
                    findTargetPlace(blockPosition, neighbor, Vec3(0.5, 0.5, 0.5), side, eyes, maxReach, raycast)
                        ?: continue

                placeRotation = compareDifferences(currPlaceRotation, placeRotation)
            } else {
                for (x in 0.1..0.9) {
                    for (y in 0.1..0.9) {
                        for (z in 0.1..0.9) {
                            currPlaceRotation =
                                findTargetPlace(blockPosition, neighbor, Vec3(x, y, z), side, eyes, maxReach, raycast)
                                    ?: continue

                            placeRotation = compareDifferences(currPlaceRotation, placeRotation)
                        }
                    }
                }
            }
        }

        placeRotation = considerStableRotation ?: placeRotation

        placeRotation ?: return false

        if (useStaticRotation && scaffoldMode == "GodBridge") {
            placeRotation = PlaceRotation(
                placeRotation.placeInfo,
                Rotation(placeRotation.rotation.yaw, if (isLookingDiagonally) 75.6f else 73.5f)
            )
        }

        if (rotationMode != "Off") {
            var targetRotation = placeRotation.rotation

            val info = placeRotation.placeInfo

            if (scaffoldMode == "GodBridge") {
                val shouldJumpForcefully = isManualJumpOptionActive && blocksPlacedUntilJump >= blocksToJump

                performBlockRaytrace(currRotation, maxReach)?.let {
                    val isSneaking = player.movementInput.sneak

                    if ((!isSneaking || speed != 0f) && it.blockPos == info.blockPos && (it.sideHit != info.enumFacing || shouldJumpForcefully) && isMoving && currRotation.yaw.roundToInt() % 45f == 0f) {
                        if (!isSneaking && godBridgeAutoJump) {
                            if (player.onGround && !isLookingDiagonally) {
                                player.jmp()
                            }

                            if (shouldJumpForcefully) {
                                blocksPlacedUntilJump = 0
                                blocksToJump = randomDelay(
                                    minBlocksToJump.get(),
                                    maxBlocksToJump.get()
                                )
                            }
                        }

                        targetRotation = currRotation
                    }
                }
            }

            val limitedRotation = RotationUtils.limitAngleChange(
                currRotation,
                targetRotation,
                nextFloat(minTurnSpeed, maxTurnSpeed),
                smootherMode
            )

            setRotation(limitedRotation, if (scaffoldMode == "Telly") 1 else keepTicks)
        }
        this.placeRotation = placeRotation
        return true
    }

    /**
     * For expand scaffold, fixes vector values that should match according to direction vector
     */
    private fun modifyVec(original: Vec3, direction: EnumFacing, pos: Vec3, shouldModify: Boolean): Vec3 {
        if (!shouldModify) {
            return original
        }

        val x = original.xCoord
        val y = original.yCoord
        val z = original.zCoord

        val side = direction.opposite

        return when (side.axis ?: return original) {
            Y -> Vec3(x, pos.yCoord + side.directionVec.y.coerceAtLeast(0), z)
            EnumFacing.Axis.X -> Vec3(pos.xCoord + side.directionVec.x.coerceAtLeast(0), y, z)
            Z -> Vec3(x, y, pos.zCoord + side.directionVec.z.coerceAtLeast(0))
        }

    }

    private fun findTargetPlace(
        pos: BlockPos, offsetPos: BlockPos, vec3: Vec3, side: EnumFacing, eyes: Vec3, maxReach: Float, raycast: Boolean,
    ): PlaceRotation? {
        val world = mc.theWorld ?: return null

        val vec = (Vec3(pos) + vec3).addVector(
            side.directionVec.x * vec3.xCoord, side.directionVec.y * vec3.yCoord, side.directionVec.z * vec3.zCoord
        )

        val distance = eyes.distanceTo(vec)

        if (raycast && (distance > maxReach || world.rayTraceBlocks(eyes, vec, false, true, false) != null)) {
            return null
        }

        val diff = vec - eyes

        if (side.axis != Y) {
            val dist = abs(if (side.axis == Z) diff.zCoord else diff.xCoord)

            if (dist < minDist && scaffoldMode != "Telly") {
                return null
            }
        }

        var rotation = toRotation(vec, false)

        rotation = when (rotationMode) {
            "Stabilized" -> Rotation(round(rotation.yaw / 45f) * 45f, rotation.pitch)
            else -> rotation
        }.fixedSensitivity()

        // If the current rotation already looks at the target block and side, then return right here
        performBlockRaytrace(currRotation, maxReach)?.let { raytrace ->
            if (raytrace.blockPos == offsetPos && (!raycast || raytrace.sideHit == side.opposite)) {
                return PlaceRotation(
                    PlaceInfo(
                        raytrace.blockPos, side.opposite, modifyVec(raytrace.hitVec, side, Vec3(offsetPos), !raycast)
                    ), currRotation
                )
            }
        }

        val raytrace = performBlockRaytrace(rotation, maxReach) ?: return null

        if (raytrace.blockPos == offsetPos && (!raycast || raytrace.sideHit == side.opposite)) {
            return PlaceRotation(
                PlaceInfo(
                    raytrace.blockPos, side.opposite, modifyVec(raytrace.hitVec, side, Vec3(offsetPos), !raycast)
                ), rotation
            )
        }

        return null
    }

    private fun performBlockRaytrace(rotation: Rotation, maxReach: Float): MovingObjectPosition? {
        val player = mc.thePlayer ?: return null
        val world = mc.theWorld ?: return null

        val eyes = player.eyes
        val rotationVec = getVectorForRotation(rotation)

        val reach = eyes + (rotationVec * maxReach.toDouble())

        return world.rayTraceBlocks(eyes, reach, false, false, true)
    }

    private fun compareDifferences(
        new: PlaceRotation, old: PlaceRotation?, rotation: Rotation = currRotation,
    ): PlaceRotation {
        if (old == null || getRotationDifference(
                new.rotation,
                rotation
            ) < getRotationDifference(
                old.rotation, rotation
            )
        ) {
            return new
        }

        return old
    }

    private fun switchBlockNextTickIfPossible(stack: ItemStack) {
        val player = mc.thePlayer ?: return

        if (autoBlock !in arrayOf("Off", "Switch") && stack.stackSize <= 0) {
            findBlockInHotbar()?.let {
                TickScheduler += {
                    if (autoBlock == "Pick") {
                        player.inventory.currentItem = it - 36
                        mc.playerController.updateController()
                    } else {
                        serverSlot = it - 36
                    }
                }
            }
        }
    }

    private fun displaySafetyLinesIfEnabled() {
        if (!safetyLines || !isGodBridgeEnabled) {
            return
        }

        val player = mc.thePlayer ?: return

        // If player is not walking diagonally then continue
        if (round(abs(wrapAngleTo180_float(player.rotationYaw)).roundToInt() / 45f) * 45f !in arrayOf(
                135f,
                45f
            ) || player.movementInput.moveForward == 0f || player.movementInput.moveStrafe != 0f
        ) {
            val (posX, posY, posZ) = player.interpolatedPosition

            glPushMatrix()
            glTranslated(-posX, -posY, -posZ)
            glLineWidth(5.5f)
            glDisable(GL_TEXTURE_2D)

            val (yawX, yawZ) = player.horizontalFacing.directionVec.x * 1.5 to player.horizontalFacing.directionVec.z * 1.5

            // The target rotation will either be the module's placeRotation or a forced rotation (usually that's where the GodBridge mode aims)
            val targetRotation = run {
                val yaw = floatArrayOf(-135f, -45f, 45f, 135f).minByOrNull {
                    abs(
                        RotationUtils.getAngleDifference(
                            it,
                            wrapAngleTo180_float(currRotation.yaw)
                        )
                    )
                } ?: return

                placeRotation?.rotation ?: Rotation(yaw, 73f)
            }

            // Calculate color based on rotation difference
            val color = getColorForRotationDifference(
                getRotationDifference(
                    targetRotation,
                    currRotation
                )
            )

            val main = BlockPos(player).down()

            val pos = if (canBeClicked(main)) {
                main
            } else {
                (-1..1).flatMap { x ->
                    (-1..1).map { z ->
                        val neighbor = main.add(x, 0, z)

                        neighbor to BlockUtils.getCenterDistance(neighbor)
                    }
                }.filter { canBeClicked(it.first) }.minByOrNull { it.second }?.first ?: main
            }.up().getVec()

            for (offset in 0..1) {
                for (i in -1..1 step 2) {
                    for (x1 in 0.25..0.5 step 0.01) {
                        val opposite = offset == 1

                        val (offsetX, offsetZ) = if (opposite) 0.0 to x1 * i else x1 * i to 0.0
                        val (lineX, lineZ) = if (opposite) yawX to 0.0 else 0.0 to yawZ

                        val (x, y, z) = pos.add(Vec3(offsetX, -0.99, offsetZ))

                        glBegin(GL_LINES)

                        glColor3f(color.x, color.y, color.z)
                        glVertex3d(x - lineX, y + 0.5, z - lineZ)
                        glVertex3d(x + lineX, y + 0.5, z + lineZ)

                        glEnd()
                    }
                }
            }
            glEnable(GL_TEXTURE_2D)
            glPopMatrix()
        }
    }

    private fun getColorForRotationDifference(rotationDifference: Float): Color3f {
        val maxDifferenceForGreen = 10.0f
        val maxDifferenceForYellow = 40.0f

        val interpolationFactor = when {
            rotationDifference <= maxDifferenceForGreen -> 0.0f
            rotationDifference <= maxDifferenceForYellow -> (rotationDifference - maxDifferenceForGreen) / (maxDifferenceForYellow - maxDifferenceForGreen)
            else -> 1.0f
        }

        val green = 1.0f - interpolationFactor
        val blue = 0.0f

        return Color3f(interpolationFactor, green, blue)
    }

    private fun updatePlacedBlocksForTelly() {
        if (blocksUntilAxisChange > horizontalPlacements + verticalPlacements) {
            blocksUntilAxisChange = 0

            horizontalPlacements =
                randomDelay(minHorizontalPlacements.get(), maxHorizontalPlacements.get())
            verticalPlacements =
                randomDelay(minVerticalPlacements.get(), maxVerticalPlacements.get())
            return
        }

        blocksUntilAxisChange++
    }

    private fun tryToPlaceBlock(
        stack: ItemStack,
        clickPos: BlockPos,
        side: EnumFacing,
        hitVec: Vec3,
        attempt: Boolean = false,
    ): Boolean {
        val thePlayer = mc.thePlayer ?: return false

        val prevSize = stack.stackSize

        val clickedSuccessfully = thePlayer.onPlayerRightClick(clickPos, side, hitVec, stack)

        if (clickedSuccessfully) {
            if (!attempt) {
                delayTimer.reset()

                if (thePlayer.onGround) {
                    thePlayer.motionX *= speedModifier
                    thePlayer.motionZ *= speedModifier
                }
            }

            mc.thePlayer.swing(swing)

            if (isManualJumpOptionActive && godBridgeAutoJump)
                blocksPlacedUntilJump++

            updatePlacedBlocksForTelly()

            if (stack.stackSize <= 0) {
                thePlayer.inventory.mainInventory[serverSlot] = null
                ForgeEventFactory.onPlayerDestroyItem(thePlayer, stack)
            } else if (stack.stackSize != prevSize || mc.playerController.isInCreativeMode)
                if (itemSwapAnimation) mc.entityRenderer.itemRenderer.resetEquippedProgress()

        } else {
            if (thePlayer.sendUseItem(stack))
                if (itemSwapAnimation) mc.entityRenderer.itemRenderer.resetEquippedProgress2()
        }

        return clickedSuccessfully
    }

    fun handleMovementOptions(input: MovementInput) {
        if (!state) {
            return
        }

        if (!slow && speedLimiter && speed > speedLimit) {
            input.moveStrafe = 0f
            input.moveForward = 0f
            return
        }

        val player = mc.thePlayer ?: return

        when (zitterMode.lowercase()) {
            "off" -> {
                return
            }

            "smooth" -> {
                val notOnGround = !player.onGround || !player.isCollidedVertically

                if (player.onGround) {
                    mc.gameSettings.keyBindSneak.pressed =
                        eagleSneaking || mc.gameSettings.keyBindSneak.isActuallyPressed
                }

                if (input.jump || mc.gameSettings.keyBindJump.isKeyDown || notOnGround) {
                    zitterTickTimer.reset()

                    if (useSneakMidAir) {
                        mc.gameSettings.keyBindSneak.pressed = true
                    }

                    if (!notOnGround && !input.jump) {
                        // Attempt to move against the direction
                        input.moveStrafe = if (zitterDirection) 1f else -1f
                    } else {
                        input.moveStrafe = 0f
                    }

                    zitterDirection = !zitterDirection

                    // Recreate input in case the user was indeed pressing inputs
                    if (mc.gameSettings.keyBindLeft.isKeyDown) {
                        input.moveStrafe++
                    }

                    if (mc.gameSettings.keyBindRight.isKeyDown) {
                        input.moveStrafe--
                    }
                    return
                }

                if (zitterTickTimer.hasTimePassed()) {
                    zitterDirection = !zitterDirection
                    zitterTickTimer.reset()
                } else {
                    zitterTickTimer.update()
                }

                if (zitterDirection) {
                    input.moveStrafe = -1f
                } else {
                    input.moveStrafe = 1f
                }
            }

            "teleport" -> {
                strafe(zitterSpeed)
                val yaw = (player.rotationYaw + if (zitterDirection) 90.0 else -90.0).toRadians()
                player.motionX -= sin(yaw) * zitterStrength
                player.motionZ += cos(yaw) * zitterStrength
                zitterDirection = !zitterDirection
            }
        }
    }

    /**
     * Returns the amount of blocks
     */
    private val blocksAmount: Int
        get() {
            var amount = 0
            for (i in 36..44) {
                val stack = mc.thePlayer.inventoryContainer.getSlot(i).stack ?: continue
                val item = stack.item
                if (item is ItemBlock) {
                    val block = item.block
                    val heldItem = mc.thePlayer.heldItem
                    if (heldItem != null && heldItem == stack || block !in BLOCK_BLACKLIST && block !is BlockBush) {
                        amount += stack.stackSize
                    }
                }
            }
            return amount
        }

    private val doKeepY
        get() = when (keepY) {
            "Always" -> true
            "Smart" -> !mc.gameSettings.keyBindJump.isActuallyPressed
            else -> false
        }

    override val tag
        get() = if (towerMode != "None") ("$scaffoldMode | $towerMode") else scaffoldMode

    data class ExtraClickInfo(val delay: Int, val lastClick: Long, var clicks: Int)
}
