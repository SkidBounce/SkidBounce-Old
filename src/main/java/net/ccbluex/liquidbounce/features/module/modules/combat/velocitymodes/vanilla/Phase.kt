/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat.velocitymodes.vanilla

import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.modules.combat.velocitymodes.VelocityMode

/**
 * @author CCBlueX/LiquidBounce
 */
object Phase : VelocityMode("Phase") {
    private var hasVelocity = false
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