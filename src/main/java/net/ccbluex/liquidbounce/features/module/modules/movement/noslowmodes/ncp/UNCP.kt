package net.ccbluex.liquidbounce.features.module.modules.movement.noslowmodes.ncp

import net.ccbluex.liquidbounce.features.module.modules.movement.noslowmodes.NoSlowMode
import net.ccbluex.liquidbounce.event.EventState
import net.ccbluex.liquidbounce.event.MotionEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.NoSlow
import net.ccbluex.liquidbounce.features.module.modules.movement.NoSlow.isHoldingConsumable
import net.ccbluex.liquidbounce.utils.PacketUtils.sendPacket
import net.ccbluex.liquidbounce.utils.inventory.InventoryUtils
import net.minecraft.item.ItemBow
import net.minecraft.item.ItemSword
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraft.util.BlockPos

object UNCP : NoSlowMode("UNCP") {
    override fun onMotion(event: MotionEvent) {
        if (mc.thePlayer.heldItem?.item is ItemSword) {
            if (event.eventState == EventState.POST) {
                sendPacket(
                    C08PacketPlayerBlockPlacement(
                        BlockPos.ORIGIN, 255, mc.thePlayer.heldItem, 0f, 0f, 0f
                    )
                )
            }
        }
        if (mc.thePlayer.heldItem?.item is ItemBow || isHoldingConsumable()) {
            if (event.eventState == EventState.PRE && NoSlow.shouldSwap) {
                InventoryUtils.serverSlot = (InventoryUtils.serverSlot + 1) % 9
                InventoryUtils.serverSlot = mc.thePlayer.inventory.currentItem
                sendPacket(C08PacketPlayerBlockPlacement(BlockPos.ORIGIN, 255, mc.thePlayer.heldItem, 0f, 0f, 0f))
                NoSlow.shouldSwap = false
            }
        }
    }
}