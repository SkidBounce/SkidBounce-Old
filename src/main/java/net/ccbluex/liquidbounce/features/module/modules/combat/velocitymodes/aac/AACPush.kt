/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat.velocitymodes.aac

import net.ccbluex.liquidbounce.event.events.JumpEvent
import net.ccbluex.liquidbounce.features.module.modules.combat.Velocity.aacPushXZReducer
import net.ccbluex.liquidbounce.features.module.modules.combat.Velocity.aacPushYReducer
import net.ccbluex.liquidbounce.features.module.modules.combat.velocitymodes.VelocityMode
import net.ccbluex.liquidbounce.features.module.modules.movement.Speed

/**
 * @author CCBlueX/LiquidBounce
 */
object AACPush : VelocityMode("AACPush") {
    private var jump = false
    override fun onUpdate() {
        if (mc.thePlayer.isInWater || mc.thePlayer.isInLava || mc.thePlayer.isInWeb) return
        if (jump) {
            if (mc.thePlayer.onGround) jump = false
        } else {
            if (mc.thePlayer.hurtTime > 0 && mc.thePlayer.motionX != 0.0 && mc.thePlayer.motionZ != 0.0)
                mc.thePlayer.onGround = true

            if (mc.thePlayer.hurtResistantTime > 0 && aacPushYReducer && !Speed.handleEvents())
                mc.thePlayer.motionY -= 0.014999993
        }
        if (mc.thePlayer.hurtResistantTime >= 19) {
            mc.thePlayer.motionX /= aacPushXZReducer
            mc.thePlayer.motionZ /= aacPushXZReducer
        }
    }

    override fun onJump(event: JumpEvent) {
        jump = true
        if (!mc.thePlayer.isCollidedVertically)
            event.cancelEvent()
    }
}
