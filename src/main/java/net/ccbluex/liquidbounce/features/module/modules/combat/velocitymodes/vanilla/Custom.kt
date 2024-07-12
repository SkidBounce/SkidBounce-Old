/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat.velocitymodes.vanilla

import net.ccbluex.liquidbounce.event.events.AttackEvent
import net.ccbluex.liquidbounce.event.events.PacketEvent
import net.ccbluex.liquidbounce.features.module.modules.combat.Velocity.attackReduce
import net.ccbluex.liquidbounce.features.module.modules.combat.Velocity.attackReduceLegit
import net.ccbluex.liquidbounce.features.module.modules.combat.Velocity.attackReduceMaxHurtTime
import net.ccbluex.liquidbounce.features.module.modules.combat.Velocity.attackReduceMinHurtTime
import net.ccbluex.liquidbounce.features.module.modules.combat.Velocity.attackReduceMultiplier
import net.ccbluex.liquidbounce.features.module.modules.combat.Velocity.cancelHorizontal
import net.ccbluex.liquidbounce.features.module.modules.combat.Velocity.cancelVertical
import net.ccbluex.liquidbounce.features.module.modules.combat.Velocity.chance
import net.ccbluex.liquidbounce.features.module.modules.combat.Velocity.horizontalMultiplier
import net.ccbluex.liquidbounce.features.module.modules.combat.Velocity.jump
import net.ccbluex.liquidbounce.features.module.modules.combat.Velocity.jumpFailRate
import net.ccbluex.liquidbounce.features.module.modules.combat.Velocity.jumpMotion
import net.ccbluex.liquidbounce.features.module.modules.combat.Velocity.maxAngleDifference
import net.ccbluex.liquidbounce.features.module.modules.combat.Velocity.modify
import net.ccbluex.liquidbounce.features.module.modules.combat.Velocity.modifyAddedMotion
import net.ccbluex.liquidbounce.features.module.modules.combat.Velocity.onLook
import net.ccbluex.liquidbounce.features.module.modules.combat.Velocity.overrideDirection
import net.ccbluex.liquidbounce.features.module.modules.combat.Velocity.overrideDirectionRotation
import net.ccbluex.liquidbounce.features.module.modules.combat.Velocity.range
import net.ccbluex.liquidbounce.features.module.modules.combat.Velocity.reverse
import net.ccbluex.liquidbounce.features.module.modules.combat.Velocity.reverseNoGround
import net.ccbluex.liquidbounce.features.module.modules.combat.Velocity.reverseSmooth
import net.ccbluex.liquidbounce.features.module.modules.combat.Velocity.reverseStrength
import net.ccbluex.liquidbounce.features.module.modules.combat.Velocity.reverseTicks
import net.ccbluex.liquidbounce.features.module.modules.combat.Velocity.tickreduce
import net.ccbluex.liquidbounce.features.module.modules.combat.Velocity.tickreduceHorizontal
import net.ccbluex.liquidbounce.features.module.modules.combat.Velocity.tickreduceMultiplier
import net.ccbluex.liquidbounce.features.module.modules.combat.Velocity.tickreduceTicks
import net.ccbluex.liquidbounce.features.module.modules.combat.Velocity.tickreduceVertical
import net.ccbluex.liquidbounce.features.module.modules.combat.Velocity.velocityTick
import net.ccbluex.liquidbounce.features.module.modules.combat.Velocity.verticalMultiplier
import net.ccbluex.liquidbounce.features.module.modules.combat.velocitymodes.VelocityMode
import net.ccbluex.liquidbounce.utils.EntityUtils
import net.ccbluex.liquidbounce.utils.EntityUtils.isLookingOnEntities
import net.ccbluex.liquidbounce.utils.MovementUtils.speed
import net.ccbluex.liquidbounce.utils.RotationUtils.currentRotation
import net.ccbluex.liquidbounce.utils.extensions.*
import net.ccbluex.liquidbounce.utils.misc.RandomUtils.chanceOf
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.Entity
import net.minecraft.network.play.server.S12PacketEntityVelocity
import net.minecraft.network.play.server.S27PacketExplosion
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * @author CCBlueX/LiquidBounce
 * @author ManInMyVan/SkidBounce
 * @author ManInMyVan
 * @author SkidderMC/FDPClient
 */
