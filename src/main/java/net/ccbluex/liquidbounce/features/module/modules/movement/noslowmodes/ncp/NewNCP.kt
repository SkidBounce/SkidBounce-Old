/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.noslowmodes.ncp

import net.ccbluex.liquidbounce.event.EventState
import net.ccbluex.liquidbounce.event.MotionEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.NoSlow
import net.ccbluex.liquidbounce.features.module.modules.movement.noslowmodes.NoSlowMode
import net.ccbluex.liquidbounce.utils.PacketUtils
import net.minecraft.network.play.client.C07PacketPlayerDigging
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing

object NewNCP : NoSlowMode("NewNCP") {
    override fun onMotion(event: MotionEvent) {
        if (!mc.thePlayer.onGround) return
        if (mc.thePlayer.ticksExisted % 2 == 0) {
            if (event.eventState == EventState.PRE) {
                PacketUtils.sendPacket(
                    C07PacketPlayerDigging(
                        C07PacketPlayerDigging.Action.RELEASE_USE_ITEM,
                        BlockPos(-1, -1, -1),
                        EnumFacing.DOWN
                    )
                )
            }
        }
        else if (event.eventState == EventState.POST) {
            PacketUtils.sendPacket(
                C08PacketPlayerBlockPlacement(
                    BlockPos(-1, -1, -1),
                    255,
                    mc.thePlayer.inventory.getCurrentItem(),
                    0f,
                    0f,
                    0f
                )
            )
            NoSlow.shouldSwap = false
        }
    }
}
