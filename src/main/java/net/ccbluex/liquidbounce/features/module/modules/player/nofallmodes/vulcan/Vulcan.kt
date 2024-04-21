/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.vulcan

import net.ccbluex.liquidbounce.event.events.PacketEvent
import net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.NoFallMode
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.extensions.round
import net.minecraft.network.play.client.C03PacketPlayer

/**
 * @author SkidderMC/FDPClient
 */
object Vulcan : NoFallMode("Vulcan") {
    private var vulCanNoFall = false
    private var vulCantNoFall = false
    private var nextSpoof = false
    private var doSpoof = false

    override fun onEnable() {
        vulCanNoFall = false
        vulCantNoFall = false
        nextSpoof = false
        doSpoof = false
    }

    override fun onUpdate() {
        if (!vulCanNoFall && mc.thePlayer.fallDistance > 3.25) vulCanNoFall = true
        if (vulCanNoFall && mc.thePlayer.onGround && vulCantNoFall) vulCantNoFall = false
        if (vulCantNoFall) return
        if (nextSpoof) {
            mc.thePlayer.motionY = -0.1
            mc.thePlayer.fallDistance = -0.1f
            MovementUtils.strafe(0.3f)
            nextSpoof = false
        }
        if (mc.thePlayer.fallDistance > 3.5625f) {
            mc.thePlayer.fallDistance = 0.0f
            doSpoof = true
            nextSpoof = true
        }
    }

    override fun onPacket(event: PacketEvent) {
        if (event.packet is C03PacketPlayer && doSpoof) {
            event.packet.onGround = true
            doSpoof = false
            event.packet.y = mc.thePlayer.posY.round(1, 2)
            mc.thePlayer.setPosition(mc.thePlayer.posX, event.packet.y, mc.thePlayer.posZ)
        }
    }
}
