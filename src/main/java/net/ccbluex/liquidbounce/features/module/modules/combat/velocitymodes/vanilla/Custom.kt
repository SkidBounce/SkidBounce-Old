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

/**
 * @author CCBlueX/LiquidBounce
 * @author ManInMyVan/SkidBounce
 * @author ManInMyVan
 * @author SkidderMC/FDPClient
 */
object Custom : VelocityMode("Custom") {
    override fun onVelocityPacket(event: PacketEvent) { // TODO: explosions
        val packet = event.packet
        chanceOf(chance / 100.0) {
            if (packet !is S12PacketEntityVelocity)
                return@chanceOf

            packet.motionX = (packet.motionX * horizontalMultiplier).toInt()
            packet.motionY = (packet.motionY * verticalMultiplier).toInt()
            packet.motionZ = (packet.motionZ * horizontalMultiplier).toInt()

            event.cancelEvent()

            if (!cancelVertical)
                mc.thePlayer.motionY = packet.realMotionY
            if (!cancelHorizontal) {
                mc.thePlayer.motionX = packet.realMotionX
                mc.thePlayer.motionZ = packet.realMotionZ
            }
        }
    }

    override fun onUpdate() {
        if (jump && mc.thePlayer.hurtTime == 9 && chanceOf(1f - jumpFailRate / 100f))
            mc.thePlayer.jump(jumpMotion)
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
