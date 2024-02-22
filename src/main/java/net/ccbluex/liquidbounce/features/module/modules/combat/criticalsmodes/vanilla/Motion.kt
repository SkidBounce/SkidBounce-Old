/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 *  https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat.criticalsmodes.vanilla

import net.ccbluex.liquidbounce.features.module.modules.combat.Criticals
import net.ccbluex.liquidbounce.features.module.modules.combat.criticalsmodes.CriticalsMode
import net.ccbluex.liquidbounce.utils.extensions.jump
import net.minecraft.entity.Entity

/**
 * @author CCBlueX/LiquidBounce
 */
object Motion : CriticalsMode("Motion") {
    override fun onAttack(entity: Entity) {
        mc.thePlayer.jump(Criticals.motionY, boost = Criticals.motionJump, ignoreGround = true)
    }
}