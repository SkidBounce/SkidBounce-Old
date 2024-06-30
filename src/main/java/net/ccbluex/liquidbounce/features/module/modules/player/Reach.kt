/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.player

import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory.PLAYER
import net.ccbluex.liquidbounce.value.FloatValue
import kotlin.math.max

object Reach : Module("Reach", PLAYER) {

    val combatReach by FloatValue("CombatReach", 3.5f, 3f..7f)
    val buildReach by FloatValue("BuildReach", 5f, 4.5f..7f)

    val maxReach
        get() = max(combatReach, buildReach)
}
