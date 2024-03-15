/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.ncp

import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.SpeedMode
import net.ccbluex.liquidbounce.utils.MovementUtils.isMoving
import net.ccbluex.liquidbounce.utils.MovementUtils.strafe
import net.ccbluex.liquidbounce.utils.extensions.jmp
import net.ccbluex.liquidbounce.utils.extensions.stopXZ

/**
 * @author CCBlueX/LiquidBounce
 */
object NCP : SpeedMode("NCP") {
    override fun onEnable() {
        mc.timer.timerSpeed = 1.0865f
        super.onEnable()
    }

    override fun onUpdate() {
        if (isMoving) {
            if (mc.thePlayer.onGround) {
                mc.thePlayer.jmp()
                mc.thePlayer.speedInAir = 0.0223f
            }
            strafe()
        } else mc.thePlayer.stopXZ()
    }
}
