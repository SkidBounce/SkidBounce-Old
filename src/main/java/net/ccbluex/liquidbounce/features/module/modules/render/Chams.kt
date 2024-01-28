/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.render

import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.value.BoolValue

object Chams : Module("Chams", ModuleCategory.RENDER, subjective = true) {
    val targets by BoolValue("Targets", true)
    val chests by BoolValue("Chests", true)
    val items by BoolValue("Items", true)
}
