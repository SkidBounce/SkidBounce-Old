/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.ui.client.hud.element

import net.ccbluex.liquidbounce.utils.render.RenderUtils

/**
 * Border of element
 */
data class Border(val x: Float, val y: Float, val x2: Float, val y2: Float) {
    fun draw() = RenderUtils.drawBorderedRect(x, y, x2, y2, 3F, Int.MIN_VALUE, 0)
}
