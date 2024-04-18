/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.utils.extensions

import net.ccbluex.liquidbounce.injection.implementations.IMixinItemStack
import net.ccbluex.liquidbounce.utils.MinecraftInstance.Companion.mc
import net.ccbluex.liquidbounce.utils.inventory.ArmorSet
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.Enchantment.sharpness
import net.minecraft.init.Items.arrow
import net.minecraft.item.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.math.roundToInt

val ItemStack.durability
    get() = maxDamage - itemDamage

/**
 * Calculates the estimated durability the item has, accounting for unbreaking
 */
val ItemStack.totalDurability: Int
    get() {
        // See https://minecraft.wiki/w/Unbreaking
        val multiplier =
            if (item is ItemArmor) 1 / (0.6 + (0.4 / (getEnchantmentLevel(Enchantment.unbreaking) + 1)))
            else getEnchantmentLevel(Enchantment.unbreaking) + 1.0

        return (multiplier * durability).roundToInt()
    }

val ItemStack.enchantments: Map<Enchantment, Int>
    get() {
        val enchantments = mutableMapOf<Enchantment, Int>()

        if (this.enchantmentTagList == null || enchantmentTagList.hasNoTags())
            return enchantments

        repeat(enchantmentTagList.tagCount()) {
            val tagCompound = enchantmentTagList.getCompoundTagAt(it)
            if (tagCompound.hasKey("ench") || tagCompound.hasKey("id"))
                enchantments[Enchantment.getEnchantmentById(tagCompound.getInteger("id"))] = tagCompound.getInteger("lvl")
        }

        return enchantments
    }

val ItemStack.enchantmentCount
    get() = enchantments.size

// Returns sum of levels of all enchantment levels
val ItemStack.enchantmentSum
    get() = enchantments.values.sum()

fun ItemStack.getEnchantmentLevel(enchantment: Enchantment) = enchantments.getOrDefault(enchantment, 0)

// Makes Kotlin smart-cast the stack to not null ItemStack
@OptIn(ExperimentalContracts::class)
fun ItemStack?.isEmpty(): Boolean {
    contract {
        returns(false) implies (this@isEmpty != null)
    }

    return this == null || item == null
}

@Suppress("CAST_NEVER_SUCCEEDS")
fun ItemStack?.hasItemAgePassed(delay: Int) = this == null
        || System.currentTimeMillis() - (this as IMixinItemStack).itemDelay >= delay

val ItemStack.attackDamage
    get() = (attributeModifiers["generic.attackDamage"].firstOrNull()?.amount ?: 1.0) +
            1.25 * getEnchantmentLevel(sharpness)

val ItemStack.isSplashPotion get() = item is ItemPotion && ItemPotion.isSplash(metadata)

val Item.canUse get() = when (this) {
    is ItemSword, is ItemPotion, is ItemBucketMilk -> true
    is ItemBow -> mc.playerController.isInCreativeMode || mc.thePlayer.inventory.hasItem(arrow)
    is ItemAppleGold -> mc.playerController.isNotCreative
    is ItemFood -> mc.playerController.isNotCreative && mc.thePlayer.foodStats.foodLevel < 20
    else -> false
}

operator fun ArmorSet?.contains(stack: ItemStack) = this?.contains(stack) ?: true
