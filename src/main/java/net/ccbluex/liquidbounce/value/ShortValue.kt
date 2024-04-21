/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.value

open class ShortValue(
    name: String,
    value: Short,
    range: ClosedRange<Short>,
    subjective: Boolean = false,
    isSupported: (() -> Boolean)? = null
) : NumberValue<Short>(name, value, range, subjective, isSupported)
