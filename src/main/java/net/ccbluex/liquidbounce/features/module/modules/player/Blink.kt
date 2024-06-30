/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.player

import net.ccbluex.liquidbounce.event.EventState.POST
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.events.MotionEvent
import net.ccbluex.liquidbounce.event.events.PacketEvent
import net.ccbluex.liquidbounce.event.events.Render3DEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory.PLAYER
import net.ccbluex.liquidbounce.features.module.modules.render.Breadcrumbs
import net.ccbluex.liquidbounce.utils.BlinkUtils
import net.ccbluex.liquidbounce.utils.render.ColorUtils.rainbow
import net.ccbluex.liquidbounce.utils.render.RenderUtils.glColor
import net.ccbluex.liquidbounce.utils.timing.MSTimer
import net.ccbluex.liquidbounce.value.BooleanValue
import net.ccbluex.liquidbounce.value.IntValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.network.handshake.client.C00Handshake
import net.minecraft.network.play.server.S02PacketChat
import net.minecraft.network.play.server.S40PacketDisconnect
import net.minecraft.network.status.client.C00PacketServerQuery
import net.minecraft.network.status.client.C01PacketPing
import org.lwjgl.opengl.GL11.*
import java.awt.Color

object Blink : Module("Blink", PLAYER, gameDetecting = false) {

    private val mode by ListValue("Mode", arrayOf("Sent", "Received", "Both"), "Sent")

    private val pulse by BooleanValue("Pulse", false)
    private val pulseDelay by IntValue("PulseDelay", 1000, 500..5000) { pulse }

    private val fakePlayerMenu by BooleanValue("FakePlayer", true)

    private val pulseTimer = MSTimer()

    override fun onEnable() {
        pulseTimer.reset()

        if (fakePlayerMenu) BlinkUtils.addFakePlayer()
    }

    override fun onDisable() {
        if (mc.thePlayer == null)
            return

        BlinkUtils.unblink()
    }

    @EventTarget(priority = -1)
    fun onPacket(event: PacketEvent) {
        val packet = event.packet

        if (mc.thePlayer == null || mc.thePlayer.isDead)
            return

        if (event.isCancelled)
            return

        when (packet) {
            is C00Handshake, is C00PacketServerQuery, is C01PacketPing, is S02PacketChat, is S40PacketDisconnect -> {
                return
            }
        }

        when (mode) {
            "Sent" -> BlinkUtils.blink(packet, event, sent = true, receive = false)
            "Received" -> BlinkUtils.blink(packet, event, sent = false, receive = true)
            "Both" -> BlinkUtils.blink(packet, event, sent = true, receive = true)
        }
    }

    @EventTarget
    fun onMotion(event: MotionEvent) {
        if (event.eventState == POST) {
            val thePlayer = mc.thePlayer ?: return

            if (thePlayer.isDead || mc.thePlayer.ticksExisted <= 10) {
                BlinkUtils.unblink()
            }

            when (mode.lowercase()) {
                "sent" -> BlinkUtils.syncSent()
                "received" -> BlinkUtils.syncReceived()
            }

            if (pulse && pulseTimer.hasTimePassed(pulseDelay)) {
                BlinkUtils.unblink()
                if (fakePlayerMenu) BlinkUtils.addFakePlayer()

                pulseTimer.reset()
            }
        }
    }

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        val color =
            if (Breadcrumbs.colorRainbow) rainbow()
            else Color(Breadcrumbs.colorRed, Breadcrumbs.colorGreen, Breadcrumbs.colorBlue)

        synchronized(BlinkUtils.positions) {
            glPushMatrix()
            glDisable(GL_TEXTURE_2D)
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
            glEnable(GL_LINE_SMOOTH)
            glEnable(GL_BLEND)
            glDisable(GL_DEPTH_TEST)
            mc.entityRenderer.disableLightmap()
            glBegin(GL_LINE_STRIP)
            glColor(color)

            val renderPosX = mc.renderManager.viewerPosX
            val renderPosY = mc.renderManager.viewerPosY
            val renderPosZ = mc.renderManager.viewerPosZ

            for (pos in BlinkUtils.positions)
                glVertex3d(pos.xCoord - renderPosX, pos.yCoord - renderPosY, pos.zCoord - renderPosZ)

            glColor4d(1.0, 1.0, 1.0, 1.0)
            glEnd()
            glEnable(GL_DEPTH_TEST)
            glDisable(GL_LINE_SMOOTH)
            glDisable(GL_BLEND)
            glEnable(GL_TEXTURE_2D)
            glPopMatrix()
        }
    }

    override val tag
        get() = (BlinkUtils.packets.size + BlinkUtils.packetsReceived.size).toString()

    fun blinkingSend() = handleEvents() && (mode == "Sent" || mode == "Both")
    fun blinkingReceive() = handleEvents() && (mode == "Received" || mode == "Both")
}
