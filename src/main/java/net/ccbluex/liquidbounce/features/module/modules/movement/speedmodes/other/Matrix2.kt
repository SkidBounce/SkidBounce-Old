/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.other

import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.SpeedMode
import net.ccbluex.liquidbounce.utils.MovementUtils.isMoving
import net.ccbluex.liquidbounce.utils.MovementUtils.strafe
import net.ccbluex.liquidbounce.utils.PacketUtils.sendPacket
import net.ccbluex.liquidbounce.utils.extensions.jmp
import net.minecraft.network.play.client.C0BPacketEntityAction

/**
 * @author CCBlueX/LiquidBounce
 * @author EclipsesDev
 */
object Matrix2 : SpeedMode("Matrix2") {
    override fun onUpdate() {
        if (mc.thePlayer.isInWater || mc.thePlayer.isInLava || mc.thePlayer.isInWeb || mc.thePlayer.isOnLadder)
            return

        if (isMoving) {
            if (mc.thePlayer.onGround) {
                mc.thePlayer.jmp()
                mc.timer.timerSpeed = 0.525f
                strafe()

                sendPacket(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING))
            } else {
                mc.timer.timerSpeed = 1.075f

                sendPacket(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING))
            }

            mc.thePlayer.speedInAir = if (mc.thePlayer.fallDistance <= 0.8 && mc.thePlayer.moveStrafing == 0f)
                0.02035f else 0.02f

        } else mc.timer.timerSpeed = 1f
    }

    override fun onDisable() {
        mc.thePlayer.speedInAir = 0.02f
        mc.timer.timerSpeed = 1f
    }
}
