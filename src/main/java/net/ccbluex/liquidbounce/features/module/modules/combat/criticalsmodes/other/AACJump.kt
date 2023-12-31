package net.ccbluex.liquidbounce.features.module.modules.combat.criticalsmodes.other

import net.ccbluex.liquidbounce.features.module.modules.combat.criticalsmodes.CriticalsMode
import net.minecraft.entity.Entity

object AACJump : CriticalsMode("AACJump") {
    override fun onAttack(entity: Entity) {
        mc.thePlayer.isInWeb = true
        mc.thePlayer.jump()
        mc.thePlayer.prevPosY = mc.thePlayer.posY
        mc.thePlayer.isInWeb = false
        if (!mc.thePlayer.onGround) {
            mc.thePlayer.motionY = -0.01
            mc.thePlayer.posY = mc.thePlayer.prevPosY
        }

    }
}
