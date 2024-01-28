/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes

import net.ccbluex.liquidbounce.event.MotionEvent
import net.ccbluex.liquidbounce.event.MoveEvent
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.utils.MinecraftInstance

open class SpeedMode(val modeName: String) : MinecraftInstance() {
    open fun onMotion(event: MotionEvent) {}
    open fun onPacket(event: PacketEvent) {}
    open fun onUpdate() {}
    open fun onMove(event: MoveEvent) {}
    open fun onTick() {}
    open fun onStrafe() {}
    open fun onEnable() {}
    open fun onDisable() {}

}
