/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.events.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory.MOVEMENT
import net.ccbluex.liquidbounce.features.module.modules.movement.jesusmodes.JesusMode
import net.ccbluex.liquidbounce.utils.ClassUtils.getAllObjects
import net.ccbluex.liquidbounce.utils.block.BlockUtils.collideBlock
import net.ccbluex.liquidbounce.utils.block.BlockUtils.getBlock
import net.ccbluex.liquidbounce.utils.extensions.component1
import net.ccbluex.liquidbounce.utils.extensions.component2
import net.ccbluex.liquidbounce.utils.extensions.component3
import net.ccbluex.liquidbounce.utils.extensions.isInsideOf
import net.ccbluex.liquidbounce.value.BooleanValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.block.BlockLiquid
import net.minecraft.block.material.Material.air
import net.minecraft.util.AxisAlignedBB.fromBounds
import net.minecraft.util.BlockPos

object Jesus : Module("Jesus", MOVEMENT) {
    private val jesusModes = javaClass.`package`.getAllObjects<JesusMode>().sortedBy { it.modeName }

    val mode by ListValue("Mode", jesusModes.map { it.modeName }.toTypedArray(), "Vanilla")

    val aacFly by FloatValue("AACFlyMotion", 0.5f, 0.1f..1f) { mode == "AACFly" }

    private val noJump by BooleanValue("NoJump", false)

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        val thePlayer = mc.thePlayer ?: return

        if (thePlayer.isSneaking) return

        if (modeModule.solid && collideBlock(mc.thePlayer.entityBoundingBox) { it is BlockLiquid } && mc.thePlayer isInsideOf air && !mc.thePlayer.isSneaking)
            mc.thePlayer.motionY = 0.08

        modeModule.onUpdate()
    }

    @EventTarget
    fun onMove(event: MoveEvent) = modeModule.onMove(event)

    @EventTarget
    fun onPacket(event: PacketEvent) = modeModule.onPacket(event)

    @EventTarget
    fun onBlockBB(event: BlockBBEvent) {
        mc.thePlayer ?: return

        if (!modeModule.solid
            || event.block !is BlockLiquid
            || collideBlock(mc.thePlayer.entityBoundingBox) { it is BlockLiquid }
            || mc.thePlayer.isSneaking
            ) return

        val (x, y, z) = event

        event.boundingBox = fromBounds(
            x.toDouble(),
            y.toDouble(),
            z.toDouble(),
            x + 1.0,
            y + 1.0,
            z + 1.0
        )
    }

    @EventTarget
    fun onJump(event: JumpEvent) {
        val (x, y, z) = mc.thePlayer ?: return
        if (noJump && getBlock(BlockPos(x, y - 0.01, z)) is BlockLiquid)
            event.cancelEvent()
    }

    private val modeModule
        get() = jesusModes.find { it.modeName == mode }!!

    override val tag
        get() = mode
}
