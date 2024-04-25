/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.events.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory.COMBAT
import net.ccbluex.liquidbounce.utils.EntityUtils.isSelected
import net.ccbluex.liquidbounce.utils.PacketUtils.sendPacket
import net.ccbluex.liquidbounce.utils.PacketUtils.sendPackets
import net.ccbluex.liquidbounce.utils.RotationUtils.keepLength
import net.ccbluex.liquidbounce.utils.extensions.*
import net.ccbluex.liquidbounce.utils.inventory.InventoryUtils.findItem
import net.ccbluex.liquidbounce.utils.inventory.InventoryUtils.serverSlot
import net.ccbluex.liquidbounce.utils.timing.MSTimer
import net.ccbluex.liquidbounce.value.BooleanValue
import net.minecraft.block.BlockAir
import net.minecraft.init.Items.flint_and_steel
import net.minecraft.init.Items.lava_bucket
import net.minecraft.item.ItemBucket
import net.minecraft.network.play.client.C03PacketPlayer.C05PacketPlayerLook
import net.minecraft.network.play.client.C09PacketHeldItemChange
import net.minecraft.util.EnumFacing
import net.minecraft.util.MathHelper.wrapAngleTo180_float
import net.minecraft.util.Vec3
import kotlin.math.atan2
import kotlin.math.sqrt

object Ignite : Module("Ignite", COMBAT) {
    private val lighter by BooleanValue("Lighter", true)
    private val lavaBucket by BooleanValue("Lava", true)

    private val msTimer = MSTimer()

    @EventTarget
    fun onUpdate(event: UpdateEvent?) {
        if (!msTimer.hasTimePassed(500)) return

        mc.thePlayer ?: return
        mc.theWorld ?: return

        val lighterInHotbar = if (lighter) findItem(36, 44, flint_and_steel) else null
        val lavaInHotbar = if (lavaBucket) findItem(26, 44, lava_bucket) else null

        if (lighterInHotbar == null && lavaInHotbar == null) return

        val fireInHotbar = lighterInHotbar ?: lavaInHotbar!!

        for (entity in mc.theWorld.getLoadedEntityList()) {
            if (isSelected(entity, true) && !entity.isBurning) {
                val blockPos = entity.position

                if (mc.thePlayer.getDistanceSq(blockPos) >= 22.3 ||
                    !blockPos.isReplaceable() ||
                    blockPos.getBlock() !is BlockAir
                ) continue

                // Probably used to prevent rotation changes while igniting
                keepLength += 1

                serverSlot = fireInHotbar - 36

                val itemStack = mc.thePlayer.inventoryContainer.getSlot(fireInHotbar).stack

                if (itemStack.item is ItemBucket) {
                    val diffX = blockPos.x + 0.5 - mc.thePlayer.posX
                    val diffY = blockPos.y + 0.5 - (mc.thePlayer.entityBoundingBox.minY + mc.thePlayer.eyeHeight)
                    val diffZ = blockPos.z + 0.5 - mc.thePlayer.posZ
                    val sqrt = sqrt(diffX * diffX + diffZ * diffZ)
                    val yaw = atan2(diffZ, diffX).toDegreesF() - 90f
                    val pitch = -atan2(diffY, sqrt).toDegreesF()

                    sendPacket(
                        C05PacketPlayerLook(
                            mc.thePlayer.rotationYaw + wrapAngleTo180_float(yaw - mc.thePlayer.rotationYaw),
                            mc.thePlayer.rotationPitch + wrapAngleTo180_float(pitch - mc.thePlayer.rotationPitch),
                            mc.thePlayer.onGround
                        )
                    )

                    mc.thePlayer.sendUseItem(itemStack)
                } else {
                    for (side in EnumFacing.entries) {
                        val neighbor = blockPos.offset(side)

                        if (!neighbor.canBeClicked()) continue

                        val diffX = neighbor.x + 0.5 - mc.thePlayer.posX
                        val diffY = neighbor.y + 0.5 -
                                (mc.thePlayer.entityBoundingBox.minY +
                                        mc.thePlayer.getEyeHeight())
                        val diffZ = neighbor.z + 0.5 - mc.thePlayer.posZ
                        val sqrt = sqrt(diffX * diffX + diffZ * diffZ)
                        val yaw = atan2(diffZ, diffX).toDegreesF() - 90f
                        val pitch = -atan2(diffY, sqrt).toDegreesF()

                        sendPacket(
                            C05PacketPlayerLook(
                                mc.thePlayer.rotationYaw + wrapAngleTo180_float(yaw - mc.thePlayer.rotationYaw),
                                mc.thePlayer.rotationPitch + wrapAngleTo180_float(pitch - mc.thePlayer.rotationPitch),
                                mc.thePlayer.onGround
                            )
                        )

                        if (mc.thePlayer.onPlayerRightClick(
                                neighbor, side.opposite,
                                Vec3(side.directionVec), itemStack
                            )
                        ) {
                            mc.thePlayer.swingItem()
                            break
                        }
                    }
                }

                sendPackets(
                    C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem),
                    C05PacketPlayerLook(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, mc.thePlayer.onGround)
                )

                msTimer.reset()
                break
            }
        }
    }
}
