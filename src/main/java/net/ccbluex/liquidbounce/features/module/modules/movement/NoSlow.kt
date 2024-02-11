/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement

import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.event.EventState.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura
import net.ccbluex.liquidbounce.features.module.modules.movement.noslowmodes.NoSlowMode
import net.ccbluex.liquidbounce.features.module.modules.movement.noslowmodes.aac.*
import net.ccbluex.liquidbounce.features.module.modules.movement.noslowmodes.ncp.*
import net.ccbluex.liquidbounce.features.module.modules.movement.noslowmodes.other.*
import net.ccbluex.liquidbounce.features.module.modules.movement.noslowmodes.watchdog.*
import net.ccbluex.liquidbounce.utils.MovementUtils.hasMotion
import net.ccbluex.liquidbounce.utils.PacketUtils.sendPacket
import net.ccbluex.liquidbounce.utils.PacketUtils.sendPackets
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
        Place,
        EmptyPlace,
        Slot,
        WatchDog,
        WatchDog2,
        Horizon,
        Medusa,
    ).sortedBy { it.modeName }
    private val consumeModes = arrayOf(
        Vanilla,
        SwitchItem,
        OldIntave,
        UNCP,
        UNCP2,
        Place,
        EmptyPlace,
        Slot,
        Horizon,
        Medusa,
    ).sortedBy { it.modeName }
    private val bowModes = arrayOf(
        Vanilla,
        SwitchItem,
        OldIntave,
        UNCP,
        UNCP2,
        Place,
        EmptyPlace,
        Slot,
        Horizon,
        Medusa,
    ).sortedBy { it.modeName }

    private val noNoMoveCheck = arrayOf(
        "Vanilla",
        "Horizon"
    )

    var shouldSwap = false

    override fun onDisable() {
        shouldSwap = false
        Blocks.slime_block.slipperiness = 0.8f
    }

    private val blocking by BoolValue("Blocking", true)
    private val blockingMode by ListValue("BlockingMode", swordModes.map { it.modeName }.toTypedArray(), "Vanilla") { blocking }
    private val onlyMoveBlocking by BoolValue("OnlyMoveBlocking", true) { blocking && blockingMode !in noNoMoveCheck }
    private val blockingPacketTiming by ListValue("BlockingPacketTiming", arrayOf("Pre", "Post", "Any"), "Pre") { blocking && blockingMode in arrayOf("Slot", "Place", "EmptyPlace") }
    private val blockForwardMultiplier by FloatValue("BlockForwardMultiplier", 1f, 0.2f..1f) { blocking }
    private val blockStrafeMultiplier by FloatValue("BlockStrafeMultiplier", 1f, 0.2f..1f) { blocking }

    private val consuming by BoolValue("Consuming", true)
    private val consumeMode by ListValue("ConsumeMode", consumeModes.map { it.modeName }.toTypedArray(), "Vanilla") { consuming }
    private val onlyMoveConsume by BoolValue("OnlyMoveConsume", true) { consuming && consumeMode !in noNoMoveCheck }
    private val consumePacketTiming by ListValue("ConsumePacketTiming", arrayOf("Pre", "Post", "Any"), "Pre") { consuming && consumeMode in arrayOf("Slot", "Place", "EmptyPlace") }
    private val consumeForwardMultiplier by FloatValue("ConsumeForwardMultiplier", 1f, 0.2f..1f) { consuming }
    private val consumeStrafeMultiplier by FloatValue("ConsumeStrafeMultiplier", 1f, 0.2f..1f) { consuming }

    private val bows by BoolValue("Bows", true)
    private val bowMode by ListValue("BowMode", bowModes.map { it.modeName }.toTypedArray(), "Vanilla") { bows }
    private val onlyMoveBow by BoolValue("OnlyMoveBow", true) { bows && bowMode !in noNoMoveCheck }
    private val bowPacketTiming by ListValue("BowPacketTiming", arrayOf("Pre", "Post", "Any"), "Pre") { bows && bowMode in arrayOf("Slot", "Place", "EmptyPlace") }
    private val bowForwardMultiplier by FloatValue("BowForwardMultiplier", 1f, 0.2f..1f) { bows }
    private val bowStrafeMultiplier by FloatValue("BowStrafeMultiplier", 1f, 0.2f..1f) { bows }

    val sneaking by BoolValue("Sneaking", true)
    private val sneakMode by ListValue("SneakMode", arrayOf("Vanilla", "Switch", "MineSecure"), "Vanilla") { sneaking }
    private val onlyMoveSneak by BoolValue("OnlyMoveSneak", true) { sneaking && sneakMode != "Vanilla" }
    val sneakForwardMultiplier by FloatValue("SneakForwardMultiplier", 0.3f, 0.3f..1.0F) { sneaking }
    val sneakStrafeMultiplier by FloatValue("SneakStrafeMultiplier", 0.3f, 0.3f..1f) { sneaking }

    val soulsand by BoolValue("SoulSand", true)
    val soulsandMultiplier by FloatValue("SoulSandMultiplier", 1f, 0.4f..1f) { soulsand }

    val slime by BoolValue("Slime", true)
    val slimeYMultiplier by FloatValue("SlimeYMultiplier", 1f, 0.2f..1f) { slime }
    val slimeMultiplier by FloatValue("SlimeMultiplier", 1f, 0.4f..1f) { slime }
    private val slimeFriction by FloatValue("SlimeFriction", 0.6f, 0.6f..0.8f) { slime }

    val liquidPush by BoolValue("LiquidPush", true)

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
                "Switch" -> when (event.eventState) {
                    PRE -> {
                        sendPackets(
                            C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING),
                            C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING)
                        )
                    }
                    POST -> {
                        sendPackets(
                            C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING),
                            C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING)
                        )
                    }
                    else -> {}
                }
                "MineSecure" -> {
                    if (event.eventState == PRE)
                        return

                    sendPacket(
                        C0BPacketEntityAction(
                            mc.thePlayer,
                            C0BPacketEntityAction.Action.START_SNEAKING
                        )
                    )
                }
            }
        }

        if (!shouldSwap && noMoveCheck) return

        if (isUsingItem || shouldSwap)
            usedMode.onMotion(event)
    }

    @EventTarget
    fun onUpdate() {
        if (serverUsing && !isUsingItem)
            serverUsing = false

        if (!noMoveCheck && isUsingItem)
            usedMode.onUpdate()
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (!noMoveCheck)
            usedMode.onPacket(event)

        val packet = event.packet
        if (event.isCancelled || shouldSwap)
            return

        if (packet is C08PacketPlayerBlockPlacement) {
            if (packet.stack?.item != null && mc.thePlayer.heldItem?.item != null && packet.stack.item == mc.thePlayer.heldItem?.item) {
                if ((modeModuleConsume == UNCP && (packet.stack.item is ItemFood || packet.stack.item is ItemPotion || packet.stack.item is ItemBucketMilk)) || (modeModuleBow == UNCP && packet.stack.item is ItemBow)) {
                    shouldSwap = true
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
        event.forward = when (noSlowItem) {
            NoSlowItem.CONSUMABLE -> consumeForwardMultiplier
            NoSlowItem.SWORD -> blockForwardMultiplier
            NoSlowItem.BOW -> bowForwardMultiplier
            NoSlowItem.OTHER -> 0.2f
        }
        event.strafe = when (noSlowItem) {
            NoSlowItem.CONSUMABLE -> consumeStrafeMultiplier
            NoSlowItem.SWORD -> blockStrafeMultiplier
            NoSlowItem.BOW -> bowStrafeMultiplier
            NoSlowItem.OTHER -> 0.2f
        }
    }

    private val noMoveCheck
        get() = noSlowItem.run {
            if (usedMode.modeName in noNoMoveCheck || hasMotion)
                return@run false

            return@run when (this) {
                NoSlowItem.SWORD -> onlyMoveBlocking
                NoSlowItem.BOW -> onlyMoveBow
                NoSlowItem.CONSUMABLE -> onlyMoveConsume
                NoSlowItem.OTHER -> false
            }
        }

    fun packetTiming(eventState: EventState) =
        when (noSlowItem) {
            NoSlowItem.SWORD -> eventState.stateName == blockingPacketTiming.uppercase() || blockingPacketTiming == "Any"
            NoSlowItem.BOW -> eventState.stateName == bowPacketTiming.uppercase() || bowPacketTiming == "Any"
            NoSlowItem.CONSUMABLE -> eventState.stateName == consumePacketTiming.uppercase() || consumePacketTiming == "Any"
            NoSlowItem.OTHER -> false
        }

    private val usedMode: NoSlowMode
        get() = when (noSlowItem) {
            NoSlowItem.SWORD -> modeModuleBlocking
            NoSlowItem.BOW -> modeModuleBow
            NoSlowItem.CONSUMABLE -> modeModuleConsume
            NoSlowItem.OTHER -> Vanilla
        }

    fun isUNCPBlocking() = modeModuleBlocking == UNCP2 && mc.gameSettings.keyBindUseItem.isKeyDown && (mc.thePlayer.heldItem?.item is ItemSword)
    private val isUsingItem get() = mc.thePlayer?.heldItem != null && (mc.thePlayer.isUsingItem || (mc.thePlayer.heldItem?.item is ItemSword && KillAura.blockStatus) || isUNCPBlocking())

    private val noSlowItem: NoSlowItem
        get() = mc.thePlayer?.heldItem?.item?.run {
            return@run when {
                this is ItemSword && blocking -> NoSlowItem.SWORD
                this is ItemBow && bows -> NoSlowItem.BOW
                (this is ItemPotion || this is ItemFood || this is ItemBucketMilk) && consuming-> NoSlowItem.CONSUMABLE
                else -> NoSlowItem.OTHER
            }
        } ?: NoSlowItem.OTHER
    enum class NoSlowItem { CONSUMABLE, SWORD, BOW, OTHER }
}
