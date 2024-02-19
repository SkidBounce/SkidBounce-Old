/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat.velocitymodes.aac

import net.ccbluex.liquidbounce.features.module.modules.combat.Velocity.aacv4MotionReducer
import net.ccbluex.liquidbounce.features.module.modules.combat.velocitymodes.VelocityMode

/**
 * @author CCBlueX/LiquidBounce
 */
object AACv4 : VelocityMode("AACv4") {
    override fun onUpdate() {
        if (mc.thePlayer.hurtTime > 0 && !mc.thePlayer.onGround && !mc.thePlayer.isInWater && !mc.thePlayer.isInLava && !mc.thePlayer.isInWeb) {
            mc.thePlayer.motionX *= aacv4MotionReducer
            mc.thePlayer.motionZ *= aacv4MotionReducer
        }
    }
}
