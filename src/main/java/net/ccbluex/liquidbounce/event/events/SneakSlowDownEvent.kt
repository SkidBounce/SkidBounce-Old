/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.event.events

import net.ccbluex.liquidbounce.event.Event

/**
 * Called in "onLivingUpdate" when the player is sneaking.
 *
 * @param strafe the applied strafe slow down
 * @param forward the applied forward slow down
 */
class SneakSlowDownEvent(var strafe: Float, var forward: Float) : Event()
