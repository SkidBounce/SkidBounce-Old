/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement

import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.modules.movement.longjumpmodes.aac.*
import net.ccbluex.liquidbounce.features.module.modules.movement.longjumpmodes.mineplex.*
import net.ccbluex.liquidbounce.features.module.modules.movement.longjumpmodes.ncp.*
import net.ccbluex.liquidbounce.features.module.modules.movement.longjumpmodes.other.*
import net.ccbluex.liquidbounce.utils.MovementUtils.isMoving
import net.ccbluex.liquidbounce.utils.MovementUtils.speed
import net.ccbluex.liquidbounce.utils.extensions.jump
import net.ccbluex.liquidbounce.value.*

object LongJump : Module("LongJump", ModuleCategory.MOVEMENT) {

    private val longJumpModes = arrayOf(
        // NCP
        NCP,

        // AAC
        AACv1, AACv2, AACv3,

        // Mineplex
        Mineplex, Mineplex2, Mineplex3,

        // Redesky
        Redesky,

        // Other
        Hycraft, Buzz,
    ).sortedBy { it.modeName }

    private val modes = longJumpModes.map { it.modeName }.toTypedArray()

    val mode by ListValue("Mode", modes, "NCP")
    val ncpBoost by FloatValue("NCPBoost", 4.25f, 1f..10f) { mode == "NCP" }
    val redeskyJumpMovement by FloatValue("RedeskyJumpMovement", 0.15f, 0.05f..0.25f) { mode == "Redesky" }
    val redeskyMotionY by FloatValue("RedeskyMotionY",0.05f,0.05f..1f) { mode == "Redesky" }
    val redeskyUseTimer by BoolValue("RedeskyUseTimer", false) { mode == "Redesky" }
    val redeskyTimer by FloatValue("RedeskyTimer", 0.7f, 0.1f..1f) { mode == "Redesky" }

    private val autoJump by BoolValue("AutoJump", false)

    var jumped = false
    var canBoost = false
    var teleported = false
    var canMineplexBoost = false

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (LadderJump.jumped) speed *= 1.08f

        if (jumped) {
            val mode = mode

            if (mc.thePlayer.onGround || mc.thePlayer.capabilities.isFlying) {
                jumped = false
                canMineplexBoost = false

                if (mode == "NCP") {
                    mc.thePlayer.motionX = 0.0
                    mc.thePlayer.motionZ = 0.0
                }
                return
            }

            modeModule.onUpdate()
        }
        if (autoJump && mc.thePlayer.onGround && isMoving) {
            jumped = true
            mc.thePlayer.jump(0.42)
        }
    }

    @EventTarget
    fun onMove(event: MoveEvent) {
        modeModule.onMove(event)
    }

    @EventTarget(ignoreCondition = true)
    fun onJump(event: JumpEvent) {
        jumped = true
        canBoost = true
        teleported = false

        if (handleEvents()) {
            modeModule.onJump(event)
        }
    }

    override fun onDisable() {
        modeModule.onDisable()
    }

    override val tag
        get() = mode

    private val modeModule
        get() = longJumpModes.find { it.modeName == mode }!!
}
