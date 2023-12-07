package net.ccbluex.liquidbounce.features.module.modules.movement.noslowmodes.ncp

import net.ccbluex.liquidbounce.event.EventState
import net.ccbluex.liquidbounce.event.MotionEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.noslowmodes.NoSlowMode
import net.ccbluex.liquidbounce.utils.PacketUtils
import net.minecraft.network.play.client.C07PacketPlayerDigging
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing

object NCP : NoSlowMode("NCP") {
    override fun onMotion(event: MotionEvent) {
        when (event.eventState) {
            EventState.PRE -> PacketUtils.sendPacket(
                C07PacketPlayerDigging(
                    C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN
                )
            )
            EventState.POST -> PacketUtils.sendPacket(
                C08PacketPlayerBlockPlacement(
                    BlockPos(-1, -1, -1), 255, mc.thePlayer.heldItem, 0f, 0f, 0f
                )
            )
            else -> {}
        }
    }
}