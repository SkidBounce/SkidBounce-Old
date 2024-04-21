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
 * This packet is sent after either LoginMojang, LoginJWT, BanUser or UnbanUser were processed successfully.
 *
 * @param reason of success packet
 */
data class ClientSuccessPacket(
        @SerializedName("reason")
        val reason: String
) : Packet
