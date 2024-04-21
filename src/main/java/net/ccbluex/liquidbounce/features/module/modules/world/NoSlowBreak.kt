/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.world

import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory.WORLD
import net.ccbluex.liquidbounce.value.BooleanValue

object NoSlowBreak : Module("NoSlowBreak", WORLD, gameDetecting = false) {
    @JvmStatic val air by BooleanValue("Air", true)
    @JvmStatic val water by BooleanValue("Water", false)
}
