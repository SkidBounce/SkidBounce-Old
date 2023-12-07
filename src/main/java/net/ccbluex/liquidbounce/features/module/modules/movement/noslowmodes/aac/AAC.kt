package net.ccbluex.liquidbounce.features.module.modules.movement.noslowmodes.aac

import net.ccbluex.liquidbounce.event.EventState
import net.ccbluex.liquidbounce.event.MotionEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.noslowmodes.NoSlowMode
import net.ccbluex.liquidbounce.utils.PacketUtils.sendPacket
import net.minecraft.network.play.client.C07PacketPlayerDigging
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing

object AAC : NoSlowMode("AAC") {
    override fun onMotion(event: MotionEvent) {
        if (mc.thePlayer.ticksExisted % 3 == 0) {
            if (event.eventState == EventState.PRE) sendPacket(
                C07PacketPlayerDigging(
                    C07PacketPlayerDigging.Action.RELEASE_USE_ITEM,
                    BlockPos(-1, -1, -1),
                    EnumFacing.DOWN
                )
            )
        } else if (event.eventState == EventState.POST) sendPacket(C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()))
    }
}