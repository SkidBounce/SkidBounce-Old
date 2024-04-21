/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement

import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory.MOVEMENT
import net.ccbluex.liquidbounce.ui.client.clickgui.ClickGui
import net.ccbluex.liquidbounce.ui.client.hud.designer.GuiHudDesigner
import net.ccbluex.liquidbounce.utils.extensions.updateKeys
import net.ccbluex.liquidbounce.utils.inventory.InventoryManager
import net.ccbluex.liquidbounce.utils.inventory.InventoryManager.canClickInventory
import net.ccbluex.liquidbounce.utils.inventory.InventoryUtils.serverOpenInventory
import net.ccbluex.liquidbounce.value.BooleanValue
import net.minecraft.client.gui.GuiChat
import net.minecraft.client.gui.GuiIngameMenu
import net.minecraft.client.gui.inventory.GuiChest
import net.minecraft.client.gui.inventory.GuiInventory

object InventoryMove : Module("InventoryMove", MOVEMENT, gameDetecting = false) {

    private val notInChests by BooleanValue("NotInChests", false)
    val aacAdditionPro by BooleanValue("AACAdditionPro", false)
    private val intave by BooleanValue("Intave", false)

    private val isIntave = (mc.currentScreen is GuiInventory || mc.currentScreen is GuiChest) && intave

    private val noMove by InventoryManager.noMoveValue
    private val noMoveAir by InventoryManager.noMoveAirValue
    private val noMoveGround by InventoryManager.noMoveGroundValue
    private val undetectable by InventoryManager.undetectableValue

    // If player violates nomove check and inventory is open, close inventory and reopen it when still
    private val silentlyCloseAndReopen by BooleanValue("SilentlyCloseAndReopen", false)
    { noMove && (noMoveAir || noMoveGround) }

    // Reopen closed inventory just before a click (could flag for clicking too fast after opening inventory)
    private val reopenOnClick by BooleanValue("ReopenOnClick", false)
    { silentlyCloseAndReopen && noMove && (noMoveAir || noMoveGround) }

    private val affectedBindings = arrayOf(
        mc.gameSettings.keyBindForward,
        mc.gameSettings.keyBindBack,
        mc.gameSettings.keyBindRight,
        mc.gameSettings.keyBindLeft,
        mc.gameSettings.keyBindJump,
        mc.gameSettings.keyBindSprint
    )

    @EventTarget(priority = 999)
    fun onUpdate(event: UpdateEvent) {
        val screen = mc.currentScreen

        // Don't make player move when chat or ESC menu are open
        if (screen is GuiChat || screen is GuiIngameMenu)
            return

        if (undetectable && (screen != null && screen !is GuiHudDesigner && screen !is ClickGui))
            return

        if (notInChests && screen is GuiChest)
            return

        if (silentlyCloseAndReopen && screen is GuiInventory) {
            if (canClickInventory(closeWhenViolating = true) && !reopenOnClick)
                serverOpenInventory = true
        }

        mc.gameSettings.updateKeys(*affectedBindings)
    }

    @EventTarget
    fun onStrafe(event: StrafeEvent) {
        if (isIntave)
            mc.gameSettings.keyBindSneak.pressed = true
    }

    @EventTarget
    fun onJump(event: JumpEvent) {
        if (isIntave) event.cancelEvent()
    }

    @EventTarget
    fun onClick(event: ClickWindowEvent) {
        if (!canClickInventory()) event.cancelEvent()
        else if (reopenOnClick) serverOpenInventory = true
    }

    override fun onDisable() {
        mc.gameSettings.updateKeys(*affectedBindings)
    }
}
