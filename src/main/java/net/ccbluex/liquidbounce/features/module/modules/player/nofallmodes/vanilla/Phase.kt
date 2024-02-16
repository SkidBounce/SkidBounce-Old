/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.vanilla

import net.ccbluex.liquidbounce.features.module.modules.player.NoFall.phaseOffset
import net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.NoFallMode
import net.ccbluex.liquidbounce.utils.PacketUtils.sendPacket
import net.ccbluex.liquidbounce.utils.misc.FallingPlayer
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition
import java.util.*
import kotlin.concurrent.schedule

/**
 * @author SkidderMC/FDPClient
 */
object Phase : NoFallMode("Phase") {
    override fun onUpdate() {
        if (mc.thePlayer.fallDistance > 3 + phaseOffset) {
            val pos = FallingPlayer(mc.thePlayer).findCollision(5)?.pos ?: return
            if (pos.y - mc.thePlayer.motionY / 20.0 < mc.thePlayer.posY) {
                mc.timer.timerSpeed = 0.05f
                Timer().schedule(100L) {
                    sendPacket(C04PacketPlayerPosition(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(), true))
                    mc.timer.timerSpeed = 1f
                }
            }
        }
    }
}
