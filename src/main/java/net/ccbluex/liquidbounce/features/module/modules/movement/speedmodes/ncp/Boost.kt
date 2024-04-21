/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.ncp

import net.ccbluex.liquidbounce.event.events.MotionEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.SpeedMode
import net.ccbluex.liquidbounce.utils.MovementUtils.isMoving

/**
 * @author CCBlueX/LiquidBounce
 */
object Boost : SpeedMode("Boost", true) {
    private var motionDelay = 0
    private var ground = 0f
    override fun onMotion(event: MotionEvent) {
        var speed = 3.1981
        var offset = 4.69
        var shouldOffset = true

        if (mc.theWorld.getCollidingBoundingBoxes(
                mc.thePlayer,
                mc.thePlayer.entityBoundingBox.offset(
                    mc.thePlayer.motionX / offset,
                    0.0,
                    mc.thePlayer.motionZ / offset
                )
            ).isNotEmpty()
        ) {
            shouldOffset = false
        }

        if (mc.thePlayer.onGround && ground < 1f)
            ground += 0.2f
        if (!mc.thePlayer.onGround)
            ground = 0f

        if (ground == 1f && shouldSpeedUp()) {
            if (!mc.thePlayer.isSprinting)
                offset += 0.8

            if (mc.thePlayer.moveStrafing != 0f) {
                speed -= 0.1
                offset += 0.5
            }
            if (mc.thePlayer.isInWater)
                speed -= 0.1


            motionDelay += 1
            when (motionDelay) {
                1 -> {
                    mc.thePlayer.motionX *= speed
                    mc.thePlayer.motionZ *= speed
                }

                2 -> {
                    mc.thePlayer.motionX /= 1.458
                    mc.thePlayer.motionZ /= 1.458
                }

                4 -> {
                    if (shouldOffset) mc.thePlayer.setPosition(
                        mc.thePlayer.posX + mc.thePlayer.motionX / offset,
                        mc.thePlayer.posY,
                        mc.thePlayer.posZ + mc.thePlayer.motionZ / offset
                    )
                    motionDelay = 0
                }
            }
        }
    }


    private fun shouldSpeedUp() =
        !mc.thePlayer.isInLava && !mc.thePlayer.isOnLadder && !mc.thePlayer.isSneaking && isMoving
}
