/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.glidemodes.other

import net.ccbluex.liquidbounce.features.module.modules.movement.Glide.neruxVaceTicks
import net.ccbluex.liquidbounce.features.module.modules.movement.glidemodes.GlideMode

/**
 * @author CCBlueX/LiquidBounce
 */
object NeruxVace : GlideMode("NeruxVace") {
    private var tick = 0
    override fun onUpdate() {
        if (!mc.thePlayer.onGround)
            tick++

        if (tick >= neruxVaceTicks && !mc.thePlayer.onGround) {
            tick = 0
            mc.thePlayer.motionY = .015
        }
    }
}
