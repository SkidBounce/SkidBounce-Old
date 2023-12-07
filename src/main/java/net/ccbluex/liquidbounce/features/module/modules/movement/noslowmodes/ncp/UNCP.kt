package net.ccbluex.liquidbounce.features.module.modules.movement.noslowmodes.ncp

import net.ccbluex.liquidbounce.features.module.modules.movement.noslowmodes.NoSlowMode
import net.ccbluex.liquidbounce.event.EventState
import net.ccbluex.liquidbounce.event.MotionEvent
import net.ccbluex.liquidbounce.utils.PacketUtils.sendPacket
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraft.util.BlockPos

object UNCP : NoSlowMode("UNCP") {
    override fun onMotion(event: MotionEvent) {
        when (event.eventState) {
            EventState.POST -> {
                var pi459312 = 5
                if (mc.thePlayer.isBlocking) pi459312 = 255
                sendPacket(
                    C08PacketPlayerBlockPlacement(
                        BlockPos.ORIGIN, pi459312, mc.thePlayer.heldItem, 0f, 0f, 0f
                    )
                )
            }

            else -> {}
        }
    }
}