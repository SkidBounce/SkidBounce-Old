/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.noslowmodes.other

import net.ccbluex.liquidbounce.event.EventState.*
import net.ccbluex.liquidbounce.event.MotionEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.NoSlow.blockingPacketTiming
import net.ccbluex.liquidbounce.features.module.modules.movement.NoSlow.bowPacketTiming
import net.ccbluex.liquidbounce.features.module.modules.movement.NoSlow.consumePacketTiming
import net.ccbluex.liquidbounce.features.module.modules.movement.noslowmodes.NoSlowMode
import net.ccbluex.liquidbounce.utils.PacketUtils.sendPacket
import net.minecraft.item.*
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement

object Place : NoSlowMode("Place") {
    override fun onMotion(event: MotionEvent) {
        when (mc.thePlayer.heldItem.item) {
            is ItemSword ->
                if ((blockingPacketTiming == "Post" && event.eventState != POST) || (blockingPacketTiming == "Pre" && event.eventState != PRE))
                    return
            is ItemBow ->
                if ((bowPacketTiming == "Post" && event.eventState != POST) || (bowPacketTiming == "Pre" && event.eventState != PRE))
                    return
            is ItemFood, is ItemBucketMilk, is ItemPotion ->
                if ((consumePacketTiming == "Post" && event.eventState != POST) || (consumePacketTiming == "Pre" && event.eventState != PRE))
                    return
        }
        sendPacket(C08PacketPlayerBlockPlacement(mc.thePlayer.heldItem))
    }
}
