/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.value

open class DoubleValue(
    name: String,
    value: Double,
    range: ClosedRange<Double>,
    subjective: Boolean = false,
    isSupported: (() -> Boolean)? = null
) : NumberValue<Double>(name, value, range, subjective, isSupported)
