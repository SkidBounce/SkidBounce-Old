/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement

import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura
import net.ccbluex.liquidbounce.features.module.modules.movement.noslowmodes.aac.*
import net.ccbluex.liquidbounce.features.module.modules.movement.noslowmodes.ncp.*
import net.ccbluex.liquidbounce.features.module.modules.movement.noslowmodes.other.*
import net.ccbluex.liquidbounce.features.module.modules.movement.noslowmodes.watchdog.*
import net.ccbluex.liquidbounce.utils.PacketUtils
import net.ccbluex.liquidbounce.utils.inventory.InventoryUtils.serverUsing
import net.ccbluex.liquidbounce.value.*
import net.minecraft.init.Blocks
import net.minecraft.item.*
import net.minecraft.network.play.client.*

object NoSlow : Module("NoSlow", ModuleCategory.MOVEMENT, gameDetecting = false) {

    private val swordModes = arrayOf(
        Vanilla,
        SwitchItem,
        OldIntave,
        NCP,
        NCP2,
        NewNCP,
        UNCP,
        UNCP2,
        AAC,
        AAC2,
        AAC5,
        WatchDog2,
    ).sortedBy { it.modeName }
    private val consumeModes = arrayOf(
        Vanilla,
        SwitchItem,
        OldIntave,
        UNCP,
        UNCP2,
        AAC5,
    ).sortedBy { it.modeName }
    private val bowModes = arrayOf(
        Vanilla,
        SwitchItem,
        OldIntave,
        UNCP,
        UNCP2,
        AAC5,
    ).sortedBy { it.modeName }

    var shouldSwap = false

    override fun onDisable() {
        shouldSwap = false
        Blocks.slime_block.slipperiness = 0.8f
    }

    private val blocking by BoolValue("Blocking", true)
    private val consuming by BoolValue("Consuming", true)
    private val bows by BoolValue("Bows", true)
    val sneaking by BoolValue("Sneaking", true)
    val soulsand by BoolValue("SoulSand", true)
    val liquidPush by BoolValue("LiquidPush", true)
    val slime by BoolValue("Slime", true)

    private val blockingMode by ListValue("BlockingMode", swordModes.map { it.modeName }.toTypedArray(), "Vanilla") { blocking }
    private val consumeMode by ListValue("ConsumeMode", consumeModes.map { it.modeName }.toTypedArray(), "Vanilla") { consuming }
    private val bowMode by ListValue("BowMode", bowModes.map { it.modeName }.toTypedArray(), "Vanilla") { bows }
    private val sneakMode by ListValue("SneakMode", arrayOf("Vanilla", "Switch", "MineSecure"), "Vanilla") { sneaking }

    private val onlyMoveBlocking by BoolValue("OnlyMoveBlocking", true) { blocking }
    private val onlyMoveConsume by BoolValue("OnlyMoveConsume", true) { consuming }
    private val onlyMoveBow by BoolValue("OnlyMoveBow", true) { bows }
    private val onlyMoveSneak by BoolValue("OnlyMoveSneak", true) { sneaking }

    private val blockForwardMultiplier by FloatValue("BlockForwardMultiplier", 1f, 0.2f..1f) { blocking }
    private val blockStrafeMultiplier by FloatValue("BlockStrafeMultiplier", 1f, 0.2f..1f) { blocking }
    private val consumeForwardMultiplier by FloatValue("ConsumeForwardMultiplier", 1f, 0.2f..1f) { consuming }
    private val consumeStrafeMultiplier by FloatValue("ConsumeStrafeMultiplier", 1f, 0.2f..1f) { consuming }
    private val bowForwardMultiplier by FloatValue("BowForwardMultiplier", 1f, 0.2f..1f) { bows }
    private val bowStrafeMultiplier by FloatValue("BowStrafeMultiplier", 1f, 0.2f..1f) { bows }
    val sneakForwardMultiplier by FloatValue("SneakForwardMultiplier", 0.3f, 0.3f..1.0F) { sneaking }
    val sneakStrafeMultiplier by FloatValue("SneakStrafeMultiplier", 0.3f, 0.3f..1f) { sneaking }
    val soulsandMultiplier by FloatValue("SoulSandMultiplier", 1f, 0.4f..1f) { soulsand }
    val slimeYMultiplier by FloatValue("SlimeYMultiplier", 1f, 0.2f..1f) { slime }
    val slimeMultiplier by FloatValue("SlimeMultiplier", 1f, 0.4f..1f) { slime }
    private val slimeFriction by FloatValue("SlimeFriction", 0.6f, 0.6f..0.8f) { slime }

    private val modeModuleBlocking
        get() = swordModes.find { it.modeName == blockingMode }!!
    private val modeModuleBow
        get() = bowModes.find { it.modeName == bowMode }!!
    private val modeModuleConsume
        get() = consumeModes.find { it.modeName == consumeMode }!!

