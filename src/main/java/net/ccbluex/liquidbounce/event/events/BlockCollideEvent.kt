/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.event.events

import net.ccbluex.liquidbounce.event.CancellableEvent
import net.minecraft.block.state.IBlockState
import net.minecraft.util.BlockPos

/**
 * Called when the player collides with a block
 *
 * @param blockPos position of block
 * @param blockState blockState
 */
class BlockCollideEvent(val blockPos: BlockPos, val blockState: IBlockState) : CancellableEvent() {
    operator fun component1() = blockPos.x
    operator fun component2() = blockPos.y
    operator fun component3() = blockPos.z
}
