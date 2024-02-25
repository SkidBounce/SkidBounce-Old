/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat.criticalsmodes.ncp

import net.ccbluex.liquidbounce.features.module.modules.combat.Criticals.ncplatestAttacks
import net.ccbluex.liquidbounce.features.module.modules.combat.Criticals.sendPacket
import net.ccbluex.liquidbounce.features.module.modules.combat.criticalsmodes.CriticalsMode
import net.minecraft.entity.Entity

/**
 * @author SkidderMC/FDPClient
 * @author Koitoyuu
 */
object NCPLatest : CriticalsMode("NCPLatest") {
    private var attacked = 0
    override fun onAttack(entity: Entity) {
        ++attacked
        if (attacked >= ncplatestAttacks) {
            sendPacket(y = 0.00001058293536)
            sendPacket(y = 0.00000916580235)
            sendPacket(y = 0.00000010371854)
            attacked = 0
        }
    }
}
