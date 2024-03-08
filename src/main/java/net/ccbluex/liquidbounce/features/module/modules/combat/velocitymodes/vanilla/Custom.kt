/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat.velocitymodes.vanilla

import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.modules.combat.Velocity
import net.ccbluex.liquidbounce.features.module.modules.combat.velocitymodes.VelocityMode
import net.ccbluex.liquidbounce.utils.*
import net.ccbluex.liquidbounce.utils.extensions.getDistanceToEntityBox
import net.ccbluex.liquidbounce.utils.extensions.jump
import net.ccbluex.liquidbounce.utils.misc.RandomUtils
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
        if (RandomUtils.nextInt(1, 100) <= Velocity.chance && packet is S12PacketEntityVelocity) {

            packet.motionX = (packet.getMotionX() * Velocity.horizontalMultiplier).toInt()
            packet.motionY = (packet.getMotionY() * Velocity.verticalMultiplier).toInt()
            packet.motionZ = (packet.getMotionZ() * Velocity.horizontalMultiplier).toInt()

            event.cancelEvent()

            if (!Velocity.cancelVertical)
                mc.thePlayer.motionY = packet.realMotionY
            if (!Velocity.cancelHorizontal) {
                mc.thePlayer.motionX = packet.realMotionX
                mc.thePlayer.motionZ = packet.realMotionZ
            }
        }
    }

    override fun onUpdate() {
        if (Velocity.jump && mc.thePlayer.hurtTime == 9 && RandomUtils.nextInt(1, 100) > Velocity.jumpFailRate)
            mc.thePlayer.jump(Velocity.jumpMotion)
        if (Velocity.reverse && !(Velocity.reverseNoGround && mc.thePlayer.onGround)) {
            run {
                val nearbyEntity = getNearestEntityInRange() ?: return@run

                if (Velocity.onLook && !EntityUtils.isLookingOnEntities(
                        nearbyEntity,
                        Velocity.maxAngleDifference.toDouble()
                    )
                ) {
                    if (Velocity.reverseSmooth)
                        mc.thePlayer.speedInAir = 0.02f
                    return@run
                }

                if (Velocity.reverseTicks > Velocity.velocityTick)
                    if (Velocity.reverseSmooth)
                        mc.thePlayer.speedInAir = Velocity.reverseStrength
                    else
                        MovementUtils.speed *= Velocity.reverseStrength
                else if (Velocity.reverseSmooth)
                    mc.thePlayer.speedInAir = 0.02f
            }
        }
        if (Velocity.tickreduce && Velocity.tickreduceTicks == Velocity.velocityTick) {
            if (Velocity.tickreduceVertical)
                mc.thePlayer.motionY *= Velocity.tickreduceMultiplier
            if (Velocity.tickreduceHorizontal) {
                mc.thePlayer.motionX *= Velocity.tickreduceMultiplier
                mc.thePlayer.motionZ *= Velocity.tickreduceMultiplier
            }
        }
    }

    override fun onAttack() {
        if (mc.thePlayer.hurtTime >= 3 && Velocity.attackReduce) {
            mc.thePlayer.motionX *= Velocity.attackReduceMultiplier
            mc.thePlayer.motionZ *= Velocity.attackReduceMultiplier
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
                (distance <= Velocity.range)
            }

        return entitiesInRange.minByOrNull { mc.thePlayer.getDistanceToEntityBox(it) }
    }
}