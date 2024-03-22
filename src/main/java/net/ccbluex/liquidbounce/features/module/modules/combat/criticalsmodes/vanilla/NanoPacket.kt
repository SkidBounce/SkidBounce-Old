/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat.criticalsmodes.vanilla

import net.ccbluex.liquidbounce.features.module.modules.combat.criticalsmodes.CriticalsMode
import net.minecraft.entity.Entity

/**
 * @author Aspw-w/NightX-Client
 */
object NanoPacket : CriticalsMode("NanoPacket") {
    override fun onAttack(entity: Entity) {
        sendPacket(0.00973333333333, true)
        sendPacket(0.001, false)
        sendPacket(-0.01200000000007, false)
        sendPacket(-0.0005, false)
        crit(entity)
    }
}
