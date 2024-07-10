/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.render

import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory.RENDER
import net.ccbluex.liquidbounce.value.BooleanValue

object Chams : Module("Chams", RENDER) {
    val targets by BooleanValue("Targets", true)
    val chests by BooleanValue("Chests", true)
    val items by BooleanValue("Items", true)
}
