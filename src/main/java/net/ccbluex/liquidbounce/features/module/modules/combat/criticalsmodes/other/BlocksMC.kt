/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat.criticalsmodes.other

import net.ccbluex.liquidbounce.features.module.modules.combat.criticalsmodes.CriticalsMode
import net.ccbluex.liquidbounce.utils.PacketUtils.sendPackets
import net.ccbluex.liquidbounce.utils.extensions.component1
import net.ccbluex.liquidbounce.utils.extensions.component2
import net.ccbluex.liquidbounce.utils.extensions.component3
import net.minecraft.entity.Entity
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition

object BlocksMC : CriticalsMode("BlocksMC") {
    override fun onAttack(entity: Entity) {
        val (x, y, z) = mc.thePlayer
        sendPackets(
            C04PacketPlayerPosition(x, y + 0.001091981, z, true),
            C04PacketPlayerPosition(x, y, z, false)
        )
        mc.thePlayer.onCriticalHit(entity)
    }
}
