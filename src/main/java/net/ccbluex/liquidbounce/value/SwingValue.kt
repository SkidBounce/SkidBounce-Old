/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.value

/**
 * Swing value represents an arm swing mode
 */
open class SwingValue(
    name: String = "Swing",
    isSupported: (() -> Boolean)? = null
) : ListValue(name, arrayOf("Normal", "Packet", "Visual", "Off"), "Normal", false, isSupported)
