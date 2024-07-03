/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement

import net.minecraft.init.Blocks.lava as stationary_lava
import net.minecraft.init.Blocks.water as stationary_water
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.events.BlockCollideEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory.MOVEMENT
import net.ccbluex.liquidbounce.utils.PacketUtils.sendPacket
import net.ccbluex.liquidbounce.value.BooleanValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.init.Blocks.flowing_lava
import net.minecraft.init.Blocks.flowing_water
import net.minecraft.network.play.client.C07PacketPlayerDigging
import net.minecraft.network.play.client.C07PacketPlayerDigging.Action.START_DESTROY_BLOCK
import net.minecraft.network.play.client.C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK
import net.minecraft.util.EnumFacing.DOWN

object NoFluid : Module("NoFluid", MOVEMENT) {
    val water by BooleanValue("Water", true)
    val lava by BooleanValue("Lava", true)
    private val mode by ListValue("Mode", arrayOf("Grim", "Vanilla"), "Vanilla")
    private val grimStrict by BooleanValue("GrimStrict", true) { mode == "Grim" }
    private val grimBreakOnWorld by BooleanValue("GrimBreakOnWorld", false) { mode == "Grim" }

    @EventTarget
    fun onCollide(event: BlockCollideEvent) {
        if (mode != "Grim" || mc.thePlayer == null) return

        when (event.blockState.block) {
            stationary_lava, flowing_lava -> if (!lava) return
            stationary_water, flowing_water -> if (!water) return
            else -> return
        }

        if (grimStrict) sendPacket(C07PacketPlayerDigging(START_DESTROY_BLOCK, event.blockPos, DOWN))
        if (grimBreakOnWorld) mc.theWorld.setBlockToAir(event.blockPos)

        sendPacket(C07PacketPlayerDigging(STOP_DESTROY_BLOCK, event.blockPos, DOWN))
    }

    override val tag
        get() = mode
}
