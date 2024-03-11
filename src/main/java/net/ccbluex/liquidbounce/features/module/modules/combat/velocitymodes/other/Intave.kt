/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat.velocitymodes.other

import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.modules.combat.velocitymodes.VelocityMode
import net.ccbluex.liquidbounce.utils.extensions.jmp

/**
 * @author EclipsesDev
 * @author CCBlueX/LiquidBounce
 */
object Intave : VelocityMode("Intave") {
    private var tick = 0
    private var velocity = false
    override fun onUpdate() {
        ++tick
        if (velocity && mc.thePlayer.hurtTime == 2) {
            if (mc.thePlayer.onGround && tick % 2 == 0) {
                mc.thePlayer.jmp()
                tick = 0
            }
            velocity = false
        }
    }

    override fun onVelocityPacket(event: PacketEvent) { velocity = true }
}
