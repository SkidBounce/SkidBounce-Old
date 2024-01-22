/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.other

import net.ccbluex.liquidbounce.event.MotionEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.IceSpeed
import net.ccbluex.liquidbounce.features.module.modules.movement.Speed.cardinalJumpWhenIceSpeed
import net.ccbluex.liquidbounce.features.module.modules.movement.Speed.cardinalWaterLowHop
import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.SpeedMode
import net.ccbluex.liquidbounce.utils.MovementUtils.isMoving
import net.ccbluex.liquidbounce.utils.MovementUtils.isOnGround
import net.ccbluex.liquidbounce.utils.MovementUtils.onIce
import net.ccbluex.liquidbounce.utils.MovementUtils.strafe
import net.ccbluex.liquidbounce.utils.block.BlockUtils.getBlock
import net.ccbluex.liquidbounce.utils.extensions.stopXZ
import net.minecraft.init.Blocks.water
import net.minecraft.util.BlockPos

object Cardinal : SpeedMode("Cardinal") {
    override fun onMotion(event: MotionEvent) {
        if (isMoving) {
            mc.thePlayer.jumpMovementFactor = 0.026f

            if (mc.thePlayer.onGround && !(onIce && IceSpeed.state && cardinalJumpWhenIceSpeed)) {
                mc.thePlayer.jump()
                if (getBlock(BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ)) == water && cardinalWaterLowHop) {
                    mc.thePlayer.motionY *= 0.875
                }
            }

            if (isOnGround(0.35))
                strafe()
        }
        else mc.thePlayer.stopXZ()
    }

    override fun onDisable() {
        mc.thePlayer.jumpMovementFactor = 0.02f
    }
}
