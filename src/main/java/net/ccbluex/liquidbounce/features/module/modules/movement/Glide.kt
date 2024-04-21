/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement

import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory.MOVEMENT
import net.ccbluex.liquidbounce.features.module.modules.movement.glidemodes.GlideMode
import net.ccbluex.liquidbounce.utils.ClassUtils.getAllObjects
import net.ccbluex.liquidbounce.utils.extensions.resetSpeed
import net.ccbluex.liquidbounce.utils.extensions.stopXZ
import net.ccbluex.liquidbounce.utils.render.RenderUtils.drawPlatform
import net.ccbluex.liquidbounce.value.BooleanValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntValue
import net.ccbluex.liquidbounce.value.ListValue
import java.awt.Color

object Glide : Module("Glide", MOVEMENT) {
    private val glideModes = javaClass.`package`.getAllObjects<GlideMode>().sortedBy { it.modeName }

    private val modes = glideModes.map { it.modeName }.toTypedArray()

    val mode by ListValue("Mode", modes, "Vulcan")

    val ncpMotion by FloatValue("NCP-Motion", 0f, 0f..1f) { mode == "NCP" }
    val neruxVaceTicks by IntValue("NeruxVace-Ticks", 6, 2..20) { mode == "NeruxVace" }
    private val mark by BooleanValue("Mark", true, subjective = true)

    private var startY = 0.0

    override fun onEnable() {
        mc.thePlayer ?: return

        startY = mc.thePlayer.posY

        modeModule.onEnable()
    }

    override fun onDisable() {
        mc.thePlayer ?: return

        if (mode in arrayOf("CubeCraft")) mc.thePlayer.stopXZ()

        mc.timer.resetSpeed()
        mc.thePlayer.speedInAir = 0.02f

        modeModule.onDisable()
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        modeModule.onUpdate()
    }

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        if (!mark)
            return

        val y = startY + 2.0
        drawPlatform(
            y,
            if (mc.thePlayer.entityBoundingBox.maxY < y) Color(0, 255, 0, 90) else Color(255, 0, 0, 90),
            1.0
        )

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

    @EventTarget
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
        modeModule.onMove(event)
    }

    override val tag
        get() = mode

    private val modeModule
        get() = glideModes.find { it.modeName == mode }!!
}
