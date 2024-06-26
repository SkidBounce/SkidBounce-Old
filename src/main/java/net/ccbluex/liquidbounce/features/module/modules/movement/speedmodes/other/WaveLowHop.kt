/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.other

import net.ccbluex.liquidbounce.features.module.modules.movement.Speed.wavelowhopTimer
import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.SpeedMode
import net.ccbluex.liquidbounce.utils.MovementUtils.isMoving
import net.ccbluex.liquidbounce.utils.MovementUtils.strafe
import net.ccbluex.liquidbounce.utils.extensions.jmp
import net.ccbluex.liquidbounce.utils.extensions.resetSpeed
import net.ccbluex.liquidbounce.utils.extensions.stopXZ
import net.minecraft.potion.Potion.moveSpeed

/**
 * @author ManInMyVan/SkidBounce
 * @author ManInMyVan
 */
object WaveLowHop : SpeedMode("WaveLowHop") {
    private var wasGround = false

    override fun onUpdate() {
        if (!isMoving) {
            mc.thePlayer.stopXZ()
            mc.timer.resetSpeed()
            return
        }

        mc.timer.timerSpeed = wavelowhopTimer

        if (mc.thePlayer.onGround) {
            wasGround = true
            mc.thePlayer.jmp(ignoreJumpBoost = true)
        } else if (wasGround) {
            wasGround = false
            mc.thePlayer.motionY = 0.0
        }
        strafe(
            if (mc.thePlayer.onGround) {
                if (hasSpeed) 1.2f else 0.49f
            } else if (hasSpeed) 1.32f else 0.588f
        )
    }

    override fun onDisable() {
        strafe(if (hasSpeed) 1.2f else 0.49f)
    }

    private val hasSpeed
        get() = mc.thePlayer.isPotionActive(moveSpeed)
}
