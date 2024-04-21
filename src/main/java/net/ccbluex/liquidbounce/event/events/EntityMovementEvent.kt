/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.event.events

import net.ccbluex.liquidbounce.event.Event
import net.minecraft.entity.Entity

/**
 * Called when another entity moves
 */
data class EntityMovementEvent(val movedEntity: Entity) : Event()
