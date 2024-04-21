/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.render

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.events.Render2DEvent
import net.ccbluex.liquidbounce.event.events.Render3DEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory.RENDER
import net.ccbluex.liquidbounce.features.module.modules.player.InventoryCleaner
import net.ccbluex.liquidbounce.utils.ClientUtils.LOGGER
import net.ccbluex.liquidbounce.utils.render.ColorUtils.rainbow
import net.ccbluex.liquidbounce.utils.render.RenderUtils.drawEntityBox
import net.ccbluex.liquidbounce.utils.render.shader.shaders.GlowShader
import net.ccbluex.liquidbounce.value.BooleanValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.ListValue
import net.ccbluex.liquidbounce.value.NumberValue
import net.minecraft.entity.item.EntityItem
import java.awt.Color

object ItemESP : Module("ItemESP", RENDER, subjective = true) {
    private val mode by ListValue("Mode", arrayOf("Box", "OtherBox", "Glow"), "Box")

    private val glowRenderScale by FloatValue("Glow-Renderscale", 1f, 0.5f..2f) { mode == "Glow" }
    private val glowRadius by NumberValue<Int>(
        "Glow-Radius",
        4,
        1..5
    ) { mode == "Glow" }
    private val glowFade by NumberValue<Int>(
        "Glow-Fade",
        10,
        0..30
    ) { mode == "Glow" }
    private val glowTargetAlpha by FloatValue("Glow-Target-Alpha", 0f, 0f..1f) { mode == "Glow" }

    private val colorRainbow by BooleanValue("Rainbow", true)
    private val colorRed by NumberValue<Int>("R", 0, 0..255) { !colorRainbow }
    private val colorGreen by NumberValue<Int>("G", 255, 0..255) { !colorRainbow }
    private val colorBlue by NumberValue<Int>("B", 0, 0..255) { !colorRainbow }

    val color
        get() = if (colorRainbow) rainbow() else Color(colorRed, colorGreen, colorBlue)

    // TODO: Removed highlighting of EntityArrow to not complicate things even further

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        if (mc.theWorld == null || mc.thePlayer == null || mode == "Glow")
            return

        renderESP { isUseful, entity ->
            // Only render green boxes on useful items, if ItemESP is enabled, render boxes of ItemESP.color on useless items as well
            drawEntityBox(entity, if (isUseful) Color.green else color, mode == "Box")
        }
    }

    @EventTarget
    fun onRender2D(event: Render2DEvent) {
        if (mc.theWorld == null || mc.thePlayer == null || mode != "Glow")
            return

        renderESP { isUseful, entity ->
            GlowShader.startDraw(event.partialTicks, glowRenderScale)

            mc.renderManager.renderEntityStatic(entity, event.partialTicks, true)

            // Only render green boxes on useful items, if ItemESP is enabled, render boxes of ItemESP.color on useless items as well
            GlowShader.stopDraw(if (isUseful) Color.green else color, glowRadius, glowFade, glowTargetAlpha)
        }
    }

    private fun renderESP(action: (Boolean, EntityItem) -> Unit) {
        val entityStacksMap = mc.theWorld.loadedEntityList
            .filterIsInstance<EntityItem>()
            .associateBy { it.entityItem }

        val stacks = mc.thePlayer.openContainer.inventory

        try {
            entityStacksMap.forEach { (stack, entity) ->
                val isUseful = InventoryCleaner.handleEvents() && InventoryCleaner.highlightUseful
                        && InventoryCleaner.isStackUseful(stack, stacks, entityStacksMap)

                // If ItemESP is disabled, only render boxes on useful items
                if (!state && !isUseful)
                    return@forEach

                action(isUseful, entity)
            }
        } catch (ex: Exception) {
            LOGGER.error("An error occurred while rendering ItemESP!", ex)
        }
    }

    override fun handleEvents() =
        super.handleEvents() || (InventoryCleaner.handleEvents() && InventoryCleaner.highlightUseful)
}
