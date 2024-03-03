/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat.criticalsmodes.aac

import net.ccbluex.liquidbounce.features.module.modules.combat.Criticals.crit
import net.ccbluex.liquidbounce.features.module.modules.combat.Criticals.sendPacket
import net.ccbluex.liquidbounce.features.module.modules.combat.criticalsmodes.CriticalsMode
import net.minecraft.entity.Entity

/**
 * @author SkidderMC/FDPClient
 */
object AAC5 : CriticalsMode("AAC5") {
    override fun onAttack(entity: Entity) {
        sendPacket(0.0625, false)
        sendPacket(0.0433, false)
        sendPacket(0.2088, false)
        sendPacket(0.9963, false)
        crit(entity)
    }
}
