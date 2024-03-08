/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.verus

import net.ccbluex.liquidbounce.event.MoveEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.SpeedMode
import net.ccbluex.liquidbounce.utils.MovementUtils.isMoving
import net.ccbluex.liquidbounce.utils.MovementUtils.strafe
import net.ccbluex.liquidbounce.utils.extensions.jump

/**
 * @author liquidbounceplusreborn/LiquidbouncePlus-Reborn
 */
object VerusLowHop2 : SpeedMode("VerusLowHop2") {
    override fun onMove(event: MoveEvent) {
        mc.thePlayer.run {
            if (isInWeb || isInLava || isInWater || isOnLadder || ridingEntity != null || !isMoving)
                return
            if (onGround) {
                jump(0)
                strafe(0.61f)
                event.y = 0.41999998688698
            }
            strafe()
        }
    }
}
