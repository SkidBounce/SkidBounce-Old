/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.jesusmodes.other

import net.ccbluex.liquidbounce.features.module.modules.movement.jesusmodes.JesusMode
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.MovementUtils.isMoving
import net.ccbluex.liquidbounce.utils.MovementUtils.strafe
import net.ccbluex.liquidbounce.utils.block.BlockUtils.getBlock
import net.ccbluex.liquidbounce.utils.extensions.component1
import net.ccbluex.liquidbounce.utils.extensions.component2
import net.ccbluex.liquidbounce.utils.extensions.component3
import net.ccbluex.liquidbounce.utils.extensions.inLiquid
import net.minecraft.block.BlockLiquid
import net.minecraft.util.BlockPos

object Horizon146 : JesusMode("Horizon1.4.6", false) {
    private var wasJesus = false
    override fun onUpdate() {
        if (!mc.thePlayer.inLiquid) {
            if (wasJesus)
                mc.gameSettings.keyBindJump.pressed = false
            wasJesus = false
            return
        }

        mc.gameSettings.keyBindJump.pressed = true
        strafe()
        if (isMoving && !mc.thePlayer.onGround)
            mc.thePlayer.motionY += 0.13

        wasJesus = mc.thePlayer.inLiquid
    }
}
