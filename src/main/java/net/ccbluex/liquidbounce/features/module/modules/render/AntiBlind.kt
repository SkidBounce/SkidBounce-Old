/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.render

import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory.RENDER
import net.ccbluex.liquidbounce.value.BooleanValue
import net.ccbluex.liquidbounce.value.FloatValue

object AntiBlind : Module("AntiBlind", RENDER, gameDetecting = false) {
    val confusionEffect by BooleanValue("Confusion", true)
    val pumpkinEffect by BooleanValue("Pumpkin", true)
    val fireEffect by FloatValue("FireAlpha", 0.3f, 0f..1f)
    val bossHealth by BooleanValue("BossHealth", true)
}
