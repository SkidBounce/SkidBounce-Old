/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.targets

import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.utils.EntityUtils.targetInvisible

object Invisible : Module("Invisible", ModuleCategory.TARGETS, subjective = true, gameDetecting = false) {
    override fun onEnable() {
        super.onEnable()
        targetInvisible = true
    }
    override fun onDisable() {
        super.onDisable()
        targetInvisible = false
    }
}