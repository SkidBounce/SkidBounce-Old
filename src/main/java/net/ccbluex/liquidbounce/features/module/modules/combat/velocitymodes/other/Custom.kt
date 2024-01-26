package net.ccbluex.liquidbounce.features.module.modules.combat.velocitymodes.other

import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.modules.combat.Velocity.attackReduce
import net.ccbluex.liquidbounce.features.module.modules.combat.Velocity.attackReduceMultiplier
import net.ccbluex.liquidbounce.features.module.modules.combat.Velocity.cancelHorizontal
import net.ccbluex.liquidbounce.features.module.modules.combat.Velocity.cancelVertical
import net.ccbluex.liquidbounce.features.module.modules.combat.Velocity.chance
import net.ccbluex.liquidbounce.features.module.modules.combat.Velocity.horizontalMultiplier
import net.ccbluex.liquidbounce.features.module.modules.combat.Velocity.jump
import net.ccbluex.liquidbounce.features.module.modules.combat.Velocity.jumpFailRate
import net.ccbluex.liquidbounce.features.module.modules.combat.Velocity.jumpMotion
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
import net.ccbluex.liquidbounce.utils.MovementUtils.speed
import net.ccbluex.liquidbounce.utils.extensions.jump
import net.minecraft.network.play.server.S12PacketEntityVelocity
import net.ccbluex.liquidbounce.utils.misc.RandomUtils

object Custom : VelocityMode("Custom") {
    override fun onVelocityPacket(event: PacketEvent) {
        val packet = event.packet
        if (packet !is S12PacketEntityVelocity) return
        if (RandomUtils.nextInt(1, 100) <= chance) {

            packet.motionX = (packet.getMotionX() * horizontalMultiplier).toInt()
            packet.motionY = (packet.getMotionY() * verticalMultiplier).toInt()
            packet.motionZ = (packet.getMotionZ() * horizontalMultiplier).toInt()

            event.cancelEvent()

            if (!cancelVertical) mc.thePlayer.motionY = packet.getMotionY().toDouble() / 8000.0
            if (!cancelHorizontal) {
                mc.thePlayer.motionX = packet.getMotionX().toDouble() / 8000.0
                mc.thePlayer.motionZ = packet.getMotionZ().toDouble() / 8000.0
            }
        }
    }

    override fun onUpdate() {
        if (jump && mc.thePlayer.hurtTime == 9 && RandomUtils.nextInt(1, 100) > jumpFailRate) {
            mc.thePlayer.jump(jumpMotion)
        }
        if (reverse && !(reverseNoGround && mc.thePlayer.onGround)) {
            if (reverseTicks > velocityTick) {
                if (reverseSmooth) mc.thePlayer.speedInAir = reverseStrength
                else speed *= reverseStrength
            } else if (reverseSmooth) mc.thePlayer.speedInAir = 0.02f
        }
        if (tickreduce && tickreduceTicks == velocityTick) {
            if (tickreduceVertical) mc.thePlayer.motionY *= tickreduceMultiplier
            if (tickreduceHorizontal) {
                mc.thePlayer.motionX *= tickreduceMultiplier
                mc.thePlayer.motionZ *= tickreduceMultiplier
            }
        }
    }

    override fun onAttack() {
        if (mc.thePlayer.hurtTime < 3 || !attackReduce) return
        mc.thePlayer.motionX *= attackReduceMultiplier
        mc.thePlayer.motionZ *= attackReduceMultiplier
    }
}
