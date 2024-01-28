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

object TPHop : CriticalsMode("TPHop") {
    override fun onAttack(entity: Entity) {
        val (x, y, z) = mc.thePlayer
        sendPackets(
            C04PacketPlayerPosition(x, y + 0.02, z, false),
            C04PacketPlayerPosition(x, y + 0.01, z, false)
        )
        mc.thePlayer.setPosition(x, y + 0.01, z)
    }
}
