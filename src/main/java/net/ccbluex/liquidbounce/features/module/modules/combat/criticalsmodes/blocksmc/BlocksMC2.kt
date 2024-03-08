/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat.criticalsmodes.blocksmc

import net.ccbluex.liquidbounce.features.module.modules.combat.criticalsmodes.CriticalsMode
import net.minecraft.entity.Entity

/**
 * @author CCBlueX/LiquidBounce
 */
object BlocksMC2 : CriticalsMode("BlocksMC2") {
    override fun onAttack(entity: Entity) {
        sendPacket(0.001091981, true)
        sendPacket(0.000114514, false)
        sendPacket(0.0, false)
        crit(entity)
    }
}