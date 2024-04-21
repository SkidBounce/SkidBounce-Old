/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.chat.packet.packets

/**
 * AXOCHAT PROTOCOL
 * https://gitlab.com/frozo/axochat/blob/master/PROTOCOL.md
 *
 * To login via mojang, the client has to send a RequestMojangInfo packet.
 * The server will then send a MojangInfo to the client.
 * This packet has no body.
 */
class ServerRequestMojangInfoPacket : Packet
