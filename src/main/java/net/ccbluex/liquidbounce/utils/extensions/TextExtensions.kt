/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.utils.extensions

fun String.toLowerCamelCase() = this.replaceFirst(this.toCharArray()[0], this.toCharArray()[0].lowercaseChar())
