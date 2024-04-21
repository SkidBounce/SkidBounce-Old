/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.aac

import net.ccbluex.liquidbounce.event.events.MoveEvent
import net.ccbluex.liquidbounce.event.events.PacketEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.SpeedMode
import net.ccbluex.liquidbounce.utils.MovementUtils.strafe
import net.ccbluex.liquidbounce.utils.MovementUtils.isMoving
import net.ccbluex.liquidbounce.utils.MovementUtils.baseMoveSpeed
import net.ccbluex.liquidbounce.utils.MovementUtils.getJumpBoostModifier
import net.minecraft.client.settings.GameSettings.isKeyDown
import net.minecraft.network.play.server.S12PacketEntityVelocity

/**
 * @author liquidbounceplusreborn/LiquidbouncePlus-Reborn
 */
object AAC3310 : SpeedMode("AAC3.3.10") {
    override fun onMove(event: MoveEvent) {
        val player = mc.thePlayer
        mc.gameSettings.keyBindJump.pressed = false
        strafe(baseMoveSpeed * 1.0164f)
        if (mc.thePlayer.onGround && mc.thePlayer.isCollidedVertically && isKeyDown(mc.gameSettings.keyBindSneak)) {
            player.motionY = getJumpBoostModifier(0.41999998688697815)
            event.y = player.motionY
        }
        if (mc.thePlayer.onGround && mc.thePlayer.isCollidedVertically && !isKeyDown(mc.gameSettings.keyBindSneak)) {
            player.motionY = getJumpBoostModifier(0.41999998688697815)
            event.y = player.motionY
        }
    }

    override fun onPacket(event: PacketEvent) {
        if (event.packet is S12PacketEntityVelocity) {
            if (mc.thePlayer.onGround && mc.thePlayer.isSneaking && isMoving) return
            event.cancelEvent()
        }
    }
}
