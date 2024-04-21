/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.event.events

import net.ccbluex.liquidbounce.event.Event
import net.minecraft.entity.Entity

/**
 * Called when player attacks other entity
 *
 * @param targetEntity Attacked entity
 */
class AttackEvent(val targetEntity: Entity?) : Event()
