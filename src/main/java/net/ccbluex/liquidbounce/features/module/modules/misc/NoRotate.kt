/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.misc

import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.utils.Rotation
import net.ccbluex.liquidbounce.value.BoolValue
import net.minecraft.entity.player.EntityPlayer

object NoRotate : Module("NoRotate", ModuleCategory.MISC, gameDetecting = false) {
    var savedRotation = Rotation(0f, 0f)

    private val ignoreOnSpawn by BoolValue("IgnoreOnSpawn", false)
    val affectServerRotation by BoolValue("AffectServerRotation", true)

    fun shouldModify(player: EntityPlayer) = handleEvents() && (!ignoreOnSpawn || player.ticksExisted != 0)
}
