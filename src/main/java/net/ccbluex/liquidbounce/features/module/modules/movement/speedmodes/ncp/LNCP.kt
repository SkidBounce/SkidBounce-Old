/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.ncp

import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.SpeedMode
import net.ccbluex.liquidbounce.utils.MovementUtils.baseMoveSpeed
import net.ccbluex.liquidbounce.utils.MovementUtils.isMoving
import net.ccbluex.liquidbounce.utils.MovementUtils.strafe
import net.ccbluex.liquidbounce.utils.extensions.jmp
import net.ccbluex.liquidbounce.utils.extensions.stopXZ

object LNCP : SpeedMode("LNCP") {

    private var mspeed = 0.0
    private var justJumped = false

    override fun onEnable() {
        mc.timer.timerSpeed = 1.0865f
    }

    override fun onUpdate() {
        if (isMoving) {
            if (mc.thePlayer.onGround) {
                mc.thePlayer.jmp()
                mspeed = baseMoveSpeed * 1.73
                justJumped = true
            } else {
                if (justJumped) {
                    mspeed *= 0.72150289018
                    justJumped = false
                } else mspeed -= mspeed / 159
            }
            mspeed = mspeed.coerceAtLeast(baseMoveSpeed)

            strafe(mspeed)
        } else mc.thePlayer.stopXZ()
    }
}
