/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.noslowmodes.other

import net.ccbluex.liquidbounce.event.EventState
import net.ccbluex.liquidbounce.event.MotionEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.noslowmodes.NoSlowMode
import net.ccbluex.liquidbounce.utils.inventory.InventoryUtils.serverSlot

object SwitchItem : NoSlowMode("SwitchItem") {
    override fun onMotion(event: MotionEvent) {
        when (event.eventState) {
            EventState.PRE -> {
                serverSlot = (serverSlot + 1) % 9
                serverSlot = mc.thePlayer.inventory.currentItem
            }
            else -> {}
        }
    }
}