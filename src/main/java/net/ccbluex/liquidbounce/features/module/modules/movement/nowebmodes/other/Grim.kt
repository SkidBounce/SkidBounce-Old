/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.nowebmodes.other

import net.ccbluex.liquidbounce.event.events.BlockCollideEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.NoWeb.grimBreakOnWorld
import net.ccbluex.liquidbounce.features.module.modules.movement.NoWeb.grimStrict
import net.ccbluex.liquidbounce.features.module.modules.movement.nowebmodes.NoWebMode
import net.ccbluex.liquidbounce.utils.PacketUtils.sendPacket
import net.minecraft.block.BlockWeb
import net.minecraft.network.play.client.C07PacketPlayerDigging
import net.minecraft.network.play.client.C07PacketPlayerDigging.Action.START_DESTROY_BLOCK
import net.minecraft.network.play.client.C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK
import net.minecraft.util.EnumFacing.DOWN

/**
 * @author ManInMyVan/SkidBounce
 * @author ManInMyVan
 */
object Grim : NoWebMode("Grim") {
    override fun onCollide(event: BlockCollideEvent) {
        if (event.blockState.block is BlockWeb) {
            event.cancelEvent()

            if (grimStrict) sendPacket(C07PacketPlayerDigging(START_DESTROY_BLOCK, event.blockPos, DOWN))
            if (grimBreakOnWorld) mc.theWorld.setBlockToAir(event.blockPos)

            sendPacket(C07PacketPlayerDigging(STOP_DESTROY_BLOCK, event.blockPos, DOWN))
        }
    }
}
