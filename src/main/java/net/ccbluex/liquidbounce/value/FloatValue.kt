/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.value

open class FloatValue(
    name: String,
    value: Float,
    range: ClosedRange<Float>,
    subjective: Boolean = false,
    isSupported: (() -> Boolean)? = null
) : NumberValue<Float>(name, value, range, subjective, isSupported)
