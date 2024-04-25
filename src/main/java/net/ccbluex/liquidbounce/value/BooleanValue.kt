/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.value

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive

/**
 * Bool value represents a value with a boolean
 */
open class BooleanValue(
    name: String,
    value: Boolean,
    subjective: Boolean = false,
    isSupported: (() -> Boolean)? = null
) : Value<Boolean>(name, value, subjective, isSupported) {
    override fun toJsonF() = JsonPrimitive(value)

    override fun fromJsonF(element: JsonElement) =
        if (element.isJsonPrimitive) element.asBoolean || element.asString.equals("true", ignoreCase = true)
        else null

    fun toggle() = set(!value)

    fun isActive() = value && isSupported()
}
