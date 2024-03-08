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
object TakaAC : CriticalsMode("TakaAC") {
    override fun onAttack(entity: Entity) {
        sendPacket(0.33319999363422365, false)
        sendPacket(0.24813599859094576, false)
        sendPacket(0.16477328182606651, false)
        sendPacket(0.08307781780646721, false)
        crit(entity)
    }
}
