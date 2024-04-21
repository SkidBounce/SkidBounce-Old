/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.events.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory.MOVEMENT
import net.ccbluex.liquidbounce.utils.PacketUtils
import net.ccbluex.liquidbounce.utils.extensions.getBlock
import net.ccbluex.liquidbounce.value.*
import net.minecraft.init.Blocks.*
import net.minecraft.network.play.client.C07PacketPlayerDigging
import net.minecraft.util.*

object NoFluid : Module("NoFluid", MOVEMENT) {

    val Water by BooleanValue("Water", true)
    val Lava by BooleanValue("Lava", true)
    private val mode by ListValue("Mode", arrayOf("Grim", "Vanilla"), "Vanilla")
    private val grimExpand by FloatValue("GrimExpand", 0.25F, 0F..1F) { mode == "Grim" }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (mode != "Grim" || mc.thePlayer == null) return

        val blocks: MutableSet<BlockPos> = mutableSetOf()

        for (x in 3 downTo -3) {
            for (y in 3 downTo -3) {
                for (z in 3 downTo -3) {

                    val pos = BlockPos(
                        mc.thePlayer.posX.toInt() + x,
                        mc.thePlayer.posY.toInt() + y,
                        mc.thePlayer.posZ.toInt() + z
                    )
                    when (pos.getBlock()) {
                        flowing_lava, lava -> if (!Lava) continue
                        flowing_water, water -> if (!Water) continue
                        else -> continue
                    }

                    if (pos.x > mc.thePlayer.entityBoundingBox.minX - grimExpand - 1 &&
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
            PacketUtils.sendPacket(
                C07PacketPlayerDigging(
                    C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK,
                    it,
                    EnumFacing.DOWN
                )
            )
        }
    }

    override val tag
        get() = mode
}
