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
object AAC504 : CriticalsMode("AAC5.0.4") {
    override fun onAttack(entity: Entity) {
        sendPacket(0.00133545, false)
        sendPacket(-0.000000433, false)
        crit(entity)
    }
}
