/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.utils.extensions

fun String.toLowerCamelCase() = this.replaceFirst(this.toCharArray()[0], this.toCharArray()[0].lowercaseChar())

/**
 * Thing I'll probably only use for debugging
 * @return [String] form of [Double] in a human-readable format
 */
fun Double.toPlainString(): String {
    if (this == 0.0) return "0.0"
    val str = String.format("%.32f", this).trim('0')
    return when {
        str.endsWith('.') -> "${str}0"
        str.startsWith('.') -> "0$str"
        else -> str
    }
}
