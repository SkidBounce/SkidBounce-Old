/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.noslowmodes.other

import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.noslowmodes.NoSlowMode
import net.ccbluex.liquidbounce.utils.PacketUtils.sendPacket
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraft.util.BlockPos
import net.minecraft.util.BlockPos.ORIGIN

object Horizon : NoSlowMode("Horizon") {
    override fun onPacket(event: PacketEvent) {
        if (event.packet is C08PacketPlayerBlockPlacement) {
            if (event.packet.placedBlockDirection == 255 && event.packet.position == BlockPos(-1,-1,-1)) {
                event.cancelEvent()
                sendPacket(C08PacketPlayerBlockPlacement(ORIGIN, 255, mc.thePlayer.heldItem, 0f, 0f, 0f))
            }
        }
    }
}
