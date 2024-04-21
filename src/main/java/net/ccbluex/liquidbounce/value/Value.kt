/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.value

import com.google.gson.JsonElement
import net.ccbluex.liquidbounce.file.FileManager.saveConfig
import net.ccbluex.liquidbounce.file.FileManager.valuesConfig
import net.ccbluex.liquidbounce.utils.ClientUtils.LOGGER
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

abstract class Value<T>(
    var name: String,
    protected open var value: T,
    val subjective: Boolean = false,
    var isSupported: (() -> Boolean)? = null
) : ReadWriteProperty<Any?, T> {
    fun set(newValue: T): Boolean {
        if (newValue == value)
            return false

        val oldValue = value

        try {
            val handledValue = onChange(oldValue, newValue)
            if (handledValue == oldValue) return false

            changeValue(handledValue)
            onChanged(oldValue, handledValue)
            onUpdate(handledValue)

            saveConfig(valuesConfig)
            return true
        } catch (e: Exception) {
            LOGGER.error("[ValueSystem ($name)]: ${e.javaClass.name} (${e.message}) [$oldValue >> $newValue]")
            return false
        }
    }

    fun get() = value

    open fun changeValue(newValue: T) {
        value = newValue
    }

    open fun toJson() = toJsonF()

    open fun fromJson(element: JsonElement) {
        val result = fromJsonF(element)
        if (result != null) changeValue(result)

        onInit(value)
        onUpdate(value)
    }

    abstract fun toJsonF(): JsonElement?
    abstract fun fromJsonF(element: JsonElement): T?

    protected open fun onInit(value: T) {}
    protected open fun onUpdate(value: T) {}
    protected open fun onChange(oldValue: T, newValue: T) = newValue
    protected open fun onChanged(oldValue: T, newValue: T) {}
    open fun isSupported() = isSupported?.invoke() ?: true

    // Support for delegating values using the `by` keyword.
    override operator fun getValue(thisRef: Any?, property: KProperty<*>) = value
    override operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        set(value)
    }
}
