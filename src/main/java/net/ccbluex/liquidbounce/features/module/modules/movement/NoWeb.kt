/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement

import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.modules.movement.nowebmodes.aac.*
import net.ccbluex.liquidbounce.features.module.modules.movement.nowebmodes.other.*
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.ListValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue

object NoWeb : Module("NoWeb", ModuleCategory.MOVEMENT) {

    private val noWebModes = arrayOf(
        Vanilla,
        Custom,

        // AAC
        AAC,
        AACv4,
        AAC4,
        AAC5,
        OldAAC,
        LAAC,

        // Other
        Cardinal,
        FastFall,
        Grim,
        Horizon,
        IntaveTest,
        Matrix,
        MineBlaze,
        Negativity,
        Rewinside,
        Spartan,
        Test
    ).sortedBy { it.modeName }

    private val modes = noWebModes.map { it.modeName }.toTypedArray()

    val mode by ListValue(
        "Mode", modes, "Vanilla"
    )

    val horizonSpeed by FloatValue("HorizonSpeed", 0.1F, 0.01F..0.8F) { mode == "Horizon" }
    val customFloat by BoolValue("CustomFloat", true) { mode == "Custom" }
    val customUpSpeed by FloatValue("CustomUpSpeed", 1F, 0F..3F) { mode == "Custom" && customFloat }
    val customDownSpeed by FloatValue("CustomDownSpeed", 1F, 0F..3F) { mode == "Custom" && customFloat }
    val customSpeed by FloatValue("CustomSpeed", 1.1F, 0.1F..1.16F) { mode == "Custom" }
    val grimExpand by FloatValue("GrimExpand", 0.25F, 0F..1F) { mode == "Grim" }

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
