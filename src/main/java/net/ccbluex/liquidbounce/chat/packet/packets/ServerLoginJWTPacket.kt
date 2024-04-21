/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.chat.packet.packets

import com.google.gson.annotations.SerializedName

/**
 * AXOCHAT PROTOCOL
 * https://gitlab.com/frozo/axochat/blob/master/PROTOCOL.md
 *
 * To login using a json web token, the client has to send a LoginJWT packet.
 * it will send Success if the login was successful.
 *
 * @param token can be retrieved by sending RequestJWT on an already authenticated connection.
 * @param allowMessages If allow_messages is true, other clients may send private messages to this client.
 */
data class ServerLoginJWTPacket(
        @SerializedName("token")
        val token: String,

        @SerializedName("allow_messages")
        val allowMessages: Boolean
) : Packet
