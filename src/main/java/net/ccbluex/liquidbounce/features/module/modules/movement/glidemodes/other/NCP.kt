/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.glidemodes.other

import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.Glide.ncpMotion
import net.ccbluex.liquidbounce.features.module.modules.movement.glidemodes.GlideMode
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.PacketUtils
import net.ccbluex.liquidbounce.utils.extensions.component1
import net.ccbluex.liquidbounce.utils.extensions.component2
import net.ccbluex.liquidbounce.utils.extensions.component3
import net.minecraft.network.play.client.C03PacketPlayer

/**
 * @author CCBlueX/LiquidBounce
 */
object NCP : GlideMode("NCP") {
	override fun onEnable() {
		if (!mc.thePlayer.onGround) return

		val (x, y, z) = mc.thePlayer

		repeat(65) {
            PacketUtils.sendPackets(
                C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.049, z, false),
                C03PacketPlayer.C04PacketPlayerPosition(x, y, z, false)
            )
		}

        PacketUtils.sendPacket(C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.1, z, true))

		mc.thePlayer.motionX *= 0.1
		mc.thePlayer.motionZ *= 0.1
		mc.thePlayer.swingItem()
	}

	override fun onUpdate() {
		mc.thePlayer.motionY =
			if (mc.gameSettings.keyBindSneak.isKeyDown) -0.5 else -ncpMotion.toDouble()

        MovementUtils.strafe()
	}

	override fun onPacket(event: PacketEvent) {
		if (event.packet is C03PacketPlayer)
			event.packet.onGround = true
	}
}
