package net.ccbluex.liquidbounce.features.module.modules.combat.criticalsmodes

import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.utils.MinecraftInstance
import net.minecraft.entity.Entity

open class CriticalsMode(val modeName: String): MinecraftInstance() {
    open fun onUpdate() {}
    open fun onAttack(entity: Entity) {}
    open fun onPacket(event: PacketEvent) {}
}
