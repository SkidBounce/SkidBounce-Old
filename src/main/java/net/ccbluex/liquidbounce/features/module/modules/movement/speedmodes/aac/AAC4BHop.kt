/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.aac

import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.SpeedMode
import net.ccbluex.liquidbounce.utils.MovementUtils.isMoving
import net.ccbluex.liquidbounce.utils.MovementUtils.strafe
import net.ccbluex.liquidbounce.utils.extensions.jmp

/**
 * @author CCBlueX/LiquidBounce
 */
object AAC4BHop : SpeedMode("AAC4BHop") {
    private var legitHop = false

    override fun onDisable() {
        mc.thePlayer.speedInAir = 0.02f
    }

    override fun onTick() {
        val thePlayer = mc.thePlayer ?: return

        if (isMoving) {
            if (legitHop) {
                if (thePlayer.onGround) {
                    thePlayer.jmp()
                    thePlayer.onGround = false
                    legitHop = false
                }
                return
            }
            if (thePlayer.onGround) {
                thePlayer.onGround = false
                strafe(0.375f)
                thePlayer.jmp(0.41, ignoreGround = true)
            } else thePlayer.speedInAir = 0.0211f
        } else {
            thePlayer.motionX = 0.0
            thePlayer.motionZ = 0.0
            legitHop = true
        }
    }

    override fun onEnable() {
        legitHop = true
    }
}
