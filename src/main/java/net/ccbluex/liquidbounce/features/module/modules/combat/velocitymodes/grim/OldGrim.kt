package net.ccbluex.liquidbounce.features.module.modules.combat.velocitymodes.grim

import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.modules.combat.velocitymodes.VelocityMode
import net.minecraft.network.play.server.S12PacketEntityVelocity
import net.minecraft.network.play.server.S32PacketConfirmTransaction

object OldGrim : VelocityMode("OldGrim") {
    private var cancelPacket = 6
    private var resetPersec = 8
    private var grimTCancel = 0
    private var updates = 0

    override fun onEnable() {
        grimTCancel = 0
    }

    override fun onVelocityPacket(event: PacketEvent) {
        event.cancelEvent()
        grimTCancel = cancelPacket
    }

    override fun onPacket(event: PacketEvent) {
        if (event.packet is S32PacketConfirmTransaction && grimTCancel > 0) {
            event.cancelEvent()
            grimTCancel--
        }
    }

    override fun onUpdate() {
        updates++

        if (resetPersec > 0) {
            if (updates >= 0 || updates >= resetPersec) {
                updates = 0
                if (grimTCancel > 0){
                    grimTCancel--
                }
            }
        }
    }
}