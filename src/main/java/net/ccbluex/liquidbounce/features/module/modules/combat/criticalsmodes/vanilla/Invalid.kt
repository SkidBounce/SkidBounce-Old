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
object Invalid : CriticalsMode("Invalid") {
    override fun onAttack(entity: Entity) {
        sendPacket(1E+27, false)
        sendPacket(-1E+68, false)
        sendPacket(1E+41, false)
        crit(entity)
    }
}
