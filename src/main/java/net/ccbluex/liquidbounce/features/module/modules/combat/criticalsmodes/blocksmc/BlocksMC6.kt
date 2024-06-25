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
 * @author EclipsesDev
 */
object BlocksMC6 : CriticalsMode("BlocksMC5") {
    override fun onAttack(entity: Entity) {
        sendPacket(0.002, true)
        sendPacket(-0.000001, false)
        sendPacket(0.0, false)
        crit(entity)
    }
}
