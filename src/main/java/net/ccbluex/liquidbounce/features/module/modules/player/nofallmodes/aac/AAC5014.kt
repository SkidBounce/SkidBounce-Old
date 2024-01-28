/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.aac

import net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.NoFallMode
import net.ccbluex.liquidbounce.utils.PacketUtils.sendPacket
import net.ccbluex.liquidbounce.utils.block.BlockUtils.getBlock
import net.ccbluex.liquidbounce.utils.block.BlockUtils.getState
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition
import net.minecraft.util.BlockPos

object AAC5014 : NoFallMode("AAC5.0.14") {
    private var check = false
    private var flag = false
    private var timer = 0
    override fun onEnable() {
        check = false
        timer = 0
        flag = false
    }

    override fun onUpdate() {
        mc.thePlayer ?: return

        var offsetY = 0.0
        check = false

        while (mc.thePlayer.motionY - 1.5 < offsetY) {
            val blockPos = BlockPos(mc.thePlayer.posX, mc.thePlayer.posY + offsetY, mc.thePlayer.posZ)
            val block = getBlock(blockPos)!!
            val axisAlignedBB = block.getCollisionBoundingBox(mc.theWorld, blockPos, getState(blockPos))
            if (axisAlignedBB != null) {
                offsetY = -999.9
                check = true
            }
            offsetY -= 0.5
        }

        if (timer > 0)
            --timer

        if (mc.thePlayer.onGround) {
            mc.thePlayer.fallDistance = -2f
            check = false
        }

        if (check && mc.thePlayer.fallDistance > 2.5 && !mc.thePlayer.onGround) {
            flag = true
            timer = 18
        } else if (timer < 2)
            flag = false

        if (!flag) return
        sendPacket(C04PacketPlayerPosition(
            mc.thePlayer.posX,
            mc.thePlayer.posY + (if (mc.thePlayer.onGround) 0.5 else 0.42),
            mc.thePlayer.posZ,
            true
        ))
    }
}
