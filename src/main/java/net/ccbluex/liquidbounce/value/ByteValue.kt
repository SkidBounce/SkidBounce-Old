/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.value

open class ByteValue(
    name: String,
    value: Byte,
    range: ClosedRange<Byte>,
    subjective: Boolean = false,
    isSupported: (() -> Boolean)? = null
) : NumberValue<Byte>(name, value, range, subjective, isSupported)
