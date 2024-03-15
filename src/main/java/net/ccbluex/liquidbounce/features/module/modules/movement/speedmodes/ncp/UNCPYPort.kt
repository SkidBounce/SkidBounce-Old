/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.ncp

import net.ccbluex.liquidbounce.event.MotionEvent
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.Speed.uncpyportDamageBoost
import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.SpeedMode
import net.ccbluex.liquidbounce.utils.MovementUtils.isMoving
import net.ccbluex.liquidbounce.utils.MovementUtils.speed
import net.ccbluex.liquidbounce.utils.MovementUtils.strafe
import net.ccbluex.liquidbounce.utils.extensions.jump
import net.minecraft.network.play.server.S12PacketEntityVelocity

/**
 * @author ManInMyVan/SkidBounce
 * @author ManInMyVan
 */
object UNCPYPort : SpeedMode("UNCPYPort") {
    override fun onMotion(event: MotionEvent) {
        mc.thePlayer.jumpMovementFactor = 0.0254f
        mc.timer.timerSpeed = if (mc.thePlayer.motionY < 0.0 && !mc.thePlayer.onGround && isMoving) 1.1675f else 1f

        if (mc.thePlayer.motionY < 0.0 && mc.thePlayer.motionY > -0.1 && isMoving)
            mc.thePlayer.motionY -= 0.16

        if (mc.thePlayer.onGround && isMoving) {
            mc.thePlayer.jump(0.3993535)
        }
        speed = speed.coerceAtMost(1.75f)
        strafe(speed, true)
    }

    override fun onPacket(event: PacketEvent) {
        if (uncpyportDamageBoost && event.packet is S12PacketEntityVelocity && event.packet.entityID == mc.thePlayer.entityId)
            speed *= 2f
    }
}
