/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.nowebmodes

import net.ccbluex.liquidbounce.utils.MinecraftInstance

open class NoWebMode(val modeName: String) : MinecraftInstance() {
    open fun onUpdate() {}
}
