/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement

import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.item.*
import net.minecraft.init.Blocks
import net.ccbluex.liquidbounce.features.module.modules.movement.noslowmodes.aac.*
import net.ccbluex.liquidbounce.features.module.modules.movement.noslowmodes.ncp.*
import net.ccbluex.liquidbounce.features.module.modules.movement.noslowmodes.other.*
import net.ccbluex.liquidbounce.utils.extensions.stopXZ

object NoSlow : Module("NoSlow", ModuleCategory.MOVEMENT, gameDetecting = false) {
    private val swordModes = arrayOf(
        Vanilla,
        SwitchItem,
        NCP,
        UNCP,
        AAC,
        AAC5,
    )
    private val bowModes = arrayOf(
        Vanilla,
        SwitchItem,
        UNCP,
        AAC5,
    )
    private val consumeModes = arrayOf(
        Vanilla,
        SwitchItem,
        UNCP,
        AAC5,
    )

    private val blocking by BoolValue("Blocking", true)
    private val consuming by BoolValue("Consuming", true)
    private val bows by BoolValue("Bows", true)
    val sneaking by BoolValue("Sneaking", true)
    val soulsand by BoolValue("SoulSand", true)
    val liquidPush by BoolValue("LiquidPush", true)
    val slime by BoolValue("Slime", true)
    private val swordMode by ListValue("SwordMode", swordModes.map { it.modeName }.toTypedArray(), "Vanilla") { blocking }
    private val consumeMode by ListValue("ConsumeMode", consumeModes.map { it.modeName }.toTypedArray(), "Vanilla") { consuming }
    private val bowMode by ListValue("BowMode", bowModes.map { it.modeName }.toTypedArray(), "Vanilla") { bows }
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

    private val modeModuleSword
        get() = swordModes.find { it.modeName == swordMode }!!
    private val modeModuleBow
        get() = bowModes.find { it.modeName == bowMode }!!
    private val modeModuleConsume
        get() = consumeModes.find { it.modeName == consumeMode }!!

    @EventTarget
    fun onMotion(event: MotionEvent) {
        Blocks.slime_block.slipperiness = if (slime) slimeFriction else 0.8f

        if (mc.thePlayer.isUsingItem) {
            when (mc.thePlayer.heldItem?.item) {
                is ItemSword -> if (blocking) modeModuleSword.onMotion(event) else return
                is ItemFood, is ItemPotion, is ItemBucketMilk -> if (consuming) modeModuleConsume.onMotion(event) else return
                is ItemBow -> if (bows) modeModuleBow.onMotion(event) else return
            }
        }
    }
    @EventTarget
    fun onUpdate() {
        if (mc.thePlayer.isUsingItem) {
            when (mc.thePlayer.heldItem?.item) {
                is ItemSword -> if (blocking) modeModuleSword.onUpdate() else return
                is ItemFood, is ItemPotion, is ItemBucketMilk -> if (consuming) modeModuleConsume.onUpdate() else return
                is ItemBow -> if (bows) modeModuleBow.onUpdate() else return
            }
        }
    }
    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (mc.thePlayer.isUsingItem) {
            when (mc.thePlayer.heldItem?.item) {
                is ItemSword -> if (blocking) modeModuleSword.onPacket(event) else return
                is ItemFood, is ItemPotion, is ItemBucketMilk -> if (consuming) modeModuleConsume.onPacket(event) else return
                is ItemBow -> if (bows) modeModuleBow.onPacket(event) else return
            }
        }
    }

    /**
     * Not sure how it works, but it should allow you to
     * block again after jumping by stopping the player xz.
     */
    @EventTarget
    fun onJump(event: JumpEvent) {
        if (modeModuleSword == UNCP && mc.thePlayer.heldItem.item is ItemSword && mc.thePlayer.isBlocking) {
            mc.thePlayer.stopXZ()
        }
    }
    override val tag
        get() = swordMode
    @EventTarget
    fun onSlowDown(event: SlowDownEvent) {
        val heldItem = mc.thePlayer.heldItem?.item
        event.forward = getMultiplier(heldItem, true)
        event.strafe = getMultiplier(heldItem, false)
    }
    private fun getMultiplier(item: Item?, isForward: Boolean) = when (item) {
        is ItemFood, is ItemPotion, is ItemBucketMilk -> if (consuming) { if (isForward) consumeForwardMultiplier else consumeStrafeMultiplier } else 0.2F

        is ItemSword -> if (blocking) { if (isForward) blockForwardMultiplier else blockStrafeMultiplier } else 0.2F

        is ItemBow -> if (bows) { if (isForward) bowForwardMultiplier else bowStrafeMultiplier } else 0.2F

        else -> 0.2F
    }
}
