/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.utils.blink

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.Listenable
import net.ccbluex.liquidbounce.event.events.PacketEvent
import net.ccbluex.liquidbounce.event.events.Render3DEvent
import net.ccbluex.liquidbounce.event.events.WorldEvent
import net.ccbluex.liquidbounce.features.module.modules.player.Blink
import net.ccbluex.liquidbounce.features.module.modules.render.Breadcrumbs
import net.ccbluex.liquidbounce.utils.ClientUtils.displayClientMessage
import net.ccbluex.liquidbounce.utils.MinecraftInstance.Companion.mc
import net.ccbluex.liquidbounce.utils.PacketType
import net.ccbluex.liquidbounce.utils.PacketUtils.queuedPackets
import net.ccbluex.liquidbounce.utils.PacketUtils.sendPacket
import net.ccbluex.liquidbounce.utils.PacketUtils.type
import net.ccbluex.liquidbounce.utils.extensions.hasPosition
import net.ccbluex.liquidbounce.utils.render.ColorUtils.rainbow
import net.ccbluex.liquidbounce.utils.render.RenderUtils.glColor
import net.minecraft.network.Packet
import net.minecraft.network.handshake.client.C00Handshake
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.server.S02PacketChat
import net.minecraft.network.play.server.S40PacketDisconnect
import net.minecraft.network.status.client.C00PacketServerQuery
import net.minecraft.network.status.client.C01PacketPing
import net.minecraft.util.Vec3
import org.lwjgl.opengl.GL11.*
import java.awt.Color
import java.util.concurrent.LinkedBlockingQueue
import kotlin.random.Random

object BlinkHandler : Listenable {
    private var fakePlayer: FakePlayer? = null
    private const val FAKE_PLAYER_ID = -1337

    val clientBlinkStates = mutableSetOf<IBlink>()
    val serverBlinkStates = mutableSetOf<IBlink>()
    val packets = LinkedBlockingQueue<Packet<*>>()
    private val positions = LinkedBlockingQueue<Vec3>()

    @EventTarget(priority = Int.MIN_VALUE)
    private fun onPacket(event: PacketEvent) {
        if (mc.thePlayer == null || mc.thePlayer.isDead || event.isCancelled) {
            return
        }

        when (event.packet) {
            is C00Handshake,
            is C00PacketServerQuery,
            is C01PacketPing,
            is S02PacketChat,
            is S40PacketDisconnect,
            -> return
        }

        val clientBlinkState = clientBlinkStates.isNotEmpty()
        val serverBlinkState = serverBlinkStates.isNotEmpty()

        val blink = when (event.packet.type) {
            PacketType.CLIENT -> clientBlinkState
            PacketType.SERVER -> serverBlinkState
        }

        if (blink) {
            event.cancelEvent()
            synchronized(packets) {
                packets += event.packet
            }

            if (event.packet is C03PacketPlayer && event.packet.hasPosition) {
                synchronized(positions) {
                    positions += Vec3(event.packet.x, event.packet.y, event.packet.z)
                }
            }
        }

        release(!clientBlinkState, !serverBlinkState)

        updateFakePlayerState()
    }

    fun release(releaseClient: Boolean, releaseServer: Boolean, overrideBlink: Boolean = false) {
        val client = releaseClient && (overrideBlink || !Blink.blinkingSend())
        val server = releaseServer && (overrideBlink || !Blink.blinkingReceive())

        if (client) {
            updateFakePlayerData()
            synchronized(positions) {
                positions.clear()
            }
        }

        if (!client && !server || packets.isEmpty()) {
            return
        }

        synchronized(packets) {
            packets.removeIf {
                when (it.type) {
                    PacketType.CLIENT -> {
                        if (client) {
                            sendPacket(it, false)
                        }

                        client
                    }

                    PacketType.SERVER -> {
                        if (server) {
                            queuedPackets += it
                        }

                        server
                    }
                }
            }
        }
    }

    @EventTarget
    private fun onWorld(event: WorldEvent) {
        // Clear packets on disconnect only
        if (event.worldClient == null) {
            synchronized(packets) {
                packets.clear()
            }
            synchronized(positions) {
                positions.clear()
            }
        }
    }

    private fun updateFakePlayerData() {
        mc.thePlayer ?: return
        fakePlayer = FakePlayer(mc.thePlayer)
    }

    @Synchronized
    private fun updateFakePlayerState() {
        mc.theWorld ?: return
        mc.thePlayer ?: return

        val clientBlinkState = clientBlinkStates.isNotEmpty()

        if (Blink.fakePlayerMenu && clientBlinkState) {
            if (mc.theWorld.getEntityByID(FAKE_PLAYER_ID) == null) {
                fakePlayer ?: updateFakePlayerData()
                mc.theWorld.addEntityToWorld(FAKE_PLAYER_ID, FakePlayer(fakePlayer!!))
            }
        } else if (fakePlayer != null) {
            mc.theWorld.removeEntityFromWorld(FAKE_PLAYER_ID)
            if (!clientBlinkState) {
                fakePlayer = null
            }
        }
    }

    @EventTarget
    private fun onRender3D(event: Render3DEvent) {
        val color = if (Breadcrumbs.colorRainbow) rainbow() else Color(Breadcrumbs.colorRed, Breadcrumbs.colorGreen, Breadcrumbs.colorBlue)

        synchronized(positions) {
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

            for (pos in positions) {
                glVertex3d(pos.xCoord - renderPosX, pos.yCoord - renderPosY, pos.zCoord - renderPosZ)
            }

            glColor4d(1.0, 1.0, 1.0, 1.0)
            glEnd()
            glEnable(GL_DEPTH_TEST)
            glDisable(GL_LINE_SMOOTH)
            glDisable(GL_BLEND)
            glEnable(GL_TEXTURE_2D)
            glPopMatrix()
        }
    }

    override fun handleEvents() = true
}
