/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement

import net.ccbluex.liquidbounce.event.EventState
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.EventState.POST
import net.ccbluex.liquidbounce.event.EventState.PRE
import net.ccbluex.liquidbounce.event.events.MotionEvent
import net.ccbluex.liquidbounce.event.events.PacketEvent
import net.ccbluex.liquidbounce.event.events.SlowDownEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory.MOVEMENT
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura
import net.ccbluex.liquidbounce.features.module.modules.movement.noslowmodes.NoSlowMode
import net.ccbluex.liquidbounce.features.module.modules.movement.noslowmodes.aac.*
import net.ccbluex.liquidbounce.features.module.modules.movement.noslowmodes.ncp.*
import net.ccbluex.liquidbounce.features.module.modules.movement.noslowmodes.other.*
import net.ccbluex.liquidbounce.features.module.modules.movement.noslowmodes.watchdog.*
import net.ccbluex.liquidbounce.utils.MovementUtils.hasMotion
import net.ccbluex.liquidbounce.utils.NoSlowItem
import net.ccbluex.liquidbounce.utils.NoSlowItem.*
import net.ccbluex.liquidbounce.utils.PacketUtils.sendPacket
import net.ccbluex.liquidbounce.utils.PacketUtils.sendPackets
import net.ccbluex.liquidbounce.utils.inventory.InventoryUtils.serverUsing
import net.ccbluex.liquidbounce.value.BooleanValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.init.Blocks
import net.minecraft.item.*
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraft.network.play.client.C0BPacketEntityAction
import net.minecraft.network.play.client.C0BPacketEntityAction.Action.START_SNEAKING
import net.minecraft.network.play.client.C0BPacketEntityAction.Action.STOP_SNEAKING

object NoSlow : Module("NoSlow", MOVEMENT, gameDetecting = false) {
    private val swordModes = arrayOf(
        Vanilla,
        SwitchItem,
        OldIntave,
        NCP,
        NewNCP,
        UNCP,
        UNCP2,
        AAC,
        AAC2,
        Place,
        EmptyPlace,
        WatchDog,
        WatchDog2,
        Medusa,
        Drop,
        Grim,
    ).sortedBy { it.modeName }
    private val consumeModes = arrayOf(
        Vanilla,
        SwitchItem,
        OldIntave,
        UNCP,
        UNCP2,
        Place,
        EmptyPlace,
        Medusa,
        Drop,
        Grim,
    ).sortedBy { it.modeName }
    private val bowModes = arrayOf(
        Vanilla,
        SwitchItem,
        OldIntave,
        UNCP,
        UNCP2,
        Place,
        EmptyPlace,
        Medusa,
        Drop,
        Grim,
    ).sortedBy { it.modeName }

    private val noNoMoveCheck = setOf(
        "Vanilla",
        "Drop",
    )

    private val noAntiDesync = setOf(
        "Drop",
    )

    private val packetTiming = setOf(
        "SwitchItem",
        "Place",
        "EmptyPlace",
    )

    var shouldSwap = false

    override fun onDisable() {
        shouldSwap = false
        Blocks.slime_block.slipperiness = 0.8f
    }

    private val blocking by BooleanValue("Blocking", true)
    private val blockingMode by ListValue("BlockingMode", swordModes.map { it.modeName }.toTypedArray(), "Vanilla") { blocking }
    private val onlyMoveBlocking by BooleanValue("OnlyMoveBlocking", true) { blocking && blockingMode !in noNoMoveCheck }
    private val blockingPacketTiming by ListValue("BlockingPacketTiming", arrayOf("Pre", "Post", "Any"), "Pre") { blocking && blockingMode in packetTiming }
    private val blockingDropWaitForPacket by BooleanValue("Blocking-Drop-WaitForPacket", true) { blocking && blockingMode == "Drop" }
    val blockingSwitchItemPackets by IntValue("Blocking-SwitchItem-Packets", 2, 1..9) { blocking && blockingMode == "SwitchItem" }
    val blockingSwitchItemEveryTick by BooleanValue("Blocking-SwitchItem-EveryTick", true) { blocking && blockingMode == "SwitchItem" }
    val ncpFunnyUsePacket by BooleanValue("NCP-FunnyUsePacket", false) { blocking && blockingMode == "NCP" }
    val ncpFunnyReleasePacket by BooleanValue("NCP-FunnyReleasePacket", false) { blocking && blockingMode == "NCP" }
    private val blockForwardMultiplier by FloatValue("BlockForwardMultiplier", 1f, 0.2f..1f) { blocking }
    private val blockStrafeMultiplier by FloatValue("BlockStrafeMultiplier", 1f, 0.2f..1f) { blocking }

