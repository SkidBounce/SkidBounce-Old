/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.render

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.events.PacketEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory.RENDER
import net.minecraft.network.play.server.S3FPacketCustomPayload

object NoBooks : Module("NoBooks", RENDER, gameDetecting = false) {
    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (packet is S3FPacketCustomPayload && packet.channelName == "MC|BOpen") event.cancelEvent()
    }
}
