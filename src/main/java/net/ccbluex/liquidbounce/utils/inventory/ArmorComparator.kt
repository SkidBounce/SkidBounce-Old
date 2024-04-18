/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.utils.inventory

import net.ccbluex.liquidbounce.utils.MinecraftInstance
import net.ccbluex.liquidbounce.utils.extensions.enchantmentCount
import net.ccbluex.liquidbounce.utils.extensions.enchantmentSum
import net.ccbluex.liquidbounce.utils.extensions.totalDurability
import net.minecraft.entity.item.EntityItem
import net.minecraft.item.ItemArmor
import net.minecraft.item.ItemStack

object ArmorComparator: MinecraftInstance() {
	private val NULL_LIST = listOf<Pair<Int?, ItemStack>?>(null)

	fun getBestArmorSet(stacks: List<ItemStack?>, entityStacksMap: Map<ItemStack, EntityItem>? = null): ArmorSet? {
		val thePlayer = mc.thePlayer ?: return null

		// Consider armor pieces dropped on ground
		// Their indices are always -1
		val droppedStacks = entityStacksMap?.keys.indexedArmorStacks { -1 }

		// Consider currently equipped armor, when searching useful stuff in chests
		// Their indices are always null to prevent any accidental impossible interactions when searching through chests
		val equippedArmorWhenInChest =
			if (thePlayer.openContainer.windowId != 0)
				// Filter out any non armor items player could be equipped (skull / pumpkin)
				thePlayer.inventory.armorInventory.toList().indexedArmorStacks { null }
			else emptyList()

		val inventoryStacks = stacks.indexedArmorStacks()

		val armorMap =
			(droppedStacks + equippedArmorWhenInChest + inventoryStacks)
				.asSequence()
				.sortedBy { (index, stack) ->
					// Sort items by distance from player, equipped items are always preferred with distance -1
					if (index == -1)
						thePlayer.getDistanceSqToEntity(entityStacksMap?.get(stack) ?: return@sortedBy -1.0)
					else -1.0
				}
				// Prioritise sets that are in lower parts of inventory (not in chest) or equipped, prevents stealing multiple armor duplicates.
				.sortedByDescending {
					if (it.second in thePlayer.inventory.armorInventory) Int.MAX_VALUE
					else it.first ?: Int.MAX_VALUE
				}
				// Prioritise sets with more durability, enchantments
				.sortedByDescending { it.second.totalDurability }
				.sortedByDescending { it.second.enchantmentCount }
				.sortedByDescending { it.second.enchantmentSum }
				.groupBy { (it.second.item as ItemArmor).armorType }

		val helmets = armorMap[0] ?: NULL_LIST
		val chestplates = armorMap[1] ?: NULL_LIST
		val leggings = armorMap[2] ?: NULL_LIST
		val boots = armorMap[3] ?: NULL_LIST

		val armorCombinations =
			helmets.flatMap { helmet ->
				chestplates.flatMap { chestplate ->
					leggings.flatMap { leggings ->
						boots.map { boots ->
							ArmorSet(helmet, chestplate, leggings, boots)
						}
					}
				}
			}

		return armorCombinations.maxByOrNull { it.defenseFactor }
	}
}

/**
 * This function takes an iterable of ItemStacks and an optional index callback function,
 * and returns a list of pairs. Each pair consists of an index and an ItemStack.
 *
 * @param indexCallback A function that takes an integer as input and returns an integer.
 *                      This function is used to manipulate the index of each ItemStack in the iterable.
 *                      By default, it returns the same index.
 *
 * @return A list of pairs. Each pair consists of an index (possibly manipulated by the indexCallback function)
 *         and an ItemStack. Only ItemStacks where the item is an instance of ItemArmor are included in the list.
 *         If the iterable is null, an empty list is returned.
 */
private fun Iterable<ItemStack?>?.indexedArmorStacks(indexCallback: (Int) -> Int? = { it }): List<Pair<Int?, ItemStack>> =
	this?.mapIndexedNotNull { index, stack ->
		if (stack?.item is ItemArmor) indexCallback(index) to stack
		else null
	} ?: emptyList()
