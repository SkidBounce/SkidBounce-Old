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
import net.ccbluex.liquidbounce.utils.extensions.getBlock
import net.ccbluex.liquidbounce.utils.extensions.isActuallyPressed
import net.ccbluex.liquidbounce.utils.timing.MSTimer
import net.ccbluex.liquidbounce.value.BooleanValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntValue
import net.minecraft.init.Blocks.air
import net.minecraft.util.BlockPos

object Eagle : Module("Eagle", PLAYER) {

    private val sneakDelay by IntValue("SneakDelay", 0, 0..100)
    private val onlyWhenLookingDown by BooleanValue("OnlyWhenLookingDown", false)
    private val lookDownThreshold by FloatValue("LookDownThreshold", 45f, 0f..90f) { onlyWhenLookingDown }

    private val sneakTimer = MSTimer()

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        mc.thePlayer ?: return

        if (mc.thePlayer.onGround
            && BlockPos(mc.thePlayer).down().getBlock() == air
            && (!onlyWhenLookingDown || mc.thePlayer.rotationPitch >= lookDownThreshold)
            && sneakTimer.hasTimePassed(sneakDelay)
        ) {
            mc.gameSettings.keyBindSneak.pressed = true
            sneakTimer.reset()
        } else mc.gameSettings.keyBindSneak.pressed = mc.gameSettings.keyBindSneak.isActuallyPressed
    }

    override fun onDisable() {
        if (mc.thePlayer == null)
            return

        if (!mc.gameSettings.keyBindSneak.isActuallyPressed)
            mc.gameSettings.keyBindSneak.pressed = false
    }
}
