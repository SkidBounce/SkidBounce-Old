/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.vanilla

import net.ccbluex.liquidbounce.event.EventState
import net.ccbluex.liquidbounce.event.MotionEvent
import net.ccbluex.liquidbounce.features.module.modules.player.NoFall
import net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.NoFallMode
import net.ccbluex.liquidbounce.utils.RotationUtils
import net.ccbluex.liquidbounce.utils.VecRotation
import net.ccbluex.liquidbounce.utils.extensions.eyes
import net.ccbluex.liquidbounce.utils.inventory.InventoryUtils
import net.ccbluex.liquidbounce.utils.misc.FallingPlayer
import net.ccbluex.liquidbounce.utils.timing.TickTimer
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemBucket
import net.minecraft.util.BlockPos
import net.minecraft.util.Vec3
import kotlin.math.ceil

object MLG : NoFallMode("MLG") {

    private val mlgTimer = TickTimer()
    private var currentMlgRotation: VecRotation? = null
    private var currentMlgBlock: BlockPos? = null

    override fun onMotion(event: MotionEvent) {
        val thePlayer = mc.thePlayer

        if (event.eventState == EventState.PRE) {
            currentMlgRotation = null

            mlgTimer.update()

            if (!mlgTimer.hasTimePassed(10)) return

            if (thePlayer.fallDistance > NoFall.mlgMinFallDistance) {
                val fallingPlayer = FallingPlayer(thePlayer)

                val maxDist = mc.playerController.blockReachDistance + 1.5

                val collision = fallingPlayer.findCollision(ceil(1.0 / thePlayer.motionY * -maxDist).toInt()) ?: return

                if ((thePlayer.motionY < collision.pos.y + 1 - thePlayer.posY) || thePlayer.eyes.distanceTo(
                        Vec3(
                            collision.pos
                        ).addVector(0.5, 0.5, 0.5)
                    ) < mc.playerController.blockReachDistance + 0.866025
                ) {
                    var index: Int? = null

                    for (i in 36..44) {
                        val itemStack = thePlayer.inventoryContainer.getSlot(i).stack ?: continue

                        if (itemStack.item == Items.water_bucket || itemStack.item is ItemBlock && (itemStack.item as ItemBlock).block == Blocks.web) {
                            index = i - 36

                            if (thePlayer.inventory.currentItem == index) break
                        }
                    }

                    index ?: return

                    currentMlgBlock = collision.pos

                    InventoryUtils.serverSlot = index

                    currentMlgRotation = RotationUtils.faceBlock(collision.pos)
                    currentMlgRotation?.rotation?.toPlayer(thePlayer)
                }
            }
        } else if (currentMlgRotation != null) {
            val stack = thePlayer.inventory.getStackInSlot(InventoryUtils.serverSlot)

            // If used item was a water bucket, try to pick it back up later
            if (mc.playerController.sendUseItem(thePlayer, mc.theWorld, stack) && stack.item is ItemBucket) {
                mlgTimer.reset()
            }

            InventoryUtils.serverSlot = thePlayer.inventory.currentItem
        }
    }
}
