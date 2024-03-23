/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.jesusmodes.aac

import net.ccbluex.liquidbounce.features.module.modules.movement.jesusmodes.JesusMode
import net.ccbluex.liquidbounce.utils.block.BlockUtils.getBlock
import net.ccbluex.liquidbounce.utils.extensions.inLiquid
import net.minecraft.init.Blocks.air
import net.minecraft.util.BlockPos

object AAC3311 : JesusMode("AAC3.3.11", false) {
    override fun onUpdate() {
        if (!mc.thePlayer.inLiquid)
            return

        mc.thePlayer.motionX *= 1.17
        mc.thePlayer.motionZ *= 1.17
        if (mc.thePlayer.isCollidedHorizontally)
            mc.thePlayer.motionY = 0.24
        else if (getBlock(BlockPos(mc.thePlayer).up()) != air)
            mc.thePlayer.motionY += 0.04
    }
}
