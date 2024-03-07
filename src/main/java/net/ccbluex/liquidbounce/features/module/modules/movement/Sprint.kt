/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement

import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.modules.combat.SuperKnockback
import net.ccbluex.liquidbounce.features.module.modules.world.Scaffold
import net.ccbluex.liquidbounce.utils.MovementUtils.isMoving
import net.ccbluex.liquidbounce.utils.RotationUtils.currentRotation
import net.ccbluex.liquidbounce.utils.RotationUtils.strict
import net.ccbluex.liquidbounce.utils.inventory.InventoryUtils.serverOpenInventory
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.minecraft.potion.Potion
import net.minecraft.util.MovementInput
import kotlin.math.abs

object Sprint : Module("Sprint", ModuleCategory.MOVEMENT, gameDetecting = false) {
    val onlyOnSprintPress by BoolValue("OnlyOnSprintPress", false)
    private val alwaysCorrect by BoolValue("AlwaysCorrectSprint", false)
    val jumpDirections by BoolValue("JumpDirections", false)

    private val sideways by BoolValue("Sideways", false)
    private val sidewaysGround by BoolValue("Sideways-Ground", true) { sideways }
    private val sidewaysAir by BoolValue("Sideways-Air", true) { sideways }
    private val backwards by BoolValue("Backwards", false)
    private val backwardsGround by BoolValue("Backwards-Ground", true) { backwards }
    private val backwardsAir by BoolValue("Backwards-Air", true) { backwards }

    private val limitSpeed by BoolValue("LimitSpeed", false)
    private val limitSpeedSideways by BoolValue("LimitSpeed-Sideways", true) { limitSpeed && sideways }
    private val limitSpeedBackwards by BoolValue("LimitSpeed-Backwards", true) { limitSpeed && backwards }
    private val limitSpeedForwards by BoolValue("LimitSpeed-Forwards", false) { limitSpeed }
    private val limitSpeedMulti by FloatValue("LimitSpeed-Multi", 1f, 0.75f..1f) { limitSpeed }
    private val limitSpeedGround by BoolValue("LimitSpeed-Ground", true) { limitSpeed }
    private val limitSpeedAir by BoolValue("LimitSpeed-Air", true) { limitSpeed }

    private val still by BoolValue("Still", false)
    private val collide by BoolValue("Collide", true)
    private val blindness by BoolValue("Blindness", true)
    private val sneaking by BoolValue("Sneaking", true)
    private val usingItem by BoolValue("UsingItem", true)
    private val inventory by BoolValue("Inventory", true)
    private val hunger by BoolValue("Hunger", false)

    private val checkServerSide by BoolValue("CheckServerSide", false)
    private val checkServerSideGround by BoolValue("CheckServerSide-Ground", true) { checkServerSide }
    private val checkServerSideAir by BoolValue("CheckServerSide-Air", true) { checkServerSide }
    val silent by BoolValue("Silent", false)

    private var isSprinting = false
    private val isBackwards
        get() = if (backwards) { if (mc.thePlayer.onGround) backwardsGround else backwardsAir } else false
    private val isSideways
        get() = if (sideways) { if (mc.thePlayer.onGround) sidewaysGround else sidewaysAir } else false
    private val doCheckServerSide
        get() = if (checkServerSide) if (mc.thePlayer.onGround) checkServerSideGround else checkServerSideAir else true
    fun correctSprintState(movementInput: MovementInput, isUsingItem: Boolean) {
        val player = mc.thePlayer ?: return

        if (SuperKnockback.breakSprint()) {
            player.isSprinting = false
            return
        }

        if ((onlyOnSprintPress || !handleEvents())
            && !player.isSprinting
            && !mc.gameSettings.keyBindSprint.isKeyDown
            && !SuperKnockback.startSprint()
            && !isSprinting
        ) return

        if (Scaffold.handleEvents()) {
            if (!Scaffold.sprint) {
                player.isSprinting = false
                isSprinting = false
                return
            } else if (Scaffold.sprint
                && Scaffold.eagle == "Normal"
                && isMoving
                && player.onGround
                && Scaffold.eagleSneaking
                && Scaffold.eagleSprint
            ) {
                player.isSprinting = true
                isSprinting = true
                return
            }
        }

        if (handleEvents() || alwaysCorrect) {
            player.isSprinting = !shouldStopSprinting(movementInput, isUsingItem)
            isSprinting = player.isSprinting
            limitSpeed(movementInput)
        }
    }

    private fun limitSpeed(movementInput: MovementInput) {
        if (!limitSpeed || !mc.thePlayer.isSprinting)
            return

        val modifiedForward =
            if (currentRotation != null && strict) mc.thePlayer.movementInput.moveForward
            else movementInput.moveForward

        if (modifiedForward > 0f && !limitSpeedForwards)
            return
        if (modifiedForward == 0f && !limitSpeedSideways)
            return
        if (modifiedForward < 0f && !limitSpeedBackwards)
            return

        if ((mc.thePlayer.onGround && limitSpeedGround) || (!mc.thePlayer.onGround && limitSpeedAir)) {
            mc.thePlayer.motionX *= limitSpeedMulti
            mc.thePlayer.motionZ *= limitSpeedMulti
        }
    }

    private fun shouldStopSprinting(movementInput: MovementInput, isUsingItem: Boolean): Boolean {
        mc.thePlayer ?: return false

        if ((!usingItem && isUsingItem)
            || (!inventory && serverOpenInventory)
            || (!sneaking && mc.thePlayer.isSneaking)
            || (!collide && mc.thePlayer.isCollidedHorizontally)
            || (!blindness && mc.thePlayer.isPotionActive(Potion.blindness) && !mc.thePlayer.isSprinting)
            || (!hunger && !(mc.thePlayer.foodStats.foodLevel > 6f || mc.thePlayer.capabilities.allowFlying))
        ) return true

        if (!isMoving)
            return !still

        val playerForwardInput =
            if (isSideways && isBackwards) false
            else if (isSideways) mc.thePlayer.movementInput.moveForward < 0f
            else if (isBackwards) mc.thePlayer.movementInput.moveForward == 0f
            else mc.thePlayer.movementInput.moveForward <= 0f

        val absPlayerForwardInput =
            if (isSideways && isBackwards) false
            else if (isSideways) abs(mc.thePlayer.movementInput.moveForward) < 0f
            else if (isBackwards) abs(mc.thePlayer.movementInput.moveForward) == 0f
            else abs(mc.thePlayer.movementInput.moveForward) <= 0f

        val modifiedInputForward =
            if (currentRotation != null && strict) mc.thePlayer.movementInput.moveForward
            else movementInput.moveForward

        val modifiedForward =
            if (isSideways && isBackwards) false
            else if (isSideways) modifiedInputForward < 0f
            else if (isBackwards) modifiedInputForward == 0f
            else modifiedInputForward <= 0f

        if (!doCheckServerSide)
            return if (currentRotation == null) playerForwardInput
                else absPlayerForwardInput || playerForwardInput && modifiedForward

        return modifiedForward
    }
}
