/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat.criticalsmodes.blocksmc

import net.ccbluex.liquidbounce.features.module.modules.combat.Criticals
import net.ccbluex.liquidbounce.features.module.modules.combat.criticalsmodes.CriticalsMode
import net.minecraft.entity.Entity

/**
 * @author SkidderMC/FDPClient
 */
object BlocksMC3 : CriticalsMode("BlocksMC3") {
    override fun onAttack(entity: Entity) {
        Criticals.sendPacket(0.0825080378093, true)
        Criticals.sendPacket(0.0215634532004, false)
        Criticals.sendPacket(0.1040220332227, false)
        Criticals.crit(entity)
    }
}