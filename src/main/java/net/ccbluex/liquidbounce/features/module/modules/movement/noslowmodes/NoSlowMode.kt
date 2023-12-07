package net.ccbluex.liquidbounce.features.module.modules.movement.noslowmodes

import net.ccbluex.liquidbounce.event.MotionEvent
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.utils.MinecraftInstance

open class NoSlowMode(val modeName: String): MinecraftInstance() {
	open fun onMotion(event: MotionEvent) {}
	open fun onUpdate() {}
	open fun onPacket(event: PacketEvent) {}
}
