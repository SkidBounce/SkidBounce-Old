/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.value

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive

/**
 * List value represents a selectable list of values
 */
open class ListValue(
    name: String,
    val values: Array<String>,
    override var value: String,
    subjective: Boolean = false,
    isSupported: (() -> Boolean)? = null
) : Value<String>(name, value, subjective, isSupported) {
    var openList = false

    operator fun contains(string: String?) = values.any { it.equals(string, true) }

    override fun changeValue(newValue: String) {
        values.find { it.equals(newValue, true) }?.let { value = it }
    }

    override fun toJsonF() = JsonPrimitive(value)

    override fun fromJsonF(element: JsonElement) = if (element.isJsonPrimitive) element.asString else null
}