    @EventTarget
    fun onMotion(event: MotionEvent) {
        if (serverUsing && !isUsingItem)
            serverUsing = false

        Blocks.slime_block.slipperiness = if (slime) slimeFriction else 0.8f

        if (mc.thePlayer.isSneaking && !(onlyMoveSneak && mc.thePlayer.motionX == 0.0 && mc.thePlayer.motionZ == 0.0)) {
            when (sneakMode) {
                "Vanilla" -> {}
                "Switch" -> when (event.eventState) {
                    EventState.PRE -> {
                        PacketUtils.sendPackets(
                            C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING),
                            C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING)
                        )
                    }
                    EventState.POST -> {
                        PacketUtils.sendPackets(
                            C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING),
                            C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING)
                        )
                    }
                    else -> {}
                }
                "MineSecure" -> {
                    if (event.eventState == EventState.PRE)
                        return

                    PacketUtils.sendPacket(
                        C0BPacketEntityAction(
                            mc.thePlayer,
                            C0BPacketEntityAction.Action.START_SNEAKING
                        )
                    )
                }
            }
        }

        if ( !shouldSwap && ( mc.thePlayer.motionX == 0.0 && mc.thePlayer.motionZ == 0.0 ) && (
                    ( onlyMoveConsume && isHoldingConsumable ) ||
                    ( onlyMoveBlocking && mc.thePlayer.heldItem?.item is ItemSword ) ||
                    ( onlyMoveBow && mc.thePlayer.heldItem?.item is ItemBow ) )
        ) return

        if (isUsingItem || shouldSwap) {
            when (mc.thePlayer.heldItem?.item) {
                is ItemSword -> if (blocking) modeModuleBlocking.onMotion(event) else return
                is ItemFood, is ItemPotion, is ItemBucketMilk -> if (consuming) modeModuleConsume.onMotion(event) else return
                is ItemBow -> if (bows) modeModuleBow.onMotion(event) else return
            }
        }
    }

    @EventTarget
    fun onUpdate() {
        if (serverUsing && !isUsingItem)
            serverUsing = false

        if ( ( mc.thePlayer.motionX == 0.0 && mc.thePlayer.motionZ == 0.0 ) && (
            ( onlyMoveConsume && isHoldingConsumable ) ||
            ( onlyMoveBlocking && mc.thePlayer.heldItem?.item is ItemSword ) ||
            ( onlyMoveBow && mc.thePlayer.heldItem?.item is ItemBow ) )
        ) return

        if (isUsingItem || shouldSwap) {
            when (mc.thePlayer.heldItem?.item) {
                is ItemSword -> if (blocking) modeModuleBlocking.onUpdate() else return
                is ItemFood, is ItemPotion, is ItemBucketMilk -> if (consuming) modeModuleConsume.onUpdate() else return
                is ItemBow -> if (bows) modeModuleBow.onUpdate() else return
            }
        }
    }
    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (
            ( isUsingItem || shouldSwap ) && ( mc.thePlayer.motionX != 0.0 || mc.thePlayer.motionZ != 0.0 ) && !(
                ( onlyMoveConsume && isHoldingConsumable ) ||
                ( onlyMoveBlocking && mc.thePlayer.heldItem?.item is ItemSword ) ||
                ( onlyMoveBow && mc.thePlayer.heldItem?.item is ItemBow )
            )
        ) {
            when (mc.thePlayer.heldItem?.item) {
                is ItemSword -> if (blocking) modeModuleBlocking.onPacket(event) else return
                is ItemFood, is ItemPotion, is ItemBucketMilk -> if (consuming) modeModuleConsume.onPacket(event) else return
                is ItemBow -> if (bows) modeModuleBow.onPacket(event) else return
            }
        }

        val packet = event.packet
        if (event.isCancelled || shouldSwap)
            return
        when (packet) {
            is C08PacketPlayerBlockPlacement -> {
                if (packet.stack?.item != null && mc.thePlayer.heldItem?.item != null && packet.stack.item == mc.thePlayer.heldItem?.item) {
                    if ((modeModuleConsume == UNCP && (packet.stack.item is ItemFood || packet.stack.item is ItemPotion || packet.stack.item is ItemBucketMilk)) || (modeModuleBow == UNCP && packet.stack.item is ItemBow)) {
                        shouldSwap = true
                    }
                }
            }
        }
    }
    override val tag
        get() = if (blocking) blockingMode
                else if (consuming) consumeMode
                else if (bows) bowMode
                else if (sneaking) sneakMode
                else ""
    @EventTarget
    fun onSlowDown(event: SlowDownEvent) {
        val heldItem = mc.thePlayer.heldItem.item
        event.forward = getMultiplier(heldItem, true)
        event.strafe = getMultiplier(heldItem, false)
    }
    private fun getMultiplier(item: Item?, isForward: Boolean) = when (item) {
        is ItemFood, is ItemPotion, is ItemBucketMilk -> if (consuming) { if (isForward) consumeForwardMultiplier else consumeStrafeMultiplier } else 0.2f

        is ItemSword -> if (blocking) { if (isForward) blockForwardMultiplier else blockStrafeMultiplier } else 0.2f

        is ItemBow -> if (bows) { if (isForward) bowForwardMultiplier else bowStrafeMultiplier } else 0.2f

        else -> 0.2f
    }
    fun isUNCPBlocking() = modeModuleBlocking == UNCP2 && mc.gameSettings.keyBindUseItem.isKeyDown && (mc.thePlayer.heldItem?.item is ItemSword)
    private val isUsingItem get() = mc.thePlayer?.heldItem != null && (mc.thePlayer.isUsingItem || (mc.thePlayer.heldItem?.item is ItemSword && KillAura.blockStatus) || isUNCPBlocking())
    private val isHoldingConsumable get() = mc.thePlayer.heldItem?.item is ItemFood || mc.thePlayer.heldItem?.item is ItemPotion || mc.thePlayer.heldItem?.item is ItemBucketMilk
}
