/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.utils

import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.utils.extensions.*
import net.minecraft.init.Blocks.*
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.potion.Potion
import net.minecraft.util.BlockPos
import kotlin.math.*

object MovementUtils : MinecraftInstance(), Listenable {

    val aboveVoid: Boolean
        get() {
            mc.thePlayer ?: return false
            mc.theWorld ?: return false
            var void = true
            var i = -(mc.thePlayer.posY - 1.4857625).toInt()

            while (i <= 0) {
                void = mc.theWorld.getCollisionBoxes(
                    mc.thePlayer.entityBoundingBox.offset(
                        mc.thePlayer.motionX * 0.5,
                        i.toDouble(),
                        mc.thePlayer.motionZ * 0.5
                    )
                ).isEmpty()
                ++i
                if (!void) break
            }

            return void
        }
    val onIce
        get() = mc.theWorld.getBlockState(BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0, mc.thePlayer.posZ)).block in arrayOf(packed_ice, ice)
    var speed
        get() = mc.thePlayer?.run { sqrt(motionX * motionX + motionZ * motionZ).toFloat() } ?: .0f
        set(value) { strafe(value) }

    val baseMoveSpeed
        get() = 0.2873 * 1.0 + if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) 0.2 * (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).amplifier + 1).toDouble() else 0.0

    fun getBaseMoveSpeed(customSpeed: Double): Double {
        var baseSpeed = if (onIce) 0.258977700006 else customSpeed
        if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            val amplifier = mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).amplifier
            baseSpeed *= 1.0 + 0.2 * (amplifier + 1)
        }
        return baseSpeed
    }
    fun getJumpBoostModifier(baseJumpHeight: Double): Double {
        return getJumpBoostModifier(baseJumpHeight, true)
    }
    fun getJumpBoostModifier(baseJumpHeight: Double, potionJump: Boolean): Double {
        @Suppress("NAME_SHADOWING") var baseJumpHeight = baseJumpHeight
        if (mc.thePlayer.isPotionActive(Potion.jump) && potionJump) {
            val amplifier = mc.thePlayer.getActivePotionEffect(Potion.jump).amplifier
            baseJumpHeight += ((amplifier + 1).toFloat() * 0.1f).toDouble()
        }
        return baseJumpHeight
    }
    val isMoving
        get() = mc.thePlayer?.movementInput?.run { moveForward != 0f || moveStrafe != 0f } ?: false

    val hasMotion
        get() = mc.thePlayer?.run { motionX != .0 || motionY != .0 || motionZ != .0 } ?: false

    @JvmOverloads
    fun strafe(speed: Float = this.speed, stopWhenNoInput: Boolean = false, moveEvent: MoveEvent? = null, strength: Float = 1f) =
        mc.thePlayer?.run {
            if (!isMoving) {
                if (stopWhenNoInput) {
                    moveEvent?.zeroXZ()
                    stopXZ()
                }

                return@run
            }

            val x = -sin(direction) * (speed * strength) + (mc.thePlayer.motionX * (1 - strength))
            val z = cos(direction) * (speed * strength) + (mc.thePlayer.motionZ * (1 - strength))

            if (moveEvent != null) {
                moveEvent.x = x
                moveEvent.z = z
            }

            motionX = x
            motionZ = z
        }

    fun forward(distance: Double) =
        mc.thePlayer?.run {
            val yaw = rotationYaw.toRadiansD()
            setPosition(posX - sin(yaw) * distance, posY, posZ + cos(yaw) * distance)
        }

    val direction
        get() = mc.thePlayer?.run {
                var yaw = rotationYaw
                var forward = 1f

                if (moveForward < 0f) {
                    yaw += 180f
                    forward = -0.5f
                } else if (moveForward > 0f)
                    forward = 0.5f

                if (moveStrafing < 0f) yaw += 90f * forward
                else if (moveStrafing > 0f) yaw -= 90f * forward

                yaw.toRadiansD()
            } ?: 0.0

    fun isOnGround(height: Double) =
        mc.theWorld != null && mc.thePlayer != null &&
        mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.entityBoundingBox.offset(0.0, -height, 0.0)).isNotEmpty()

    var serverOnGround = false

    var serverX = .0
    var serverY = .0
    var serverZ = .0

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (event.isCancelled)
            return

        val packet = event.packet

        if (packet is C03PacketPlayer) {
            serverOnGround = packet.onGround

            if (packet.isMoving) {
                serverX = packet.x
                serverY = packet.y
                serverZ = packet.z
            }
        }
    }

    override fun handleEvents() = true
}
