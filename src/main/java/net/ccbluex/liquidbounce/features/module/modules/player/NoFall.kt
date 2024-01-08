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
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.block.BlockLiquid
import net.minecraft.util.AxisAlignedBB.fromBounds

object NoFall : Module("NoFall", ModuleCategory.PLAYER) {
    private val noFallModes = arrayOf(
        SpoofGround,
        NoGround,
        Packet,
        Packet2,
        Packet3,
        Packet4,
        Packet5,
        MLG,
        Damage,
        AAC,
        AACv4,
        AAC44xFlag,
        LAAC,
        AAC3311,
        AAC3315,
        Spartan,
        CubeCraft,
        Hypixel,
        HypixelFlag,
        HypSpoof,
        Vulcan,
        Medusa,
        Verus,
    )

    private val modes = noFallModes.map { it.modeName }.toTypedArray()

    val mode by ListValue("Mode", modes, "SpoofGround")
    val mlgMinFallDistance by FloatValue("MLG-MinHeight", 5f, 2f..50f, subjective = true) { mode == "MLG" }
    val spoofgroundAlways by BoolValue("SpoofGround-Always", true) { mode == "SpoofGround" }
    val spoofgroundMinFallDistance by FloatValue("SpoofGround-MinFallDistance", 0f, 0f..3f) { mode == "SpoofGround" && !spoofgroundAlways }

    override fun onEnable() {
        modeModule.onEnable()
    }

    override fun onDisable() {
        modeModule.onDisable()
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        val thePlayer = mc.thePlayer

        if (FreeCam.handleEvents()) return

        if (collideBlock(thePlayer.entityBoundingBox) { it is BlockLiquid } || collideBlock(
                fromBounds(
                    thePlayer.entityBoundingBox.maxX,
                    thePlayer.entityBoundingBox.maxY,
                    thePlayer.entityBoundingBox.maxZ,
                    thePlayer.entityBoundingBox.minX,
                    thePlayer.entityBoundingBox.minY - 0.01,
                    thePlayer.entityBoundingBox.minZ
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
        val thePlayer = mc.thePlayer

        if (collideBlock(thePlayer.entityBoundingBox) { it is BlockLiquid }
            || collideBlock(
                fromBounds(
                    thePlayer.entityBoundingBox.maxX,
                    thePlayer.entityBoundingBox.maxY,
                    thePlayer.entityBoundingBox.maxZ,
                    thePlayer.entityBoundingBox.minX,
                    thePlayer.entityBoundingBox.minY - 0.01,
                    thePlayer.entityBoundingBox.minZ
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