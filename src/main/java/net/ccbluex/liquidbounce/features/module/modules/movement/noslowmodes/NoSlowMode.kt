/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.noslowmodes

import net.ccbluex.liquidbounce.event.events.MotionEvent
import net.ccbluex.liquidbounce.event.events.PacketEvent
import net.ccbluex.liquidbounce.utils.MinecraftInstance

open class NoSlowMode(val modeName: String) : MinecraftInstance() {
    open fun onMotion(event: MotionEvent) {}
    open fun onUpdate() {}
    open fun onPacket(event: PacketEvent) {}
}
