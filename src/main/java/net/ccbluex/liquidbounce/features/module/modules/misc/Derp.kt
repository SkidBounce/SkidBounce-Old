/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.misc

import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory.MISC
import net.ccbluex.liquidbounce.utils.Rotation
import net.ccbluex.liquidbounce.utils.misc.RandomUtils.nextFloat
import net.ccbluex.liquidbounce.value.BooleanValue
import net.ccbluex.liquidbounce.value.FloatValue

object Derp : Module("Derp", MISC) {

    private val headless by BooleanValue("Headless", false)
    private val spinny by BooleanValue("Spinny", false)
    private val increment by FloatValue("Increment", 1F, 0F..50F) { spinny }

    private var currentSpin = 0F

    val rotation: Rotation
        get() {
            val rot = Rotation(mc.thePlayer.rotationYaw + nextFloat(-180f, 180f), nextFloat(-90f, 90f))

            if (headless)
                rot.pitch = 180F

            if (spinny) {
                currentSpin += increment
                rot.yaw = currentSpin
            }

            rot.fixedSensitivity()

            return rot
        }

}
