/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.value

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import java.math.BigDecimal
import java.math.BigInteger

/**
 * Number value represents a value with a number
 */
open class NumberValue<T>(
    name: String,
    value: T,
    val range: ClosedRange<T>,
    subjective: Boolean = false,
    isSupported: (() -> Boolean)? = null
) : Value<T>(name, value, subjective, isSupported) where T : Number, T : Comparable<T> {
    @Suppress("UNCHECKED_CAST")
    // has to be internal?
    internal fun set(newValue: Number): Boolean = super.set(
        when (value) {
            is Float -> newValue.toFloat()
            is Double -> newValue.toDouble()
            is Byte -> newValue.toByte()
            is Short -> newValue.toShort()
            is Int -> newValue.toInt()
            is Long -> newValue.toLong()
            is BigDecimal -> BigDecimal(newValue.toString())
            is BigInteger -> BigInteger.valueOf(newValue.toLong())
            else -> throw IllegalStateException()
        } as T
    )

    override fun toJsonF() = JsonPrimitive(value)

    @Suppress("UNCHECKED_CAST")
    override fun fromJsonF(element: JsonElement): T? =
        if (!element.isJsonPrimitive) null else
        when (value) {
            is Float -> element.asFloat
            is Double -> element.asDouble
            is Byte -> element.asByte
            is Short -> element.asShort
            is Int -> element.asInt
            is Long -> element.asLong
            is BigDecimal -> element.asBigDecimal
            is BigInteger -> element.asBigInteger
            else -> throw IllegalStateException()
        } as T

    val isMinimal get() = value <= minimum
    val isMaximal get() = value >= maximum

    val minimum: T = range.start
    val maximum: T = range.endInclusive
}
