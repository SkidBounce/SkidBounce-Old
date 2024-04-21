/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.value

open class LongValue(
    name: String,
    value: Long,
    range: ClosedRange<Long>,
    subjective: Boolean = false,
    isSupported: (() -> Boolean)? = null
) : NumberValue<Long>(name, value, range, subjective, isSupported)
