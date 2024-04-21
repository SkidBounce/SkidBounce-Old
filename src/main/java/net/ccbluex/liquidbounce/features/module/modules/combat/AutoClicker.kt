/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.Render3DEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory.COMBAT
import net.ccbluex.liquidbounce.utils.extensions.fixedSensitivityPitch
import net.ccbluex.liquidbounce.utils.extensions.fixedSensitivityYaw
import net.ccbluex.liquidbounce.utils.misc.RandomUtils.nextFloat
import net.ccbluex.liquidbounce.utils.misc.RandomUtils.nextInt
import net.ccbluex.liquidbounce.utils.timing.TimeUtils.randomClickDelay
import net.ccbluex.liquidbounce.value.BooleanValue
import net.ccbluex.liquidbounce.value.IntValue
import net.ccbluex.liquidbounce.value.NumberValue
import net.minecraft.client.settings.KeyBinding.onTick
import net.minecraft.item.EnumAction
import net.minecraft.util.MovingObjectPosition.MovingObjectType.BLOCK
import kotlin.random.Random.Default.nextBoolean

object AutoClicker : Module("AutoClicker", COMBAT) {

    private val simulateDoubleClicking by BooleanValue("SimulateDoubleClicking", false)

    private val maxCPSValue: NumberValue<Int> = object : IntValue("MaxCPS", 8, 1..20) {
        override fun onChange(oldValue: Int, newValue: Int) = newValue.coerceAtLeast(minCPS)
    }
    private val maxCPS by maxCPSValue

    private val minCPS by object : IntValue("MinCPS", 5, 1..20) {
        override fun onChange(oldValue: Int, newValue: Int) = newValue.coerceAtMost(maxCPS)

        override fun isSupported() = !maxCPSValue.isMinimal
    }

    private val right by BooleanValue("Right", true)
    private val left by BooleanValue("Left", true)
    private val jitter by BooleanValue("Jitter", false)
    private val block by BooleanValue("AutoBlock", false) { left }

    private var rightDelay = randomClickDelay(minCPS, maxCPS)
    private var rightLastSwing = 0L
    private var leftDelay = randomClickDelay(minCPS, maxCPS)
    private var leftLastSwing = 0L

    private val shouldAutoClick
        get() = mc.thePlayer.capabilities.isCreativeMode || mc.objectMouseOver.typeOfHit != BLOCK

    private var shouldJitter = false

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        val doubleClick = if (simulateDoubleClicking) nextInt(-1, 1) else 0

        val time = System.currentTimeMillis()

        if (block && mc.gameSettings.keyBindAttack.isKeyDown && !mc.gameSettings.keyBindUseItem.isKeyDown) {
            mc.gameSettings.keyBindUseItem.pressTime = 0
        }

        if (left && mc.gameSettings.keyBindAttack.isKeyDown && shouldAutoClick && time - leftLastSwing >= leftDelay) {
            repeat(1 + doubleClick) {
                onTick(mc.gameSettings.keyBindAttack.keyCode)

                leftLastSwing = time
                leftDelay = randomClickDelay(minCPS, maxCPS)
            }
        } else if (block && mc.gameSettings.keyBindAttack.isKeyDown && !mc.gameSettings.keyBindUseItem.isKeyDown && shouldAutoClick && shouldAutoRightClick() && mc.gameSettings.keyBindAttack.pressTime != 0) {
            onTick(mc.gameSettings.keyBindUseItem.keyCode)
        }

        if (right && mc.gameSettings.keyBindUseItem.isKeyDown && time - rightLastSwing >= rightDelay) {
            repeat(1 + doubleClick) {
                onTick(mc.gameSettings.keyBindUseItem.keyCode)

                rightLastSwing = time
                rightDelay = randomClickDelay(minCPS, maxCPS)
            }
        }

        shouldJitter =
            !(mc.objectMouseOver.typeOfHit == BLOCK && mc.gameSettings.keyBindAttack.pressTime != 0)
    }

    @EventTarget
    fun onTick(event: UpdateEvent) {
        val thePlayer = mc.thePlayer ?: return

        if (jitter && ((left && mc.gameSettings.keyBindAttack.isKeyDown && shouldAutoClick && shouldJitter) || (right && mc.gameSettings.keyBindUseItem.isKeyDown && !mc.thePlayer.isUsingItem))) {
            if (nextBoolean()) thePlayer.fixedSensitivityYaw += nextFloat(-1F, 1F)
            if (nextBoolean()) thePlayer.fixedSensitivityPitch += nextFloat(-1F, 1F)
        }
    }

    private fun shouldAutoRightClick() = mc.thePlayer.heldItem?.itemUseAction in arrayOf(EnumAction.BLOCK)
}
