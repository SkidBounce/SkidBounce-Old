/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory.MOVEMENT
import net.ccbluex.liquidbounce.utils.extensions.update

object AutoWalk : Module("AutoWalk", MOVEMENT, gameDetecting = false) {

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        mc.gameSettings.keyBindForward.pressed = true
    }

    override fun onDisable() {
        mc.gameSettings.keyBindForward.update()
    }
}
