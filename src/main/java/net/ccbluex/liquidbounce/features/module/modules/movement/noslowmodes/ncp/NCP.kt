/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.noslowmodes.ncp

import net.ccbluex.liquidbounce.event.EventState.POST
import net.ccbluex.liquidbounce.event.EventState.PRE
import net.ccbluex.liquidbounce.event.events.MotionEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.NoSlow.ncpFunnyReleasePacket
import net.ccbluex.liquidbounce.features.module.modules.movement.NoSlow.ncpFunnyUsePacket
import net.ccbluex.liquidbounce.features.module.modules.movement.noslowmodes.NoSlowMode
import net.ccbluex.liquidbounce.utils.PacketUtils.sendPacket
import net.minecraft.network.play.client.C07PacketPlayerDigging
import net.minecraft.network.play.client.C07PacketPlayerDigging.Action.RELEASE_USE_ITEM
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraft.util.BlockPos
import net.minecraft.util.BlockPos.ORIGIN
import net.minecraft.util.EnumFacing.DOWN

/**
 * @author CCBlueX/LiquidBounce
 */
object NCP : NoSlowMode("NCP") {
    override fun onMotion(event: MotionEvent) {
        when (event.eventState) {
            PRE -> sendPacket(
                C07PacketPlayerDigging(
                    RELEASE_USE_ITEM,
                    if (ncpFunnyReleasePacket) BlockPos(-1, -1, -1) else ORIGIN,
                    DOWN
                )
            )

            POST -> sendPacket(
                C08PacketPlayerBlockPlacement(
                    if (ncpFunnyUsePacket) ORIGIN else BlockPos(-1, -1, -1),
                    255,
                    mc.thePlayer.heldItem,
                    0f, 0f, 0f
                )
            )
            else -> {}
        }
    }
}
