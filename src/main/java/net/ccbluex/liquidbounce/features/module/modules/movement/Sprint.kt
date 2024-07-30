/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement

import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory.MOVEMENT
import net.ccbluex.liquidbounce.features.module.modules.combat.SuperKnockback
import net.ccbluex.liquidbounce.features.module.modules.world.Scaffold
import net.ccbluex.liquidbounce.utils.MovementUtils.isMoving
import net.ccbluex.liquidbounce.utils.RotationUtils.currentRotation
import net.ccbluex.liquidbounce.utils.RotationUtils.strict
import net.ccbluex.liquidbounce.utils.inventory.InventoryUtils.serverOpenInventory
import net.ccbluex.liquidbounce.value.BooleanValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.minecraft.potion.Potion
import net.minecraft.util.MovementInput
import kotlin.math.abs

object Sprint : Module("Sprint", MOVEMENT, gameDetecting = false) {
    val onlyOnSprintPress by BooleanValue("OnlyOnSprintPress", false)
    private val alwaysCorrect by BooleanValue("AlwaysCorrectSprint", false)
    val jumpDirections by BooleanValue("JumpDirections", false)

    private val sideways by BooleanValue("Sideways", false)
    private val sidewaysGround by BooleanValue("Sideways-Ground", true) { sideways }
    private val sidewaysAir by BooleanValue("Sideways-Air", true) { sideways }
    private val backwards by BooleanValue("Backwards", false)
    private val backwardsGround by BooleanValue("Backwards-Ground", true) { backwards }
    private val backwardsAir by BooleanValue("Backwards-Air", true) { backwards }

    private val limitSpeed by BooleanValue("LimitSpeed", false)
    private val limitSpeedSideways by BooleanValue("LimitSpeed-Sideways", true) { limitSpeed && sideways }
    private val limitSpeedBackwards by BooleanValue("LimitSpeed-Backwards", true) { limitSpeed && backwards }
    private val limitSpeedForwards by BooleanValue("LimitSpeed-Forwards", false) { limitSpeed }
    private val limitSpeedMulti by FloatValue("LimitSpeed-Multi", 1f, 0.75f..1f) { limitSpeed }
    private val limitSpeedGround by BooleanValue("LimitSpeed-Ground", true) { limitSpeed }
    private val limitSpeedAir by BooleanValue("LimitSpeed-Air", true) { limitSpeed }

    private val still by BooleanValue("Still", false)
    private val collide by BooleanValue("Collide", true)
    private val blindness by BooleanValue("Blindness", true)
    private val sneaking by BooleanValue("Sneaking", true)
    private val usingItem by BooleanValue("UsingItem", true)
    private val usingItemOnlyNoSlow by BooleanValue("UsingItem-OnlyOnNoSlow", true) { usingItem }
    private val inventory by BooleanValue("Inventory", true)
    private val hunger by BooleanValue("Hunger", false)

    private val checkServerSide by BooleanValue("CheckServerSide", false)
    private val checkServerSideGround by BooleanValue("CheckServerSide-Ground", true) { checkServerSide }
    private val checkServerSideAir by BooleanValue("CheckServerSide-Air", true) { checkServerSide }
    val silent by BooleanValue("Silent", false)

    private var isSprinting = false
    private val isBackwards
        get() = if (backwards) {
            if (mc.thePlayer.onGround) backwardsGround else backwardsAir
        } else false
    private val isSideways
        get() = if (sideways) {
            if (mc.thePlayer.onGround) sidewaysGround else sidewaysAir
        } else false
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

        if (isUsingItem && (!usingItem || usingItemOnlyNoSlow && !NoSlow.doNoSlow())
            || !inventory && serverOpenInventory
            || !sneaking && mc.thePlayer.isSneaking
            || !collide && mc.thePlayer.isCollidedHorizontally
            || !blindness && mc.thePlayer.isPotionActive(Potion.blindness) && !mc.thePlayer.isSprinting
            || !hunger && mc.thePlayer.foodStats.foodLevel <= 6f && !mc.thePlayer.capabilities.allowFlying
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
