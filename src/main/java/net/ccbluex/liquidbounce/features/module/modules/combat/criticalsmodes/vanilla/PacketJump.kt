/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat.criticalsmodes.vanilla

import net.ccbluex.liquidbounce.features.module.modules.combat.criticalsmodes.CriticalsMode
import net.minecraft.entity.Entity

/**
 * @author SkidderMC/FDPClient
 */
object PacketJump : CriticalsMode("PacketJump") {
    override fun onAttack(entity: Entity) {
        sendPacket(0.41999998688698, false)
        sendPacket(0.7531999805212, false)
        sendPacket(1.00133597911214, false)
        sendPacket(1.16610926093821, false)
        sendPacket(1.24918707874468, false)
        sendPacket(1.1707870772188, false)
        sendPacket(1.0155550727022, false)
        sendPacket(0.78502770378924, false)
        sendPacket(0.4807108763317, false)
        sendPacket(0.10408037809304, false)
        sendPacket(0.0, true)
        crit(entity)
    }
}
