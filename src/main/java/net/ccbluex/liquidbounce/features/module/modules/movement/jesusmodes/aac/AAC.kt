/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.jesusmodes.aac

import net.ccbluex.liquidbounce.features.module.modules.movement.jesusmodes.JesusMode
import net.ccbluex.liquidbounce.utils.block.BlockUtils.getBlock
import net.ccbluex.liquidbounce.utils.extensions.inLiquid
import net.minecraft.init.Blocks.water

object AAC : JesusMode("AAC", false) {
    override fun onUpdate() {
        if (!mc.thePlayer.onGround && getBlock(mc.thePlayer.position.down()) == water || mc.thePlayer.inLiquid) {

            mc.thePlayer.motionX *= 0.99999
            mc.thePlayer.motionY *= 0.0
            mc.thePlayer.motionZ *= 0.99999

            if (mc.thePlayer.isCollidedHorizontally)
                mc.thePlayer.motionY = ((mc.thePlayer.posY - (mc.thePlayer.posY - 1).toInt()).toInt() / 8f).toDouble()

            if (mc.thePlayer.fallDistance >= 4) mc.thePlayer.motionY =
                -0.004 else if (mc.thePlayer.isInWater) mc.thePlayer.motionY = 0.09
        }

        if (mc.thePlayer.hurtTime != 0)
            mc.thePlayer.onGround = false
    }
}
