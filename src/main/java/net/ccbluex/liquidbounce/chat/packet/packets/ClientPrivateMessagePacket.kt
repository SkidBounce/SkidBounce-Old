/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.chat.packet.packets

import com.google.gson.annotations.SerializedName
import net.ccbluex.liquidbounce.chat.User

/**
 * AXOCHAT PROTOCOL
 * https://gitlab.com/frozo/axochat/blob/master/PROTOCOL.md
 *
 * This packet will be sent to an authenticated client with allow_messages turned on,
 * if another client successfully sent a private message to the server with the id.
 *
 * @param id author_id is an Id.
 * @param user author_info is optional and described in detail in UserInfo.
 * @param content content is any message fitting the validation scheme of the server.
 */
data class ClientPrivateMessagePacket(
    @SerializedName("author_id")
        val id: String,

    @SerializedName("author_info")
        val user: User,

    @SerializedName("content")
        val content: String
) : Packet
