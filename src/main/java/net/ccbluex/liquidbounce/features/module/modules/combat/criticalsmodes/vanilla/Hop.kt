/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat.criticalsmodes.vanilla

import net.ccbluex.liquidbounce.features.module.modules.combat.criticalsmodes.CriticalsMode
import net.minecraft.entity.Entity

/**
 * @author CCBlueX/LiquidBounce
 */
object Hop : CriticalsMode("Hop") {
    override fun onAttack(entity: Entity) {
        mc.thePlayer.motionY = 0.1
        mc.thePlayer.fallDistance = 0.1f
        mc.thePlayer.onGround = false
    }
}
