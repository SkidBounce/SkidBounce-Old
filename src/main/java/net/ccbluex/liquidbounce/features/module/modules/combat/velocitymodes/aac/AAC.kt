/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat.velocitymodes.aac

import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.modules.combat.Velocity.aacHorizontal
import net.ccbluex.liquidbounce.features.module.modules.combat.Velocity.aacVertical
import net.ccbluex.liquidbounce.features.module.modules.combat.Velocity.velocityTimer
import net.ccbluex.liquidbounce.features.module.modules.combat.velocitymodes.VelocityMode

object AAC : VelocityMode("AAC") {
    private var hasVelocity = false
    override fun onEnable() {
        hasVelocity = false
    }
    override fun onVelocityPacket(event: PacketEvent) {
        if (!mc.thePlayer.isInWater && !mc.thePlayer.isInLava && !mc.thePlayer.isInWeb)
            hasVelocity = true
    }
    override fun onUpdate() {
        if (hasVelocity && velocityTimer.hasTimePassed(80) && !mc.thePlayer.isInWater && !mc.thePlayer.isInLava && !mc.thePlayer.isInWeb) {
            mc.thePlayer.motionX *= aacHorizontal
            mc.thePlayer.motionZ *= aacHorizontal
            mc.thePlayer.motionY *= aacVertical
            hasVelocity = false
        }
    }
}
