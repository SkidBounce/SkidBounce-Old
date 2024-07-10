/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat.velocitymodes.vanilla

import net.ccbluex.liquidbounce.event.events.PacketEvent
import net.ccbluex.liquidbounce.features.module.modules.combat.Velocity.attackReduce
import net.ccbluex.liquidbounce.features.module.modules.combat.Velocity.attackReduceMultiplier
import net.ccbluex.liquidbounce.features.module.modules.combat.Velocity.cancelHorizontal
import net.ccbluex.liquidbounce.features.module.modules.combat.Velocity.cancelVertical
import net.ccbluex.liquidbounce.features.module.modules.combat.Velocity.chance
import net.ccbluex.liquidbounce.features.module.modules.combat.Velocity.horizontalMultiplier
import net.ccbluex.liquidbounce.features.module.modules.combat.Velocity.jump
import net.ccbluex.liquidbounce.features.module.modules.combat.Velocity.jumpFailRate
import net.ccbluex.liquidbounce.features.module.modules.combat.Velocity.jumpMotion
import net.ccbluex.liquidbounce.features.module.modules.combat.Velocity.maxAngleDifference
import net.ccbluex.liquidbounce.features.module.modules.combat.Velocity.multiplyAddedMotion
import net.ccbluex.liquidbounce.features.module.modules.combat.Velocity.onLook
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
import net.ccbluex.liquidbounce.utils.*
import net.ccbluex.liquidbounce.utils.EntityUtils.isLookingOnEntities
import net.ccbluex.liquidbounce.utils.MovementUtils.speed
import net.ccbluex.liquidbounce.utils.extensions.*
import net.ccbluex.liquidbounce.utils.misc.RandomUtils.chanceOf
import net.minecraft.entity.Entity
import net.minecraft.network.play.server.S12PacketEntityVelocity
import net.minecraft.network.play.server.S27PacketExplosion

/**
 * @author CCBlueX/LiquidBounce
 * @author ManInMyVan/SkidBounce
 * @author ManInMyVan
 * @author SkidderMC/FDPClient
 */
object Custom : VelocityMode("Custom") {
    override fun onVelocityPacket(event: PacketEvent) {
        val packet = event.packet

        chanceOf(chance / 100.0) {
            if (packet is S12PacketEntityVelocity) {
                event.cancelEvent()
                if (multiplyAddedMotion) mc.thePlayer.setVelocity(
                    mc.thePlayer.motionX + (packet.realMotionX - mc.thePlayer.motionX) * horizontalMultiplier,
                    mc.thePlayer.motionY + (packet.realMotionY - mc.thePlayer.motionY) * verticalMultiplier,
                    mc.thePlayer.motionZ + (packet.realMotionZ - mc.thePlayer.motionZ) * horizontalMultiplier
                ) else mc.thePlayer.setVelocity(
                    if (cancelHorizontal) mc.thePlayer.motionX else packet.realMotionX * horizontalMultiplier,
                    if (cancelVertical) mc.thePlayer.motionY else packet.realMotionY * verticalMultiplier,
                    if (cancelHorizontal) mc.thePlayer.motionZ else packet.realMotionZ * horizontalMultiplier
                )
            }

            if (packet is S27PacketExplosion) {
                if (multiplyAddedMotion) {
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

    override fun onAttack() {
        if (mc.thePlayer.hurtTime >= 3 && attackReduce) {
            mc.thePlayer.motionX *= attackReduceMultiplier
            mc.thePlayer.motionZ *= attackReduceMultiplier
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
