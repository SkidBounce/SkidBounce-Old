/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.utils.inventory

import net.ccbluex.liquidbounce.utils.MinecraftInstance
import net.ccbluex.liquidbounce.utils.extensions.hasItemAgePassed
import net.ccbluex.liquidbounce.utils.extensions.isEmpty
import net.minecraft.item.*
import net.minecraft.nbt.JsonToNBT
import net.minecraft.util.ResourceLocation

object ItemUtils : MinecraftInstance() {
    /**
     * Allows you to create an item using the item json
     *
     * @param itemArguments arguments of item
     * @return created item
     */
    fun createItem(itemArguments: String): ItemStack? {
        return try {
            val args = itemArguments.replace('&', '§').split(" ")

            val amount = args.getOrNull(1)?.toIntOrNull() ?: 1
            val meta = args.getOrNull(2)?.toIntOrNull() ?: 0

            val resourceLocation = ResourceLocation(args[0])
            val item = Item.itemRegistry.getObject(resourceLocation) ?: return null

            val itemStack = ItemStack(item, amount, meta)

            if (args.size >= 4) {
                val nbt = args.drop(3).joinToString(" ")

                itemStack.tagCompound = JsonToNBT.getTagFromJson(nbt)
            }

            itemStack
        } catch (exception: Exception) {
            exception.printStackTrace()
            null
        }
    }

    fun getItems(startInclusive: Int = 0, endInclusive: Int = 44,
                 itemDelay: Int? = null, filter: ((ItemStack, Int) -> Boolean)? = null): Map<Int, ItemStack> {
        val items = mutableMapOf<Int, ItemStack>()

        for (i in startInclusive..endInclusive) {
            val itemStack = mc.thePlayer.inventoryContainer.getSlot(i).stack ?: continue

            if (itemStack.isEmpty())
                continue

            if (itemDelay != null && !itemStack.hasItemAgePassed(itemDelay))
                continue

            if (filter?.invoke(itemStack, i) != false)
                items[i] = itemStack
        }

        return items
    }


    /**
     * Allows you to check if player is consuming item
     */
    val isConsumingItem: Boolean
        get() {
            if (!mc.thePlayer.isUsingItem)
                return false

            val usingItem = mc.thePlayer.itemInUse.item
            return usingItem is ItemFood || usingItem is ItemBucketMilk || usingItem is ItemPotion
        }
}
