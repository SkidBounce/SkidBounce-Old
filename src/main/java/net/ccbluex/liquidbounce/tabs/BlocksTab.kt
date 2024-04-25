/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.tabs

import net.minecraft.creativetab.CreativeTabs
import net.minecraft.init.Blocks.*
import net.minecraft.init.Items.command_block_minecart
import net.minecraft.item.Item
import net.minecraft.item.ItemStack

object BlocksTab : CreativeTabs("Special blocks") {

    /**
     * Initialize of special blocks tab
     */
    init {
        backgroundImageName = "item_search.png"
    }

    /**
     * Add all items to tab
     *
     * @param itemList list of tab items
     */
    override fun displayAllReleventItems(itemList: MutableList<ItemStack>) {
        itemList += ItemStack(command_block)
        itemList += ItemStack(command_block_minecart)
        itemList += ItemStack(barrier)
        itemList += ItemStack(dragon_egg)
        itemList += ItemStack(brown_mushroom_block)
        itemList += ItemStack(red_mushroom_block)
        itemList += ItemStack(farmland)
        itemList += ItemStack(mob_spawner)
        itemList += ItemStack(lit_furnace)
    }

    /**
     * Return icon item of tab
     *
     * @return icon item
     */
    override fun getTabIconItem(): Item = ItemStack(command_block).item

    /**
     * Return name of tab
     *
     * @return tab name
     */
    override fun getTranslatedTabLabel() = "Special blocks"

    /**
     * @return searchbar status
     */
    override fun hasSearchBar() = true
}
