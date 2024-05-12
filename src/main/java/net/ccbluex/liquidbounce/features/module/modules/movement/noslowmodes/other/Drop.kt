/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.noslowmodes.other

import net.ccbluex.liquidbounce.event.events.PacketEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.noslowmodes.NoSlowMode
import net.ccbluex.liquidbounce.utils.PacketUtils.sendPacket
import net.ccbluex.liquidbounce.utils.extensions.canUse
import net.ccbluex.liquidbounce.utils.extensions.isUse
import net.ccbluex.liquidbounce.utils.inventory.InventoryUtils.serverSlot
import net.minecraft.network.play.client.C07PacketPlayerDigging
import net.minecraft.network.play.client.C07PacketPlayerDigging.Action.DROP_ITEM
import net.minecraft.network.play.server.S2FPacketSetSlot
import net.minecraft.util.BlockPos.ORIGIN
import net.minecraft.util.EnumFacing.DOWN

object Drop : NoSlowMode("Drop") {
    var received = false
        private set

    override fun onPacket(event: PacketEvent) {
        if (event.isCancelled)
            return

        if (!mc.thePlayer.isUsingItem)
            received = false

        if (event.packet.isUse && mc.thePlayer.heldItem.item.canUse) {
            // fixme this should be after the packet is sent
            sendPacket(C07PacketPlayerDigging(DROP_ITEM, ORIGIN, DOWN))
            received = false
        } else if (event.packet is S2FPacketSetSlot && mc.thePlayer.isUsingItem && !received) {
            if (event.packet.func_149175_c() != 0 || event.packet.func_149173_d() != serverSlot + 36)
                return

            event.cancelEvent()
            received = true

            mc.thePlayer.itemInUse = event.packet.func_149174_e()
            if (!mc.thePlayer.isUsingItem)
                mc.thePlayer.itemInUseCount = 0
            mc.thePlayer.inventory.mainInventory[serverSlot] = event.packet.func_149174_e()
        }
    }
}
