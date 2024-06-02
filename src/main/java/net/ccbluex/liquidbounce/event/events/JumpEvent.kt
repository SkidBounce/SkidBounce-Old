/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.event.events

import net.ccbluex.liquidbounce.event.CancellableEvent

/**
 * Called when player jumps
 *
 * @param motion jump motion (y motion)
 */
class JumpEvent(var motion: Float, var sprintBoost: Float) : CancellableEvent()