    private val consuming by BooleanValue("Consuming", true)
    private val consumeMode by ListValue("ConsumeMode", consumeModes.map { it.modeName }.toTypedArray(), "Vanilla") { consuming }
    private val onlyMoveConsume by BooleanValue("OnlyMoveConsume", true) { consuming && consumeMode !in noNoMoveCheck }
    private val consumePacketTiming by ListValue("ConsumePacketTiming", arrayOf("Pre", "Post", "Any"), "Pre") { consuming && consumeMode in packetTiming }
    private val consumeDropWaitForPacket by BooleanValue("Consume-Drop-WaitForPacket", true) { consuming && consumeMode == "Drop" }
    val consumeSwitchItemPackets by IntValue("Consume-SwitchItem-Packets", 2, 1..9) { consuming && consumeMode == "SwitchItem" }
    val consumeSwitchItemEveryTick by BooleanValue("Consume-SwitchItem-EveryTick", true) { consuming && consumeMode == "SwitchItem" }
    private val consumeForwardMultiplier by FloatValue("ConsumeForwardMultiplier", 1f, 0.2f..1f) { consuming }
    private val consumeStrafeMultiplier by FloatValue("ConsumeStrafeMultiplier", 1f, 0.2f..1f) { consuming }

    private val bows by BooleanValue("Bows", true)
    private val bowMode by ListValue("BowMode", bowModes.map { it.modeName }.toTypedArray(), "Vanilla") { bows }
    private val onlyMoveBow by BooleanValue("OnlyMoveBow", true) { bows && bowMode !in noNoMoveCheck }
    private val bowPacketTiming by ListValue("BowPacketTiming", arrayOf("Pre", "Post", "Any"), "Pre") { bows && bowMode in packetTiming }
    private val bowDropWaitForPacket by BooleanValue("Bow-Drop-WaitForPacket", true) { bows && bowMode == "Drop" }
    val bowSwitchItemPackets by IntValue("Bow-SwitchItem-Packets", 2, 1..9) { bows && bowMode == "SwitchItem" }
    val bowSwitchItemEveryTick by BooleanValue("Bow-SwitchItem-EveryTick", true) { bows && bowMode == "SwitchItem" }
    private val bowForwardMultiplier by FloatValue("BowForwardMultiplier", 1f, 0.2f..1f) { bows }
    private val bowStrafeMultiplier by FloatValue("BowStrafeMultiplier", 1f, 0.2f..1f) { bows }

    @JvmStatic val sneaking by BooleanValue("Sneaking", true)
    private val sneakMode by ListValue("SneakMode", arrayOf("Vanilla", "Switch", "MineSecure"), "Vanilla") { sneaking }
    private val onlyMoveSneak by BooleanValue("OnlyMoveSneak", true) { sneaking && sneakMode != "Vanilla" }
    @JvmStatic val sneakForwardMultiplier by FloatValue("SneakForwardMultiplier", 0.3f, 0.3f..1.0F) { sneaking }
    @JvmStatic val sneakStrafeMultiplier by FloatValue("SneakStrafeMultiplier", 0.3f, 0.3f..1f) { sneaking }

    @JvmStatic val soulsand by BooleanValue("SoulSand", true)
    @JvmStatic val soulsandMultiplier by FloatValue("SoulSandMultiplier", 1f, 0.4f..1f) { soulsand }

    @JvmStatic val slime by BooleanValue("Slime", true)
    @JvmStatic val slimeYMultiplier by FloatValue("SlimeYMultiplier", 1f, 0.2f..1f) { slime }
    @JvmStatic val slimeMultiplier by FloatValue("SlimeMultiplier", 1f, 0.4f..1f) { slime }
    private val slimeFriction by FloatValue("SlimeFriction", 0.6f, 0.6f..0.8f) { slime }

