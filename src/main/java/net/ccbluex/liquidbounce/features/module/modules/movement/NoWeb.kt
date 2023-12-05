/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.modules.movement.nowebmodes.aac.*
import net.ccbluex.liquidbounce.features.module.modules.movement.nowebmodes.other.*
import net.ccbluex.liquidbounce.value.ListValue
import net.ccbluex.liquidbounce.value.FloatValue

object NoWeb : Module("NoWeb", ModuleCategory.MOVEMENT) {

    private val noWebModes = arrayOf(
        // Vanilla
        Vanilla,

        // AAC
        AAC,
        AAC4,
        AAC5,
        OldAAC,
        LAAC,

        // Other
        FastFall,
        Horizon,
        Matrix,
        MineBlaze,
        Rewinside,
        Spartan

    )

    private val modes = noWebModes.map { it.modeName }.toTypedArray()

    val mode by ListValue(
        "Mode", modes, "Vanilla"
    )

    val horizonSpeed by FloatValue("HorizonSpeed", 0.1F, 0.01F..0.8F) { mode == "Horizon" }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        modeModule.onUpdate()
    }
    override fun onDisable() {
        mc.timer.timerSpeed = 1.0F
    }
    override val tag
        get() = mode

    private val modeModule
        get() = noWebModes.find { it.modeName == mode }!!
}
