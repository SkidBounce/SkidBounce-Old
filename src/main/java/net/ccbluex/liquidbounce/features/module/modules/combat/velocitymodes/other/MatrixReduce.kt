/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat.velocitymodes.other

import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.modules.combat.velocitymodes.VelocityMode
import net.minecraft.network.play.server.S12PacketEntityVelocity

/**
 * @author EclipsesDev
 * @author CCBlueX/LiquidBounce
 */
object MatrixReduce : VelocityMode("MatrixReduce") {
    override fun onVelocityPacket(event: PacketEvent) {
        val packet = event.packet
        if (packet is S12PacketEntityVelocity) {
            packet.motionX = (packet.getMotionX() * 0.33).toInt()
            packet.motionZ = (packet.getMotionZ() * 0.33).toInt()

            if (mc.thePlayer.onGround) {
                packet.motionX = (packet.getMotionX() * 0.86).toInt()
                packet.motionZ = (packet.getMotionZ() * 0.86).toInt()
            }
        }
    }
}