    @EventTarget
    fun onMotion(event: MotionEvent) {
        antiDesync()

        Blocks.slime_block.slipperiness = if (slime) slimeFriction else 0.8f

        if (mc.thePlayer.isSneaking && !(onlyMoveSneak && !hasMotion) && sneaking) {
            when (sneakMode) {
                "Switch" -> when (event.eventState) {
                    PRE -> {
                        sendPackets(
                            C0BPacketEntityAction(mc.thePlayer, START_SNEAKING),
                            C0BPacketEntityAction(mc.thePlayer, STOP_SNEAKING)
                        )
                    }

                    POST -> {
                        sendPackets(
                            C0BPacketEntityAction(mc.thePlayer, STOP_SNEAKING),
                            C0BPacketEntityAction(mc.thePlayer, START_SNEAKING)
                        )
                    }
                    else -> {}
                }

                "MineSecure" -> {
                    if (event.eventState != PRE)
                        sendPacket(C0BPacketEntityAction(mc.thePlayer, START_SNEAKING))
                }
            }
        }

        if (!shouldSwap && noMoveCheck) return

        if (isUsingItem || shouldSwap)
            usedMode.onMotion(event)
    }

    @EventTarget
    fun onUpdate() {
        antiDesync()

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

    private fun doNoSlow(noSlowItem: NoSlowItem): Boolean {
        if (noSlowItem == OTHER)
            return false

        val mode = usedMode

        if (mode == Drop && !Drop.received && when (noSlowItem) {
            CONSUMABLE -> consumeDropWaitForPacket
            SWORD -> blockingDropWaitForPacket
            else -> bowDropWaitForPacket
        }) return false

        if (mode == Grim && Grim.slow)
            return false

        return true
    }

    @EventTarget
    fun onSlowDown(event: SlowDownEvent) {
        val item = noSlowItem

        if (!doNoSlow(item)) return

        event.forward = when (item) {
            CONSUMABLE -> consumeForwardMultiplier
            SWORD -> blockForwardMultiplier
            BOW -> bowForwardMultiplier
            OTHER -> 0.2f
        }
        event.strafe = when (item) {
            CONSUMABLE -> consumeStrafeMultiplier
            SWORD -> blockStrafeMultiplier
            BOW -> bowStrafeMultiplier
            OTHER -> 0.2f
        }
    }

    override val tag
        get() = if (blocking) blockingMode
        else if (consuming) consumeMode
        else if (bows) bowMode
        else if (sneaking) sneakMode
        else ""

    private val modeModuleBlocking
        get() = swordModes.find { it.modeName == blockingMode }!!
    private val modeModuleBow
        get() = bowModes.find { it.modeName == bowMode }!!
    private val modeModuleConsume
        get() = consumeModes.find { it.modeName == consumeMode }!!

    private val noMoveCheck
        get() = noSlowItem.run {
            if (usedMode.modeName in noNoMoveCheck || (mc.thePlayer.motionX != 0.0 && mc.thePlayer.motionZ != 0.0))
                return@run false

            return@run when (this) {
                SWORD -> onlyMoveBlocking
                BOW -> onlyMoveBow
                CONSUMABLE -> onlyMoveConsume
                OTHER -> false
            }
        }

    private fun antiDesync() {
        if (usedMode.modeName !in noAntiDesync && serverUsing && !isUsingItem)
            serverUsing = false
    }

    fun packetTiming(eventState: EventState) =
        when (noSlowItem) {
            SWORD -> eventState.name == blockingPacketTiming.uppercase() || blockingPacketTiming == "Any"
            BOW -> eventState.name == bowPacketTiming.uppercase() || bowPacketTiming == "Any"
            CONSUMABLE -> eventState.name == consumePacketTiming.uppercase() || consumePacketTiming == "Any"
            OTHER -> false
        }

    private val usedMode: NoSlowMode
        get() = when (noSlowItem) {
            SWORD -> modeModuleBlocking
            BOW -> modeModuleBow
            CONSUMABLE -> modeModuleConsume
            OTHER -> Vanilla
        }

    fun isUNCPBlocking() =
        modeModuleBlocking == UNCP2 && mc.gameSettings.keyBindUseItem.isKeyDown && (mc.thePlayer.heldItem?.item is ItemSword)

    private val isUsingItem get() = mc.thePlayer?.heldItem != null && (mc.thePlayer.isUsingItem || (mc.thePlayer.heldItem?.item is ItemSword && KillAura.blockStatus) || isUNCPBlocking())

    val noSlowItem: NoSlowItem
        get() = mc.thePlayer?.heldItem?.item?.run {
            return@run when {
                this is ItemSword && blocking -> SWORD
                this is ItemBow && bows -> BOW
                (this is ItemPotion || this is ItemFood || this is ItemBucketMilk) && consuming -> CONSUMABLE
                else -> OTHER
            }
        } ?: OTHER
}
