/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.utils.blink

import net.minecraft.client.entity.AbstractClientPlayer
import net.minecraft.client.entity.EntityOtherPlayerMP

// TODO: skin layers & cape
// FIXME: cannot place blocks inside
class FakePlayer(player: AbstractClientPlayer) : EntityOtherPlayerMP(player.worldObj, player.gameProfile) {
    override fun canBeCollidedWith() = false

    init {
        rotationYawHead = player.rotationYawHead
        renderYawOffset = player.renderYawOffset
        copyLocationAndAnglesFrom(player)

        itemInUse = player.itemInUse?.copy()
        itemInUseCount = player.itemInUseCount

        inventory.mainInventory = player.inventory?.mainInventory?.clone()
        inventory.armorInventory = player.inventory?.armorInventory?.clone()
        inventory.currentItem = player.inventory.currentItem

        isSneaking = player.isSneaking
    }
}
