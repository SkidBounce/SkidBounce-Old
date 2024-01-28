/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat.velocitymodes

import net.ccbluex.liquidbounce.event.BlockBBEvent
import net.ccbluex.liquidbounce.event.JumpEvent
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.TickEvent
import net.ccbluex.liquidbounce.utils.MinecraftInstance

open class VelocityMode(val modeName: String): MinecraftInstance() {
    open fun onUpdate() {}
    open fun onTick(event: TickEvent) {}
    open fun onAttack() {}
    open fun onJump(event: JumpEvent) {}
    open fun onPacket(event: PacketEvent) {}
    open fun onBlockBB(event: BlockBBEvent) {}
    open fun onVelocityPacket(event: PacketEvent) {}
    open fun onEnable() {}
}
