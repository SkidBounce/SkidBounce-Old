/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.value

/**
 * Block value represents a value with a block
 */
open class BlockValue(name: String, value: Int, subjective: Boolean = false, isSupported: (() -> Boolean)? = null)
    : IntValue(name, value, 1..197, subjective, isSupported)
