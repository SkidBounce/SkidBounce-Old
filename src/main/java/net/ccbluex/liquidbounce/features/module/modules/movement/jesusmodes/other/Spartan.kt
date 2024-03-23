/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.jesusmodes.other

import net.ccbluex.liquidbounce.features.module.modules.movement.jesusmodes.JesusMode
import net.ccbluex.liquidbounce.utils.block.BlockUtils.getBlock
import net.ccbluex.liquidbounce.utils.extensions.component1
import net.ccbluex.liquidbounce.utils.extensions.component2
import net.ccbluex.liquidbounce.utils.extensions.component3
import net.ccbluex.liquidbounce.utils.extensions.inLiquid
import net.minecraft.block.BlockLiquid
import net.minecraft.util.BlockPos

object Spartan : JesusMode("Spartan", false) {
    override fun onUpdate() {
        if (!mc.thePlayer.inLiquid)
            return

        if (mc.thePlayer.isCollidedHorizontally) {
            mc.thePlayer.motionY += 0.15
            return
        }

        val (x, y, z) = mc.thePlayer
        when {
            getBlock(BlockPos(x, y + 1.1, z)) is BlockLiquid -> mc.thePlayer.motionY = 0.1
            getBlock(BlockPos(x, y + 1.0, z)) is BlockLiquid -> mc.thePlayer.motionY = 0.0
        }

        mc.thePlayer.onGround = true
        mc.thePlayer.motionX *= 1.085
        mc.thePlayer.motionZ *= 1.085
    }
}
