/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat.criticalsmodes.ncp

import net.ccbluex.liquidbounce.features.module.modules.combat.Criticals.sendPacket
import net.ccbluex.liquidbounce.features.module.modules.combat.criticalsmodes.CriticalsMode
import net.minecraft.entity.Entity

/**
 * @author CCBlueX/LiquidBounce
 */
object NCPPacket : CriticalsMode("NCPPacket") {
    override fun onAttack(entity: Entity) {
        sendPacket(y = 0.11)
        sendPacket(y = 0.1100013579)
        sendPacket(y = 0.0000013579)
        mc.thePlayer.onCriticalHit(entity)
    }
}
