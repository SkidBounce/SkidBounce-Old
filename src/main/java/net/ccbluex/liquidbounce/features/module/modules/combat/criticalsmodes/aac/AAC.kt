/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat.criticalsmodes.aac

import net.ccbluex.liquidbounce.features.module.modules.combat.criticalsmodes.CriticalsMode
import net.minecraft.entity.Entity

/**
 * @author SkidderMC/FDPClient
 */
object AAC : CriticalsMode("AAC") {
    override fun onAttack(entity: Entity) {
        sendPacket(0.05250000001304, false)
        sendPacket(0.00150000001304, false)
        sendPacket(0.01400000001304, false)
        sendPacket(0.00150000001304, false)
        crit(entity)
    }
}
