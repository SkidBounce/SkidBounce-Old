/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat.criticalsmodes.aac

import net.ccbluex.liquidbounce.features.module.modules.combat.criticalsmodes.CriticalsMode
import net.ccbluex.liquidbounce.utils.extensions.stopXZ
import net.minecraft.entity.Entity

/**
 * @author Aspw-w/NightX-Client
 */
object AAC4 : CriticalsMode("AAC4") {
    override fun onAttack(entity: Entity) {
        mc.thePlayer.stopXZ()
        sendPacket(3e-14, true)
        sendPacket(8e-15, true)
        crit(entity)
    }
}
