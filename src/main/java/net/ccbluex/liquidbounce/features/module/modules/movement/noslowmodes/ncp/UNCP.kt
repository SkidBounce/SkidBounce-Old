/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.noslowmodes.ncp

import net.ccbluex.liquidbounce.event.EventState.PRE
import net.ccbluex.liquidbounce.event.events.MotionEvent
import net.ccbluex.liquidbounce.event.events.PacketEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.NoSlow
import net.ccbluex.liquidbounce.features.module.modules.movement.NoSlow.usedMode
import net.ccbluex.liquidbounce.features.module.modules.movement.noslowmodes.NoSlowMode
import net.ccbluex.liquidbounce.utils.PacketUtils.sendPacket
import net.ccbluex.liquidbounce.utils.inventory.InventoryUtils.serverSlot
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraft.util.BlockPos.ORIGIN

/**
 * @author CCBlueX/LiquidBounce
 */
object UNCP : NoSlowMode("UNCP") {
    var shouldSwap: Boolean = false
        get() = usedMode == this && field

    override fun onMotion(event: MotionEvent) {
        if (event.eventState == PRE && shouldSwap) {
            serverSlot = (serverSlot + 1) % 9
            serverSlot = mc.thePlayer.inventory.currentItem
            sendPacket(C08PacketPlayerBlockPlacement(ORIGIN, 255, mc.thePlayer.heldItem, 0f, 0f, 0f))
            shouldSwap = false
        }
    }

    override fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (event.isCancelled || shouldSwap)
            return

        if (packet is C08PacketPlayerBlockPlacement) {
            if (packet.stack?.item != null && mc.thePlayer.heldItem?.item != null && packet.stack.item == mc.thePlayer.heldItem?.item) {
                if (usedMode == this) {
                    shouldSwap = true
                }
            }
        }
    }
}
