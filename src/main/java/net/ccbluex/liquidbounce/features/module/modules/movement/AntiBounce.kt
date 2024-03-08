/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement

import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory.MOVEMENT
import net.ccbluex.liquidbounce.injection.forge.mixins.block.MixinBlockSlime

/**
 * @see MixinBlockSlime
 *
 * @author ManInMyVan/SkidBounce
 * @author ManInMyVan
 */
object AntiBounce : Module("AntiBounce", MOVEMENT)
