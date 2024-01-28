/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat.criticalsmodes.other

import net.ccbluex.liquidbounce.features.module.modules.combat.criticalsmodes.CriticalsMode
import net.ccbluex.liquidbounce.utils.PacketUtils
import net.ccbluex.liquidbounce.utils.extensions.component1
import net.ccbluex.liquidbounce.utils.extensions.component2
import net.ccbluex.liquidbounce.utils.extensions.component3
import net.minecraft.entity.Entity
import net.minecraft.network.play.client.C03PacketPlayer

object NCPPacket : CriticalsMode("NCPPacket") {
    override fun onAttack(entity: Entity) {
        val (x, y, z) = mc.thePlayer
        PacketUtils.sendPackets(
            C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.11, z, false),
            C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.1100013579, z, false),
            C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.0000013579, z, false)
        )
        mc.thePlayer.onCriticalHit(entity)
    }
}
