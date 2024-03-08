/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat.criticalsmodes.vanilla

import net.ccbluex.liquidbounce.features.module.modules.combat.criticalsmodes.CriticalsMode
import net.ccbluex.liquidbounce.utils.extensions.component1
import net.ccbluex.liquidbounce.utils.extensions.component2
import net.ccbluex.liquidbounce.utils.extensions.component3
import net.minecraft.entity.Entity

/**
 * @author CCBlueX/LiquidBounce
 */
object TPHop : CriticalsMode("TPHop") {
    override fun onAttack(entity: Entity) {
        val (x, y, z) = mc.thePlayer
        sendPacket(0.02, false)
        sendPacket(0.01, false)
        mc.thePlayer.setPosition(x, y + 0.01, z)
    }
}
