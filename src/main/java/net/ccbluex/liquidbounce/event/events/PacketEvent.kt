/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.event.events

import net.ccbluex.liquidbounce.event.CancellableEvent
import net.ccbluex.liquidbounce.event.EventState
import net.minecraft.network.Packet

/**
 * Called when receive or send a packet
 */
class PacketEvent(val packet: Packet<*>, val eventType: EventState) : CancellableEvent()
