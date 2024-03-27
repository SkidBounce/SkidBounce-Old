/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat.criticalsmodes.other

import net.ccbluex.liquidbounce.features.module.modules.combat.criticalsmodes.CriticalsMode
import net.minecraft.entity.Entity

/**
 * @author SkidderMC/FDPClient
 */
object TestMinemora : CriticalsMode("TestMinemora") {
    override fun onAttack(entity: Entity) {
        sendPacket(0.0114514, false)
        sendPacket(0.0010999999940395355, false)
        sendPacket(0.00150000001304, false)
        sendPacket(0.0012016413, false)
        crit(entity)
    }
}
