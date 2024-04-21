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
 * The content of this packet will be sent to the specified client as PrivateMessage if it fits the validation scheme.
 *
 * @param receiver receiver is an Id.
 * @param content content of message.
 */
data class ServerPrivateMessagePacket(
        @SerializedName("receiver")
        val receiver: String,

        @SerializedName("content")
        val content: String
) : Packet
