/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.matrix

import net.ccbluex.liquidbounce.features.module.modules.movement.Speed.matrix2Fast
import net.ccbluex.liquidbounce.features.module.modules.movement.Speed.matrix2SprintBypass
import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.SpeedMode
import net.ccbluex.liquidbounce.utils.MovementUtils.isMoving
import net.ccbluex.liquidbounce.utils.MovementUtils.strafe
import net.ccbluex.liquidbounce.utils.PacketUtils.sendPacket
import net.ccbluex.liquidbounce.utils.extensions.jmp
import net.minecraft.network.play.client.C0BPacketEntityAction
import net.minecraft.network.play.client.C0BPacketEntityAction.Action.STOP_SPRINTING
import net.minecraft.network.play.client.C0BPacketEntityAction.Action.START_SPRINTING

/**
 * @author CCBlueX/LiquidBounce
 * @author EclipsesDev
 */
object Matrix2 : SpeedMode("Matrix2") {
    override fun onUpdate() {
        if (isMoving) {
            if (mc.thePlayer.isAirBorne && mc.thePlayer.fallDistance > 2) {
                onDisable()
                return
            }

            if (mc.thePlayer.onGround) {
                mc.thePlayer.jmp()
                mc.timer.timerSpeed = if (matrix2Fast) 0.5195f else 0.525f
                strafe()

                if (matrix2SprintBypass) sendPacket(C0BPacketEntityAction(mc.thePlayer, STOP_SPRINTING))
            } else {
                mc.timer.timerSpeed = if (matrix2Fast) 1.0973f else 1.075f

                if (matrix2SprintBypass) sendPacket(C0BPacketEntityAction(mc.thePlayer, START_SPRINTING))
            }

            mc.thePlayer.speedInAir = if (mc.thePlayer.fallDistance <= (if (matrix2Fast) 0.4 else 0.8) && mc.thePlayer.moveStrafing == 0f)
                0.02035f else 0.02f

        } else mc.timer.timerSpeed = 1f
    }

    override fun onDisable() {
        mc.thePlayer.speedInAir = 0.02f
        mc.timer.timerSpeed = 1f
    }
}
