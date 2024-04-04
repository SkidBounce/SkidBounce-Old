/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.utils

import net.minecraft.potion.Potion
import net.minecraft.potion.Potion.*

object PotionUtils {
    enum class Potions(val potion: Potion) {
        SPEED(moveSpeed),
        SLOWNESS(moveSlowdown),
        HASTE(digSpeed),
        MINING_FATIGUE(digSlowdown),
        STRENGTH(damageBoost),
        INSTANT_HEALTH(heal),
        INSTANT_DAMAGE(harm),
        JUMP_BOOST(jump),
        NAUSEA(confusion),
        RESISTANCE(resistance),
        FIRE_RESISTANCE(fireResistance),
        WATER_BREATHING(waterBreathing),
        REGENERATION(regeneration),
        INVISIBILITY(invisibility),
        BLINDNESS(blindness),
        NIGHT_VISION(nightVision),
        HUNGER(hunger),
        WEAKNESS(weakness),
        POISON(poison),
        WITHER(wither),
        HEALTH_BOOST(healthBoost),
        ABSORPTION(absorption),
        SATURATION(saturation);
    }
}
