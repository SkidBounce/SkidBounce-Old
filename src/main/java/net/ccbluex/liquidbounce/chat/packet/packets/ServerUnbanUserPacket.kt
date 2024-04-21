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
 * A client can send this packet to unban other users.
 *
 * @param user user is an Id.
 */
data class ServerUnbanUserPacket(
        @SerializedName("user")
        val user: String
) : Packet
