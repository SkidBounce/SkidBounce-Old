package net.ccbluex.liquidbounce.features.module.modules.combat.velocitymodes.other

import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.modules.combat.velocitymodes.VelocityMode

object Vanilla : VelocityMode("Vanilla") {
    override fun onVelocityPacket(event: PacketEvent) {
        event.cancelEvent()
    }
}