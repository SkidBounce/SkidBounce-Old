package net.ccbluex.liquidbounce.features.module.modules.combat.criticalsmodes.other

import net.ccbluex.liquidbounce.features.module.modules.combat.Criticals.motionJump
import net.ccbluex.liquidbounce.features.module.modules.combat.Criticals.motionY
import net.ccbluex.liquidbounce.features.module.modules.combat.criticalsmodes.CriticalsMode
import net.minecraft.entity.Entity

object Motion : CriticalsMode("Motion") {
    override fun onAttack(entity: Entity) {
        if (motionJump) mc.thePlayer.jump()
        mc.thePlayer.motionY = motionY.toDouble()
    }
}
