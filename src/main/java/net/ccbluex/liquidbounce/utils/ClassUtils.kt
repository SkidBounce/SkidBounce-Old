/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.utils

import org.apache.logging.log4j.core.config.plugins.ResolverUtil

object ClassUtils {

    private val cachedClasses = mutableMapOf<String, Boolean>()

    /**
     * Allows you to check for existing classes with the [className]
     */
    fun hasClass(className: String) =
        if (className in cachedClasses)
            cachedClasses[className]!!
        else try {
            Class.forName(className)
            cachedClasses[className] = true

            true
        } catch (e: ClassNotFoundException) {
            cachedClasses[className] = false

            false
        }

    fun hasForge() = hasClass("net.minecraftforge.common.MinecraftForge")

    val Class<*>.isObject: Boolean
        get() = fields.any { it.name == "INSTANCE" }

    inline fun <reified T> Array<Class<T>>.getAllObjects(): Array<T> {
        val list = mutableListOf<T>()
        for (`class` in this) {
            val instance = `class`.fields.find { it.name == "INSTANCE" }?.get(this) as? T ?: continue
            list += instance
        }
        return list.toTypedArray()
    }
    inline fun <reified T> getAllClassesIn(`package`: String): Array<Class<T>> {
        val resolver = ResolverUtil()
        resolver.classLoader = T::class.java.classLoader
        val test = object : ResolverUtil.ClassTest() {
            override fun matches(type: Class<*>) = type.superclass == T::class.java
        }
        resolver.findInPackage(test, `package`)

        return resolver.classes.filterIsInstance<Class<T>>().toTypedArray()
    }
    inline fun <reified T> Package.getAllClasses(): Array<Class<T>> = getAllClassesIn<T>(name)
    inline fun <reified T> Package.getAllObjects(): Array<T> = getAllClasses<T>().getAllObjects()
}
