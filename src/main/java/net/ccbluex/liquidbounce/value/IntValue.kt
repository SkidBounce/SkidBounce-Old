/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.value

open class IntValue(
    name: String,
    value: Int,
    range: ClosedRange<Int>,
    subjective: Boolean = false,
    isSupported: (() -> Boolean)? = null
) : NumberValue<Int>(name, value, range, subjective, isSupported)
