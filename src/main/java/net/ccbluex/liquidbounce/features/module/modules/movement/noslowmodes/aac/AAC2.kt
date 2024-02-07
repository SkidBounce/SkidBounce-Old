/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.noslowmodes.aac

import net.ccbluex.liquidbounce.event.EventState.*
import net.ccbluex.liquidbounce.event.MotionEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.NoSlow.shouldSwap
import net.ccbluex.liquidbounce.features.module.modules.movement.noslowmodes.NoSlowMode
import net.ccbluex.liquidbounce.utils.PacketUtils.sendPacket
import net.minecraft.network.play.client.C07PacketPlayerDigging
import net.minecraft.network.play.client.C07PacketPlayerDigging.Action.RELEASE_USE_ITEM
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing.DOWN

object AAC2 : NoSlowMode("AAC2") {
    override fun onMotion(event: MotionEvent) {
        when {
            event.eventState == PRE && mc.thePlayer.ticksExisted % 3 == 0 ->
                sendPacket(C07PacketPlayerDigging(RELEASE_USE_ITEM, BlockPos(-1, -1, -1), DOWN))
            event.eventState == POST && mc.thePlayer.ticksExisted % 3 == 1 ->
                sendPacket(C08PacketPlayerBlockPlacement(mc.thePlayer.heldItem))
        }
    }
}
