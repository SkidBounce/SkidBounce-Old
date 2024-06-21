/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.events.PacketEvent
import net.ccbluex.liquidbounce.event.events.PushOutEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory.MOVEMENT
import net.ccbluex.liquidbounce.value.BooleanValue

/**
 * @author ManInMyVan/SkidBounce
 * @author ManInMyVan
 */
object NoPush : Module("NoPush", MOVEMENT) {
    private val blocks by BooleanValue("Blocks", true)
    val pistons by BooleanValue("Pistons", false)
    val pistonBlocks by BooleanValue("PistonBlocks", true)

    @EventTarget
    fun onPush(event: PushOutEvent) {
        if (blocks) event.cancelEvent()
    }
}
