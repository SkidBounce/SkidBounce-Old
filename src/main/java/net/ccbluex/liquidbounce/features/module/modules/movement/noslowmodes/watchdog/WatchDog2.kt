/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.noslowmodes.watchdog

import net.ccbluex.liquidbounce.event.EventState.POST
import net.ccbluex.liquidbounce.event.EventState.PRE
import net.ccbluex.liquidbounce.event.events.MotionEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.noslowmodes.NoSlowMode
import net.ccbluex.liquidbounce.utils.PacketUtils.sendPacket
import net.minecraft.network.play.client.C07PacketPlayerDigging
import net.minecraft.network.play.client.C07PacketPlayerDigging.Action.RELEASE_USE_ITEM
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraft.util.BlockPos
import net.minecraft.util.BlockPos.ORIGIN
import net.minecraft.util.EnumFacing.DOWN

/**
 * @author SkidderMC/FDPClient
 */
object WatchDog2 : NoSlowMode("WatchDog2") {
    override fun onMotion(event: MotionEvent) {
        when (event.eventState) {
            PRE -> sendPacket(C07PacketPlayerDigging(RELEASE_USE_ITEM, ORIGIN, DOWN))
            POST -> sendPacket(C08PacketPlayerBlockPlacement(BlockPos(-1, -1, -1), 255, null, 0f, 0f, 0f))
            else -> {}
        }
    }
}
