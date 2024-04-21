/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.chat.packet.packets

import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * AXOCHAT PROTOCOL
 * https://gitlab.com/frozo/axochat/blob/master/PROTOCOL.md
 *
 * After the client received a MojangInfo packet and authenticating itself with mojang,
 * it has to send a LoginMojang packet to the server.
 * After the server receives a LoginMojang packet, it will send Success if the login was successful.

 * @param name name needs to be associated with the uuid.
 * @param uuid uuid is not guaranteed to be hyphenated.
 * @param allowMessages If allow_messages is true, other clients may send private messages to this client.
 */
data class ServerLoginMojangPacket(
    @SerializedName("name")
        val name: String,

    @SerializedName("uuid")
        val uuid: UUID,

    @SerializedName("allow_messages")
        val allowMessages: Boolean
) : Packet
