/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 *  https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat.criticalsmodes.vanilla

import net.ccbluex.liquidbounce.features.module.modules.combat.Criticals.sendPacket
import net.ccbluex.liquidbounce.features.module.modules.combat.criticalsmodes.CriticalsMode
import net.ccbluex.liquidbounce.utils.extensions.*
import net.minecraft.entity.Entity

/**
 * @author CCBlueX/LiquidBounce
 */
object TPHop : CriticalsMode("TPHop") {
    override fun onAttack(entity: Entity) {
        val (x, y, z) = mc.thePlayer
        sendPacket(y = 0.02)
        sendPacket(y = 0.01)
        mc.thePlayer.setPosition(x, y + 0.01, z)
    }
}