object Custom : VelocityMode("Custom") {
    override fun onVelocityPacket(event: PacketEvent) {
        if (!modify) return

        val packet = event.packet

        chanceOf(chance / 100.0) {
            if (packet is S12PacketEntityVelocity) {
                event.cancelEvent()

                var x = packet.realMotionX
                var y = packet.realMotionY
                var z = packet.realMotionZ

                if (overrideDirection) {
                    val yaw = when (overrideDirectionRotation) {
                        "Server" -> currentRotation?.yaw ?: mc.thePlayer.rotationYaw
                        else -> mc.thePlayer.rotationYaw
                    }.plus(90).toRadiansD()

                    val dist = sqrt(x * x + z * z)

                    x = cos(yaw) * dist
                    z = sin(yaw) * dist
                }

                if (modifyAddedMotion) {
                    x -= mc.thePlayer.motionX
                    y -= mc.thePlayer.motionY
                    z -= mc.thePlayer.motionZ
                }

                x *= horizontalMultiplier
                y *= verticalMultiplier
                z *= horizontalMultiplier

                if (modifyAddedMotion) {
                    x += mc.thePlayer.motionX
                    y += mc.thePlayer.motionY
                    z += mc.thePlayer.motionZ
                } else {
                    if (cancelVertical) {
                        y = mc.thePlayer.motionY
                    }
                    if (cancelHorizontal) {
                        x = mc.thePlayer.motionX
                        z = mc.thePlayer.motionZ
                    }
                }

                mc.thePlayer.setVelocity(x, y, z)
            }

            if (packet is S27PacketExplosion) {
                if (modifyAddedMotion) {
                    packet.field_149152_f *= horizontalMultiplier
                    packet.field_149153_g *= verticalMultiplier
                    packet.field_149159_h *= horizontalMultiplier
                } else {
                    packet.field_149152_f
                    packet.field_149153_g
                    packet.field_149159_h

                    if (!cancelVertical) {
                        mc.thePlayer.motionY = (packet.field_149153_g + mc.thePlayer.motionY) * verticalMultiplier
                    }

                    if (!cancelHorizontal) {
                        mc.thePlayer.motionX = (packet.field_149152_f + mc.thePlayer.motionX) * horizontalMultiplier
                        mc.thePlayer.motionZ = (packet.field_149159_h + mc.thePlayer.motionZ) * horizontalMultiplier
                    }

                    packet.field_149152_f = 0f
                    packet.field_149153_g = 0f
                    packet.field_149159_h = 0f
                }
            } else return@chanceOf
        }
    }

    override fun onUpdate() {
        if (jump && mc.thePlayer.hurtTime == 9 && chanceOf(1f - jumpFailRate / 100f)) {
            mc.thePlayer.jmp(jumpMotion)
        }

        if (reverse && !(reverseNoGround && mc.thePlayer.onGround)) {
            run {
                val nearbyEntity = getNearestEntityInRange() ?: return@run

                if (onLook && !isLookingOnEntities(
                        nearbyEntity,
                        maxAngleDifference
                    )
                ) {
                    if (reverseSmooth)
                        mc.thePlayer.speedInAir = 0.02f
                    return@run
                }

                if (reverseTicks > velocityTick)
                    if (reverseSmooth)
                        mc.thePlayer.speedInAir = reverseStrength
                    else
                        speed *= reverseStrength
                else if (reverseSmooth)
                    mc.thePlayer.speedInAir = 0.02f
            }
        }

        if (tickreduce && tickreduceTicks == velocityTick) {
            if (tickreduceVertical)
                mc.thePlayer.motionY *= tickreduceMultiplier
            if (tickreduceHorizontal) {
                mc.thePlayer.motionX *= tickreduceMultiplier
                mc.thePlayer.motionZ *= tickreduceMultiplier
            }
        }
    }

    override fun onAttack(event: AttackEvent) {
        if (attackReduce && !attackReduceLegit) {
            if (mc.thePlayer.hurtTime in attackReduceMinHurtTime..attackReduceMaxHurtTime) {
                mc.thePlayer.motionX *= attackReduceMultiplier
                mc.thePlayer.motionZ *= attackReduceMultiplier
            }
        }
    }

    private fun getAllEntities(): List<Entity> {
        return mc.theWorld.loadedEntityList
            .filter { EntityUtils.isSelected(it, true) }
            .toList()
    }

    private fun getNearestEntityInRange(): Entity? {
        val entitiesInRange = getAllEntities()
            .filter {
                val distance = mc.thePlayer.getDistanceToEntityBox(it)
                (distance <= range)
            }

        return entitiesInRange.minByOrNull { mc.thePlayer.getDistanceToEntityBox(it) }
    }
}
