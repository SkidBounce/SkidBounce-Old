/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.modules.world.Scaffold
import net.ccbluex.liquidbounce.features.module.modules.combat.SuperKnockback
import net.ccbluex.liquidbounce.utils.MovementUtils.isMoving
import net.ccbluex.liquidbounce.utils.RotationUtils.currentRotation
import net.ccbluex.liquidbounce.utils.RotationUtils.strict
import net.ccbluex.liquidbounce.utils.inventory.InventoryUtils.serverOpenInventory
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.minecraft.network.play.client.C0BPacketEntityAction.Action.START_SPRINTING
import net.minecraft.network.play.client.C0BPacketEntityAction
import net.minecraft.potion.Potion
import net.minecraft.util.MovementInput
import kotlin.math.abs

object Sprint : Module("Sprint", ModuleCategory.MOVEMENT, gameDetecting = false) {
    val onlyOnSprintPress by BoolValue("OnlyOnSprintPress", false)
    private val alwaysCorrect by BoolValue("AlwaysCorrectSprint", false)
    private val legit by BoolValue("Legit", false)
    val jumpDirections by BoolValue("JumpDirections", false)

    private val allDirections by BoolValue("AllDirections", true) { !legit }
    private val allDirectionsLimitSpeed by FloatValue("AllDirectionsLimitSpeed", 1f, 0.75f..1f) { !legit && allDirections }
    private val allDirectionsLimitSpeedGround by BoolValue("AllDirectionsLimitSpeedOnlyGround", true) { !legit && allDirections }

    private val collide by BoolValue("Collide", true)
    private val blindness by BoolValue("Blindness", true)
    private val sneaking by BoolValue("Sneaking", true)
    private val usingItem by BoolValue("UsingItem", true)
    private val inventory by BoolValue("Inventory", true)
    private val hunger by BoolValue("Hunger", false)

    private val checkServerSide by BoolValue("CheckServerSide", false)
    private val checkServerSideGround by BoolValue("CheckServerSideOnlyGround", false) { checkServerSide }
    private val silent by BoolValue("Silent", false)

    private var isSprinting = false

    fun correctSprintState(movementInput: MovementInput, isUsingItem: Boolean) {
        val player = mc.thePlayer ?: return

        if (SuperKnockback.breakSprint()) {
            player.isSprinting = false
            return
        }

        if ((onlyOnSprintPress || !handleEvents()) && !player.isSprinting && !mc.gameSettings.keyBindSprint.isKeyDown && !SuperKnockback.startSprint() && !isSprinting)
            return

        if (Scaffold.handleEvents()) {
            if (!Scaffold.sprint) {
                player.isSprinting = false
                isSprinting = false
                return
            } else if (Scaffold.sprint && Scaffold.eagle == "Normal" && isMoving && player.onGround && Scaffold.eagleSneaking && Scaffold.eagleSprint) {
                player.isSprinting = true
                isSprinting = true
                return
            }
        }

        if (handleEvents() || alwaysCorrect) {
            player.isSprinting = !shouldStopSprinting(movementInput, isUsingItem)
            isSprinting = player.isSprinting
            if (player.isSprinting && allDirections && !legit) {
                if (!allDirectionsLimitSpeedGround || player.onGround) {
                    player.motionX *= allDirectionsLimitSpeed
                    player.motionZ *= allDirectionsLimitSpeed
                }
            }
        }
    }

    private fun shouldStopSprinting(movementInput: MovementInput, isUsingItem: Boolean): Boolean {
        mc.thePlayer ?: return false

        val modifiedForward =
            if (currentRotation != null && strict) mc.thePlayer.movementInput.moveForward
            else movementInput.moveForward

        if (!isMoving)
            return true

        if (!collide && mc.thePlayer.isCollidedHorizontally)
            return true

        if (!sneaking && mc.thePlayer.isSneaking)
            return true

        if (!blindness && mc.thePlayer.isPotionActive(Potion.blindness) && !mc.thePlayer.isSprinting)
            return true

        if (!hunger && !(mc.thePlayer.foodStats.foodLevel > 6f || mc.thePlayer.capabilities.allowFlying))
            return true

        if (!usingItem && isUsingItem)
            return true

        if (!inventory && serverOpenInventory)
            return true

        if (legit)
            return modifiedForward < 0.0

        if (allDirections && !checkServerSide)
            return false

        val playerForwardInput = mc.thePlayer.movementInput.moveForward

        if (!checkServerSide) {
            return if (currentRotation == null) playerForwardInput < 0.0
            else abs(playerForwardInput) < 0.0 || playerForwardInput < 0 && modifiedForward < 0.0
        }
        if (checkServerSideGround && !mc.thePlayer.onGround)
            return currentRotation == null && modifiedForward < 0.0

        return modifiedForward < 0.0
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (event.packet is C0BPacketEntityAction && silent && event.packet.action == START_SPRINTING && !event.isCancelled)
            event.cancelEvent()
    }
}
