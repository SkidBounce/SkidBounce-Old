/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement

import net.ccbluex.liquidbounce.event.EventState
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.MotionEvent
import net.ccbluex.liquidbounce.event.WorldEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory.MOVEMENT
import net.ccbluex.liquidbounce.utils.MovementUtils.isMoving
import net.ccbluex.liquidbounce.utils.PacketUtils.sendPacket
import net.ccbluex.liquidbounce.utils.PacketUtils.sendPackets
import net.ccbluex.liquidbounce.utils.extensions.isActuallyPressed
import net.ccbluex.liquidbounce.value.BooleanValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.network.play.client.C0BPacketEntityAction
import net.minecraft.network.play.client.C0BPacketEntityAction.Action.START_SNEAKING
import net.minecraft.network.play.client.C0BPacketEntityAction.Action.STOP_SNEAKING

object Sneak : Module("Sneak", MOVEMENT) {

    val mode by ListValue("Mode", arrayOf("Legit", "Vanilla", "Switch", "MineSecure").sortedArray(), "MineSecure")
    val stopMove by BooleanValue("StopMove", false)

    private var sneaking = false

    @EventTarget
    fun onMotion(event: MotionEvent) {
        if (stopMove && isMoving) {
            if (sneaking)
                onDisable()
            return
        }

        when (mode.lowercase()) {
            "legit" -> mc.gameSettings.keyBindSneak.pressed = true
            "vanilla" -> {
                if (sneaking)
                    return

                sendPacket(C0BPacketEntityAction(mc.thePlayer, START_SNEAKING))
            }

            "switch" -> {
                when (event.eventState) {
                    EventState.PRE -> {
                        sendPackets(
                            C0BPacketEntityAction(mc.thePlayer, START_SNEAKING),
                            C0BPacketEntityAction(mc.thePlayer, STOP_SNEAKING)
                        )
                    }

                    EventState.POST -> {
                        sendPackets(
                            C0BPacketEntityAction(mc.thePlayer, STOP_SNEAKING),
                            C0BPacketEntityAction(mc.thePlayer, START_SNEAKING)
                        )
                    }
                    else -> {}
                }
            }

            "minesecure" -> {
                if (event.eventState == EventState.PRE)
                    return

                sendPacket(C0BPacketEntityAction(mc.thePlayer, START_SNEAKING))
            }
        }
    }

    @EventTarget
    fun onWorld(worldEvent: WorldEvent) {
        sneaking = false
    }

    override fun onDisable() {
        val player = mc.thePlayer ?: return

        when (mode.lowercase()) {
            "legit" -> {
                if (!mc.gameSettings.keyBindSneak.isActuallyPressed)
                    mc.gameSettings.keyBindSneak.pressed = false
            }

            "vanilla", "switch", "minesecure" -> sendPacket(C0BPacketEntityAction(player, STOP_SNEAKING))
        }
        sneaking = false
    }
}
