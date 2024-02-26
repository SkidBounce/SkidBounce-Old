/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.player

import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.*
import net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.aac.*
import net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.hypixel.*
import net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.matrix.*
import net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.other.*
import net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.vanilla.*
import net.ccbluex.liquidbounce.features.module.modules.player.nofallmodes.vulcan.*
import net.ccbluex.liquidbounce.features.module.modules.render.FreeCam
import net.ccbluex.liquidbounce.utils.MovementUtils.aboveVoid
import net.ccbluex.liquidbounce.utils.block.BlockUtils.collideBlock
import net.ccbluex.liquidbounce.utils.extensions.resetSpeed
import net.ccbluex.liquidbounce.value.*
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
        Cancel,
        Damage,
        Phase,
        Less,

        OldAAC,
        AAC4,
        AAC442,
        AAC44xFlag,
        LAAC,
        AAC3311,
        AAC3315,
        AAC504,
        AAC5014,

        Matrix62x,
        Matrix62xPacket,
        Matrix663,
        Matrix6632,
        MatrixCollide,
        MatrixNew,
        OldMatrix,

        Vulcan,
        Vulcan2,
        OldVulcan,

        Hypixel,
        Hypixel2,
        Hypixel3,
        HypixelFlag,
        HypixelBlink,
        HypSpoof,

        Cardinal,
        OldSpartan,
        CubeCraft,
        Medusa,
        Medusa2,
        Verus,
    ).sortedBy { it.modeName }

    val mode by ListValue("Mode", noFallModes.map { it.modeName }.toTypedArray(), "SpoofGround")

    private val noVoid by BoolValue("NoVoid", false)
    val mlgMinFallDistance by FloatValue("MLG-MinHeight", 5f, 2f..50f) { mode == "MLG" }
    val spoofgroundAlways by BoolValue("SpoofGround-Always", true) { mode == "SpoofGround" }
    val spoofgroundMinFallDistance by FloatValue("SpoofGround-MinFallDistance", 0f, 0f..3f) { mode == "SpoofGround" && !spoofgroundAlways }
    val motionMotion by FloatValue("Motion-Motion", -0.01f, -5f..5f) { mode == "Motion" }
    val phaseOffset by IntegerValue("Phase-Offset", 1, 0..5) { mode == "Phase" }
    val verusMulti by FloatValue("Verus-XZMulti", 0.6f, 0f..1f) { mode == "Verus" }
    val vulcan2Motion by FloatValue("Vulcan2-Motion", 0.35f, 0f..10f) { mode == "Vulcan2" }
    val aac5014NightX by BoolValue("AAC5.0.14-NightX", false) { mode == "AAC5.0.14" }
    val matrix6632Safe by BoolValue("Matrix6.6.3-2-Safe", false) { mode == "Matrix6.6.3-2" }

    override fun onEnable() {
        mc.timer.resetSpeed()
        modeModule.onEnable()
    }

    override fun onDisable() {
        mc.timer.resetSpeed()
        modeModule.onDisable()
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        mc.thePlayer ?: return

        if (void || FreeCam.handleEvents()) return

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
    fun onPacket(event: PacketEvent) {
        mc.thePlayer ?: return
        if (void) return

        modeModule.onPacket(event)
    }

    // Ignore condition used in LAAC mode
    @EventTarget(ignoreCondition = true)
    fun onJump(event: JumpEvent) {
        modeModule.onJump(event)
    }

    @EventTarget
    fun onMotion(event: MotionEvent) {
        if (void) return
        modeModule.onMotion(event)
    }

    @EventTarget
    fun onMove(event: MoveEvent) {
        if (void) return
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

    private val void
        get() = noVoid && aboveVoid

    private val modeModule
        get() = noFallModes.find { it.modeName == mode }!!
}
