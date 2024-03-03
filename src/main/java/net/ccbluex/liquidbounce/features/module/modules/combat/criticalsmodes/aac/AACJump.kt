/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat.criticalsmodes.aac

import net.ccbluex.liquidbounce.features.module.modules.combat.criticalsmodes.CriticalsMode
import net.ccbluex.liquidbounce.utils.extensions.jump
import net.minecraft.entity.Entity

/**
 * @author CCBlueX/LiquidBounce
 */
object AACJump : CriticalsMode("AACJump") {
    override fun onAttack(entity: Entity) {
        mc.thePlayer.isInWeb = true
        mc.thePlayer.jump(ignoreGround = true)
        mc.thePlayer.prevPosY = mc.thePlayer.posY
        mc.thePlayer.isInWeb = false
        if (!mc.thePlayer.onGround) {
            mc.thePlayer.motionY = -0.01
            mc.thePlayer.posY = mc.thePlayer.prevPosY
        }
    }
}