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
object AAC431OldHyt : CriticalsMode("AAC4.3.1OldHyt") {
    override fun onAttack(entity: Entity) {
        sendPacket(0.042487, false)
        sendPacket(0.0104649713461000007, false)
        sendPacket(0.0014749900000101, false)
        sendPacket(0.00000074518164, false)
        crit(entity)
    }
}
