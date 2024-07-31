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
import net.ccbluex.liquidbounce.utils.MovementUtils.isMoving
import net.ccbluex.liquidbounce.utils.PacketUtils.sendPacket
import net.ccbluex.liquidbounce.utils.PacketUtils.sendPackets
import net.ccbluex.liquidbounce.utils.extensions.getDistanceToEntityBox
import net.ccbluex.liquidbounce.utils.extensions.stopXZ
import net.ccbluex.liquidbounce.utils.timing.MSTimer
import net.ccbluex.liquidbounce.utils.timing.TimeUtils.randomDelay
import net.ccbluex.liquidbounce.value.BooleanValue
import net.ccbluex.liquidbounce.value.IntValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.entity.EntityLivingBase
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C0BPacketEntityAction
import net.minecraft.network.play.client.C0BPacketEntityAction.Action.*
import kotlin.math.abs

object SuperKnockback : Module("SuperKnockback", COMBAT) {

    private val delay by IntValue("Delay", 0, 0..500)
    private val hurtTime by IntValue("HurtTime", 10, 0..10)

    private val mode by ListValue("Mode", arrayOf("SprintTap", "SprintTap2", "WTap", "Old", "Grim", "Silent", "Packet", "SneakPacket").sortedArray(), "Old")
    private val grim by BooleanValue("Grim", true) { mode in arrayOf("Old", "Packet", "SneakPacket") }
    private val maxTicksUntilBlock: IntValue = object : IntValue("MaxTicksUntilBlock", 2, 0..5) {
        override fun isSupported() = mode == "WTap"

        override fun onChange(oldValue: Int, newValue: Int) = newValue.coerceAtLeast(minTicksUntilBlock.get())
    }
    private val minTicksUntilBlock: IntValue = object : IntValue("MinTicksUntilBlock", 0, 0..5) {
        override fun isSupported() = mode == "WTap"

        override fun onChange(oldValue: Int, newValue: Int) = newValue.coerceAtMost(maxTicksUntilBlock.get())
    }

    private val reSprintMaxTicks: IntValue = object : IntValue("ReSprintMaxTicks", 2, 1..5) {
        override fun isSupported() = mode == "WTap"

        override fun onChange(oldValue: Int, newValue: Int) = newValue.coerceAtLeast(reSprintMinTicks.get())
    }
    private val reSprintMinTicks: IntValue = object : IntValue("ReSprintMinTicks", 1, 1..5) {
        override fun isSupported() = mode == "WTap"

        override fun onChange(oldValue: Int, newValue: Int) = newValue.coerceAtMost(reSprintMaxTicks.get())
    }

    private val targetDistance by IntValue("TargetDistance", 3, 1..5) { mode == "WTap" }

    private val stopTicks: IntValue = object : IntValue("PressBackTicks", 1, 1..5) {
        override fun isSupported() = mode == "SprintTap2"

        override fun onChange(oldValue: Int, newValue: Int) = newValue.coerceAtMost(unSprintTicks.get())
    }
    private val unSprintTicks: IntValue = object : IntValue("ReleaseBackTicks", 2, 1..5) {
        override fun isSupported() = mode == "SprintTap2"

        override fun onChange(oldValue: Int, newValue: Int) = newValue.coerceAtLeast(stopTicks.get())
    }

    private val onlyGround by BooleanValue("OnlyGround", false)
    @JvmStatic val onlyMove by BooleanValue("OnlyMove", true)
    @JvmStatic val onlyMoveForward by BooleanValue("OnlyMoveForward", true) { onlyMove }

    private var ticks = 0
    private var forceSprintState = 0
    private val timer = MSTimer()

    // WTap
    private var blockInputTicks = randomDelay(minTicksUntilBlock.get(), maxTicksUntilBlock.get())
    private var blockTicksElapsed = 0
    private var startWaiting = false
    private var blockInput = false
    private var allowInputTicks = randomDelay(reSprintMinTicks.get(), reSprintMaxTicks.get())
    private var ticksElapsed = 0

    // SprintTap2
    private var sprintTicks = 0

    override fun onToggle(state: Boolean) {
        // Make sure the user won't have their input forever blocked
        blockInput = false
        startWaiting = false
        blockTicksElapsed = 0
        ticksElapsed = 0
        sprintTicks = 0
    }

