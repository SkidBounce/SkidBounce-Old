/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.lang

class Language(val locale: String, val contributors: List<String>, val translations: Map<String, String>) {
    fun getTranslation(key: String, vararg args: Any) = translations[key]?.format(*args)

    override fun toString() = locale
}
