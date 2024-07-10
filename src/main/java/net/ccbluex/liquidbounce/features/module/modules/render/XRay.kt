/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.render

import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory.RENDER
import net.minecraft.init.Blocks.*

object XRay : Module("XRay", RENDER, gameDetecting = false) {

    @JvmStatic
    val xrayBlocks = mutableListOf(
        coal_ore,
        iron_ore,
        gold_ore,
        redstone_ore,
        lapis_ore,
        diamond_ore,
        emerald_ore,
        quartz_ore,
        clay,
        glowstone,
        crafting_table,
        torch,
        ladder,
        tnt,
        coal_block,
        iron_block,
        gold_block,
        diamond_block,
        emerald_block,
        lapis_block,
        fire,
        mossy_cobblestone,
        mob_spawner,
        end_portal_frame,
        enchanting_table,
        bookshelf,
        command_block,
        lava,
        flowing_lava,
        water,
        flowing_water,
        furnace,
        lit_furnace,
    )

    private var prevGammaLevel = 0f

    override fun onEnable() {
        prevGammaLevel = mc.gameSettings.gammaSetting
    }

    override fun onToggle(state: Boolean) {
        mc.renderGlobal.loadRenderers()
    }

    override fun onDisable() {
        mc.gameSettings.gammaSetting = prevGammaLevel
    }
}