    @EventTarget
    fun onAttack(event: AttackEvent) {
        val player = mc.thePlayer ?: return

        val target = event.targetEntity as? EntityLivingBase ?: return
        val distance = player.getDistanceToEntityBox(target)

        if (event.targetEntity.hurtTime > hurtTime || !timer.hasTimePassed(delay) || (onlyGround && !player.onGround)) return

        if (onlyMove && (!isMoving || (onlyMoveForward && player.movementInput.moveStrafe != 0f))) return

        when (mode) {
            "Old" -> {
                // Users reported that this mode is better than the other ones

                if (player.isSprinting && !grim || grim && player.serverSprintState) {
                    sendPacket(C0BPacketEntityAction(player, STOP_SPRINTING))
                }

                sendPackets(
                    C0BPacketEntityAction(player, START_SPRINTING),
                    C0BPacketEntityAction(player, STOP_SPRINTING),
                    C0BPacketEntityAction(player, START_SPRINTING)
                )
                player.isSprinting = true
                player.serverSprintState = true
            }

            "SprintTap", "Silent" -> if (player.isSprinting && player.serverSprintState) ticks = 2

            "Packet" -> {
                if (grim && player.serverSprintState || !grim) {
                    sendPacket(C0BPacketEntityAction(player, STOP_SPRINTING))
                }

                sendPacket(C0BPacketEntityAction(player, START_SPRINTING))

                if (grim) {
                    player.isSprinting = true
                    player.serverSprintState = true
                }
            }

            "SneakPacket" -> {
                if (grim && player.serverSprintState || !grim) {
                    sendPacket(C0BPacketEntityAction(player, STOP_SPRINTING))
                }

                sendPackets(
                    C0BPacketEntityAction(player, START_SNEAKING),
                    C0BPacketEntityAction(player, START_SPRINTING),
                    C0BPacketEntityAction(player, STOP_SNEAKING)
                )

                if (grim) {
                    player.isSprinting = true
                    player.serverSprintState = true
                }
            }

            "WTap" -> {
                // We want the player to be sprinting before we block inputs
                if (player.isSprinting && player.serverSprintState && !blockInput && !startWaiting) {
                    val delayMultiplier = 1.0 / (abs(targetDistance - distance) + 1)

                    blockInputTicks = (randomDelay(minTicksUntilBlock.get(), maxTicksUntilBlock.get()) * delayMultiplier).toInt()

                    blockInput = blockInputTicks == 0

                    if (!blockInput) {
                        startWaiting = true
                    }

                    allowInputTicks = (randomDelay(reSprintMinTicks.get(), reSprintMaxTicks.get()) * delayMultiplier).toInt()
                }
            }

            "SprintTap2" -> {
                if (++sprintTicks == stopTicks.get()) {

                    if (mc.thePlayer.isSprinting && mc.thePlayer.serverSprintState) {
                        mc.thePlayer.isSprinting = false
                        mc.thePlayer.serverSprintState = false
                    } else {
                        mc.thePlayer.isSprinting = true
                        mc.thePlayer.serverSprintState = true
                    }

                    mc.thePlayer.stopXZ()

                } else if (sprintTicks >= unSprintTicks.get()) {

                    mc.thePlayer.isSprinting = false
                    mc.thePlayer.serverSprintState = false

                    sprintTicks = 0
                }
            }
        }

        timer.reset()
    }

    @EventTarget
    fun onPostSprintUpdate(event: PostSprintUpdateEvent) {
        val player = mc.thePlayer ?: return
        if (mode == "SprintTap") {
            when (ticks) {
                2 -> {
                    player.isSprinting = false
                    forceSprintState = 2
                    ticks--
                }

                1 -> {
                    if (player.movementInput.moveForward > 0.8) {
                        player.isSprinting = true
                    }
                    forceSprintState = 1
                    ticks--
                }

                else -> {
                    forceSprintState = 0
                }
            }
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (mode == "WTap") {
            if (blockInput) {
                if (ticksElapsed++ >= allowInputTicks) {
                    blockInput = false
                    ticksElapsed = 0
                }
            } else {
                if (startWaiting) {
                    blockInput = blockTicksElapsed++ >= blockInputTicks

                    if (blockInput) {
                        startWaiting = false
                        blockTicksElapsed = 0
                    }
                }
            }
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val player = mc.thePlayer ?: return
        val packet = event.packet
        if (packet is C03PacketPlayer && mode == "Silent") {
            if (ticks == 2) {
                sendPacket(C0BPacketEntityAction(player, STOP_SPRINTING))
                ticks--
            } else if (ticks == 1 && player.isSprinting) {
                sendPacket(C0BPacketEntityAction(player, START_SPRINTING))
                ticks--
            }
        }
    }

    @JvmStatic
    fun shouldBlockInput() = handleEvents() && mode == "WTap" && blockInput

    override val tag
        get() = mode


    fun breakSprint() = handleEvents() && forceSprintState == 2 && mode == "SprintTap"
    fun startSprint() = handleEvents() && forceSprintState == 1 && mode == "SprintTap"
}