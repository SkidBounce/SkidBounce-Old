/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.noslowmodes.ncp

import net.ccbluex.liquidbounce.event.EventState
import net.ccbluex.liquidbounce.event.MotionEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.noslowmodes.NoSlowMode
import net.ccbluex.liquidbounce.utils.PacketUtils
import net.minecraft.network.play.client.C07PacketPlayerDigging
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing

object NCP2 : NoSlowMode("NCP2") {
    override fun onMotion(event: MotionEvent) {
        when (event.eventState) {
            EventState.PRE -> PacketUtils.sendPacket(
                C07PacketPlayerDigging(
                    C07PacketPlayerDigging.Action.RELEASE_USE_ITEM,
                    BlockPos(-1, -1, -1),
                    EnumFacing.DOWN
                )
            )
            EventState.POST -> PacketUtils.sendPacket(
                C08PacketPlayerBlockPlacement(mc.thePlayer.heldItem)
            )
            else -> {}
        }
    }
}