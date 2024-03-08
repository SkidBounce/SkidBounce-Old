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
object Mineplex : CriticalsMode("Mineplex") {
    override fun onAttack(entity: Entity) {
        sendPacket(0.0000000000000045, false)
        sendPacket(0.0, false)
        crit(entity)
    }
}
