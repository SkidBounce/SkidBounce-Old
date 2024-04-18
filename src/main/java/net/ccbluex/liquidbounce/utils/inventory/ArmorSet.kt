/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.utils.inventory

import net.ccbluex.liquidbounce.utils.extensions.getEnchantmentLevel
import net.minecraft.enchantment.Enchantment
import net.minecraft.item.ItemArmor
import net.minecraft.item.ItemStack

class ArmorSet(private vararg val armorPairs: Pair<Int?, ItemStack>?) : Iterable<Pair<Int?, ItemStack>?> {
	/**
	 * 1.4.6 - 1.8.9 Armor calculations
	 * https://minecraft.wiki/w/Armor#Enchantments
	 *
	 * @return Average defense of the whole armor set.
	 */
	val defenseFactor by lazy {
		var baseDefensePercentage = 0
		var epf = 0

		forEach { pair ->
			val stack = pair?.second ?: return@forEach
			val item = stack.item as ItemArmor
			baseDefensePercentage += item.armorMaterial.getDamageReductionAmount(item.armorType) * 4

			val protectionLvl = stack.getEnchantmentLevel(Enchantment.protection)

			// Calculate epf based on protection level
			if (protectionLvl > 0)
				epf += ((6 + protectionLvl * protectionLvl) * 0.75f / 3).toInt()
		}

		val baseDefense = baseDefensePercentage / 100f

		// Not ceiling epf up to simulate the fact that 0.75f is actually random number between 0.5 and 1
		// By ceiling up, you for example get that 3x protection 1 is same as 4x protection 1, even tho 4x protection 1 has better overall average defense
		// More details: https://www.guilded.gg/CCBlueX/groups/1dgpg8Jz/channels/034be45e-1b72-4d5a-bee7-d6ba52ba1657/chat?messageId=c0d88f1e-5ad6-48f3-8acb-d5ab7611164b
		baseDefense + (1 - baseDefense) * epf.coerceAtMost(25) * 0.75f * 0.04f
	}

	override fun iterator() = armorPairs.iterator()

	operator fun contains(stack: ItemStack) = armorPairs.any { it?.second == stack }

	operator fun contains(index: Int) = armorPairs.any { it?.first == index }

	fun indexOf(stack: ItemStack) = armorPairs.find { it?.second == stack }?.first ?: -1

	operator fun get(index: Int) = armorPairs.getOrNull(index)
}
