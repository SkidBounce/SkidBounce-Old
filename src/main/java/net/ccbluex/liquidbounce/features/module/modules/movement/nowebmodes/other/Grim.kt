/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.nowebmodes.other

import net.ccbluex.liquidbounce.features.module.modules.movement.NoWeb.grimBreakOnWorld
import net.ccbluex.liquidbounce.features.module.modules.movement.NoWeb.grimExpand
import net.ccbluex.liquidbounce.features.module.modules.movement.NoWeb.grimStrict
import net.ccbluex.liquidbounce.features.module.modules.movement.nowebmodes.NoWebMode
import net.ccbluex.liquidbounce.utils.PacketUtils.sendPacket
import net.ccbluex.liquidbounce.utils.block.BlockUtils.getBlock
import net.minecraft.init.Blocks.web
import net.minecraft.network.play.client.C07PacketPlayerDigging
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing

/**
 * @author ManInMyVan/SkidBounce
 * @author ManInMyVan
 */
object Grim : NoWebMode("Grim") {
    override fun onUpdate() {
        if (mc.thePlayer == null) return
        val blocks: MutableSet<BlockPos> = mutableSetOf()

        for (x in 3 downTo -3) {
            for (y in 3 downTo -3) {
                for (z in 3 downTo -3) {

                    val pos = BlockPos(
                        mc.thePlayer.posX.toInt() + x,
                        mc.thePlayer.posY.toInt() + y,
                        mc.thePlayer.posZ.toInt() + z
                    )

                    if (getBlock(pos) == web && // "collision" check
                        pos.x > mc.thePlayer.entityBoundingBox.minX - grimExpand - 1 &&
                        pos.x < mc.thePlayer.entityBoundingBox.maxX + grimExpand &&
                        pos.y > mc.thePlayer.entityBoundingBox.minY - grimExpand - 1 &&
                        pos.y < mc.thePlayer.entityBoundingBox.maxY + grimExpand &&
                        pos.z > mc.thePlayer.entityBoundingBox.minZ - grimExpand - 1 &&
                        pos.z < mc.thePlayer.entityBoundingBox.maxZ + grimExpand
                    ) blocks += pos
                }
            }
        }
        blocks.forEach {
            if (grimStrict) sendPacket(
                C07PacketPlayerDigging(
                    C07PacketPlayerDigging.Action.START_DESTROY_BLOCK,
                    it,
                    EnumFacing.DOWN
                )
            )

            sendPacket(
                C07PacketPlayerDigging(
                    C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK,
                    it,
                    EnumFacing.DOWN
                )
            )

            if (grimBreakOnWorld) mc.theWorld.setBlockToAir(it)
        }
        mc.thePlayer.isInWeb = false
    }
}
