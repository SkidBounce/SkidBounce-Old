package net.ccbluex.liquidbounce.features.module.modules.combat.velocitymodes.aac

import net.ccbluex.liquidbounce.event.JumpEvent
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.modules.combat.velocitymodes.VelocityMode

object AACZero : VelocityMode("AACZero") {
    var hasVelocity = false
    override fun onVelocityPacket(event: PacketEvent) {
        if (mc.thePlayer.isInWater || mc.thePlayer.isInLava || mc.thePlayer.isInWeb) return
        hasVelocity = true
    }
    override fun onUpdate() {
        if (mc.thePlayer.isInWater || mc.thePlayer.isInLava || mc.thePlayer.isInWeb) return
        if (mc.thePlayer.hurtTime > 0) {
            if (!hasVelocity || mc.thePlayer.onGround || mc.thePlayer.fallDistance > 2f) return
            mc.thePlayer.motionY -= 1.0
            mc.thePlayer.isAirBorne = true
            mc.thePlayer.onGround = true
        } else hasVelocity = false
    }
    override fun onJump(event: JumpEvent) { if (mc.thePlayer.hurtTime > 0) event.cancelEvent() }
}
