/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.noslowmodes.other

import net.ccbluex.liquidbounce.event.EventState.PRE
import net.ccbluex.liquidbounce.event.events.MotionEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.noslowmodes.NoSlowMode
import net.ccbluex.liquidbounce.utils.inventory.InventoryUtils.serverSlot

/**
 * @author CCBlueX/LiquidBounce
 */
object SwitchItem : NoSlowMode("SwitchItem") {
    override fun onMotion(event: MotionEvent) {
        if (event.eventState == PRE) {
            serverSlot = (serverSlot + 1) % 9
            serverSlot = mc.thePlayer.inventory.currentItem
        }
    }
}
