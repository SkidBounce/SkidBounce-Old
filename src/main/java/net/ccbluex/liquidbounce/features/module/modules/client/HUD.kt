/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.client

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.ui.client.hud.designer.GuiHudDesigner
import net.ccbluex.liquidbounce.utils.ClientUtils.resource
import net.ccbluex.liquidbounce.value.BoolValue
import net.minecraft.client.gui.GuiChat

object HUD : Module("HUD", ModuleCategory.CLIENT, defaultInArray = false, gameDetecting = false, subjective = true) {
    val blackHotbar by BoolValue("BlackHotbar", true)
    val inventoryParticle by BoolValue("InventoryParticle", false)
    private val blur by BoolValue("Blur", false)
    val fontChat by BoolValue("FontChat", false)

    @EventTarget
    fun onRender2D(event: Render2DEvent) {
        if (mc.currentScreen is GuiHudDesigner)
            return

        LiquidBounce.hud.render(false)
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) = LiquidBounce.hud.update()

    @EventTarget
    fun onKey(event: KeyEvent) = LiquidBounce.hud.handleKey('a', event.key)

    @EventTarget(ignoreCondition = true)
    fun onScreen(event: ScreenEvent) {
        if (mc.theWorld == null || mc.thePlayer == null) return
        if (state && blur && !mc.entityRenderer.isShaderActive && event.guiScreen != null &&
                !(event.guiScreen is GuiChat || event.guiScreen is GuiHudDesigner)) mc.entityRenderer.loadShader(
            resource("blur.json")
        ) else if (mc.entityRenderer.shaderGroup != null &&
            "${LiquidBounce.CLIENT_NAME.lowercase()}/blur.json" in mc.entityRenderer.shaderGroup.shaderGroupName) mc.entityRenderer.stopUseShader()
    }

    init {
        state = true
    }
}
