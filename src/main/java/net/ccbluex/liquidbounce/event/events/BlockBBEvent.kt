/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.event.events

import net.ccbluex.liquidbounce.event.Event
import net.minecraft.block.Block
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.BlockPos

/**
 * Called when minecraft get bounding box of block
 *
 * @param blockPos block position of block
 * @param block block itself
 * @param boundingBox vanilla bounding box
 */
class BlockBBEvent(blockPos: BlockPos, val block: Block, var boundingBox: AxisAlignedBB?) : Event() {
    operator fun component1() = x
    operator fun component2() = y
    operator fun component3() = z

    val x = blockPos.x
    val y = blockPos.y
    val z = blockPos.z
}
