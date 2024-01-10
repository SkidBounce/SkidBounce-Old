/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.player

import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.aac.*
import net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.other.*
import net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.vanilla.*
import net.ccbluex.liquidbounce.features.module.modules.render.FreeCam
import net.ccbluex.liquidbounce.utils.block.BlockUtils.collideBlock
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.block.BlockLiquid
import net.minecraft.util.AxisAlignedBB.fromBounds

object NoFall : Module("NoFall", ModuleCategory.PLAYER) {
    private val noFallModes = arrayOf(
        SpoofGround,
        NoGround,
        NoPacket,
        Packet,
        Packet2,
        Packet3,
        Packet4,
        Motion,
        MLG,
        Damage,
        Phase,

        AAC,
        AACv4,
        AAC44xFlag,
        LAAC,
        AAC3311,
        AAC3315,
        AAC504,
        AAC5014,

        Matrix62x,
        Spartan,
        CubeCraft,
        Hypixel,
        HypixelFlag,
        HypSpoof,
        Vulcan,
        Medusa,
        Medusa2,
        Verus,
    )

    private val modes = noFallModes.map { it.modeName }.toTypedArray()

    val mode by ListValue("Mode", modes, "SpoofGround")
    val mlgMinFallDistance by FloatValue("MLG-MinHeight", 5f, 2f..50f) { mode == "MLG" }
    val spoofgroundAlways by BoolValue("SpoofGround-Always", true) { mode == "SpoofGround" }
    val spoofgroundMinFallDistance by FloatValue("SpoofGround-MinFallDistance", 0f, 0f..3f) { mode == "SpoofGround" && !spoofgroundAlways }
    val motionMotion by FloatValue("Motion-Motion", -0.01f, -5f..5f) { mode == "Motion" }
    val phaseOffset by IntegerValue("Phase-Offset", 1, 0..5) { mode == "Phase" }
    val verusMulti by FloatValue("Verus-XZMulti", 0.6f, 0f..1f) { mode == "Verus" }

    override fun onEnable() {
        modeModule.onEnable()
    }

    override fun onDisable() {
        modeModule.onDisable()
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        mc.thePlayer ?: return

        if (FreeCam.handleEvents()) return

        if (collideBlock(mc.thePlayer.entityBoundingBox) { it is BlockLiquid } || collideBlock(
                fromBounds(
                    mc.thePlayer.entityBoundingBox.maxX,
                    mc.thePlayer.entityBoundingBox.maxY,
                    mc.thePlayer.entityBoundingBox.maxZ,
                    mc.thePlayer.entityBoundingBox.minX,
                    mc.thePlayer.entityBoundingBox.minY - 0.01,
                    mc.thePlayer.entityBoundingBox.minZ
                )
            ) { it is BlockLiquid }
        ) return

        modeModule.onUpdate()
    }

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        modeModule.onRender3D(event)
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        mc.thePlayer ?: return

        modeModule.onPacket(event)
    }

    @EventTarget
    fun onBB(event: BlockBBEvent) {
        mc.thePlayer ?: return

        modeModule.onBB(event)
    }

    // Ignore condition used in LAAC mode
    @EventTarget(ignoreCondition = true)
    fun onJump(event: JumpEvent) {
        modeModule.onJump(event)
    }

    @EventTarget
    fun onStep(event: StepEvent) {
        modeModule.onStep(event)
    }

    @EventTarget
    fun onMotion(event: MotionEvent) {
        modeModule.onMotion(event)
    }

    @EventTarget
    fun onMove(event: MoveEvent) {
        mc.thePlayer ?: return

        if (collideBlock(mc.thePlayer.entityBoundingBox) { it is BlockLiquid }
            || collideBlock(
                fromBounds(
                    mc.thePlayer.entityBoundingBox.maxX,
                    mc.thePlayer.entityBoundingBox.maxY,
                    mc.thePlayer.entityBoundingBox.maxZ,
                    mc.thePlayer.entityBoundingBox.minX,
                    mc.thePlayer.entityBoundingBox.minY - 0.01,
                    mc.thePlayer.entityBoundingBox.minZ
                )
            ) { it is BlockLiquid }
        ) return

        modeModule.onMove(event)
    }

    override val tag
        get() = mode

    private val modeModule
        get() = noFallModes.find { it.modeName == mode }!!
}