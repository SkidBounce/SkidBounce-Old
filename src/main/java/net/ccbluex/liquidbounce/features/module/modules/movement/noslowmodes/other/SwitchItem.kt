/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.noslowmodes.other

import net.ccbluex.liquidbounce.event.EventState
import net.ccbluex.liquidbounce.event.MotionEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.noslowmodes.NoSlowMode
import net.ccbluex.liquidbounce.utils.inventory.InventoryUtils.serverSlot

object SwitchItem : NoSlowMode("SwitchItem") {
    override fun onMotion(event: MotionEvent) {
        if (event.eventState == EventState.PRE) {
            serverSlot = (serverSlot + 1) % 9
            serverSlot = mc.thePlayer.inventory.currentItem
        }
    }
}
