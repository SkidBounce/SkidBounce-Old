package net.ccbluex.liquidbounce.features.module.modules.combat.velocitymodes.other

import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.modules.combat.velocitymodes.VelocityMode

object Phase : VelocityMode("Phase") {
    var hasVelocity = false
    override fun onEnable() {
        hasVelocity = false
    }
    override fun onUpdate() {
        if (mc.thePlayer.isInWater || mc.thePlayer.isInLava || mc.thePlayer.isInWeb || !mc.thePlayer.onGround) return
        mc.thePlayer.noClip = hasVelocity
        if (mc.thePlayer.hurtTime == 7)
            mc.thePlayer.motionY = 0.4
        hasVelocity = false
    }
    override fun onVelocityPacket(event: PacketEvent) {
        if (!mc.thePlayer.isInWater && !mc.thePlayer.isInLava && !mc.thePlayer.isInWeb && mc.thePlayer.onGround) {
            hasVelocity = true
            event.cancelEvent()
        }
    }
}