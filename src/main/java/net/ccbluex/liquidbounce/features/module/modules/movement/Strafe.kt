/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.events.JumpEvent
import net.ccbluex.liquidbounce.event.events.StrafeEvent
import net.ccbluex.liquidbounce.event.events.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory.MOVEMENT
import net.ccbluex.liquidbounce.utils.MovementUtils.direction
import net.ccbluex.liquidbounce.utils.MovementUtils.isMoving
import net.ccbluex.liquidbounce.utils.MovementUtils.speed
import net.ccbluex.liquidbounce.utils.extensions.inLiquid
import net.ccbluex.liquidbounce.utils.extensions.jmp
import net.ccbluex.liquidbounce.utils.extensions.toDegreesF
import net.ccbluex.liquidbounce.value.BooleanValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntValue
import kotlin.math.cos
import kotlin.math.sin

object Strafe : Module("Strafe", MOVEMENT, gameDetecting = false) {

    private val strength by FloatValue("Strength", 0.5F, 0F..1F)
    private val noMoveStop by BooleanValue("NoMoveStop", false)

    private val inAir by BooleanValue("InAir", true)
    private val inLiquid by BooleanValue("InLiquid", true)
    private val outOfLiquidTicks by IntValue("OutOfLiquidTicks", 40, 0..100)
    private val onGround by BooleanValue("OnGround", false)

    private val allDirectionsJump by BooleanValue("AllDirectionsJump", false)

    private var wasDown = false
    private var jump = false
    private var ticksOutOfLiquid = 0

    @EventTarget
    fun onJump(event: JumpEvent) {
        if (jump) {
            event.cancelEvent()
        }
    }

    override fun onEnable() {
        wasDown = false
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (mc.thePlayer.inLiquid) {
            ticksOutOfLiquid = 0
        } else ticksOutOfLiquid++

        if (mc.thePlayer.onGround && mc.gameSettings.keyBindJump.isKeyDown && allDirectionsJump && isMoving && !(mc.thePlayer.isInWater || mc.thePlayer.isInLava || mc.thePlayer.isOnLadder || mc.thePlayer.isInWeb)) {
            if (mc.gameSettings.keyBindJump.isKeyDown) {
                mc.gameSettings.keyBindJump.pressed = false
                wasDown = true
            }
            val yaw = mc.thePlayer.rotationYaw
            mc.thePlayer.rotationYaw = direction.toDegreesF()
            mc.thePlayer.jmp()
            mc.thePlayer.rotationYaw = yaw
            jump = true
            if (wasDown) {
                mc.gameSettings.keyBindJump.pressed = true
                wasDown = false
            }
        } else {
            jump = false
        }
    }

    @EventTarget
    fun onStrafe(event: StrafeEvent) {
        if (!isMoving) {
            if (noMoveStop) {
                mc.thePlayer.motionX = 0.0
                mc.thePlayer.motionZ = 0.0
            }
            return
        }

        if (shouldStrafe) {
            val speed = speed * strength
            val yaw = direction

            mc.thePlayer.motionX = mc.thePlayer.motionX * (1 - strength) - sin(yaw) * speed
            mc.thePlayer.motionZ = mc.thePlayer.motionZ * (1 - strength) + cos(yaw) * speed
        }
    }

    private val shouldStrafe: Boolean
        get() {
            if (inLiquid && mc.thePlayer.inLiquid) return true
            if (ticksOutOfLiquid <= outOfLiquidTicks) return true

            return if (mc.thePlayer.onGround) onGround else inAir
        }
}
