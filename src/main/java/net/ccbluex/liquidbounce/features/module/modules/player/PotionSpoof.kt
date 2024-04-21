/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.player

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.events.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory.PLAYER
import net.ccbluex.liquidbounce.value.BooleanValue
import net.ccbluex.liquidbounce.value.IntValue
import net.minecraft.potion.Potion.*
import net.minecraft.potion.PotionEffect

object PotionSpoof : Module("PotionSpoof", PLAYER) {

    private val level by object : IntValue("PotionLevel", 2, 1..5) {
        override fun onChanged(oldValue: Int, newValue: Int) = onDisable()
    }

    private val speedValue = BooleanValue("Speed", false)
    private val moveSlowDownValue = BooleanValue("Slowness", false)
    private val hasteValue = BooleanValue("Haste", false)
    private val digSlowDownValue = BooleanValue("MiningFatigue", false)
    private val blindnessValue = BooleanValue("Blindness", false)
    private val strengthValue = BooleanValue("Strength", false)
    private val jumpBoostValue = BooleanValue("JumpBoost", false)
    private val weaknessValue = BooleanValue("Weakness", false)
    private val regenerationValue = BooleanValue("Regeneration", false)
    private val witherValue = BooleanValue("Wither", false)
    private val resistanceValue = BooleanValue("Resistance", false)
    private val fireResistanceValue = BooleanValue("FireResistance", false)
    private val absorptionValue = BooleanValue("Absorption", false)
    private val healthBoostValue = BooleanValue("HealthBoost", false)
    private val poisonValue = BooleanValue("Poison", false)
    private val saturationValue = BooleanValue("Saturation", false)
    private val waterBreathingValue = BooleanValue("WaterBreathing", false)

    private val potionMap = mapOf(
        moveSpeed.id to speedValue,
        moveSlowdown.id to moveSlowDownValue,
        digSpeed.id to hasteValue,
        digSlowdown.id to digSlowDownValue,
        blindness.id to blindnessValue,
        damageBoost.id to strengthValue,
        jump.id to jumpBoostValue,
        weakness.id to weaknessValue,
        regeneration.id to regenerationValue,
        wither.id to witherValue,
        resistance.id to resistanceValue,
        fireResistance.id to fireResistanceValue,
        absorption.id to absorptionValue,
        healthBoost.id to healthBoostValue,
        poison.id to poisonValue,
        saturation.id to saturationValue,
        waterBreathing.id to waterBreathingValue
    )

    override fun onDisable() {
        mc.thePlayer ?: return

        mc.thePlayer.activePotionEffects
            .filter { it.duration == 0 && potionMap[it.potionID]?.get() == true }
            .forEach { mc.thePlayer.removePotionEffect(it.potionID) }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) =
        potionMap.forEach { (potionId, value) ->
            if (value.get())
                mc.thePlayer.addPotionEffect(PotionEffect(potionId, 0, level - 1, false, false))
            else if (mc.thePlayer.activePotionEffects.any { it.duration == 0 && it.potionID == potionId })
                mc.thePlayer.removePotionEffect(potionId)
        }
}
