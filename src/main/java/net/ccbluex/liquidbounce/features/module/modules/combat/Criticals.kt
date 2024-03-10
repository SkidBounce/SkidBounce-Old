/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.event.AttackEvent
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory.COMBAT
import net.ccbluex.liquidbounce.features.module.modules.combat.criticalsmodes.CriticalsMode
import net.ccbluex.liquidbounce.utils.ClassUtils.getAllObjects
import net.ccbluex.liquidbounce.utils.timing.MSTimer
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.entity.EntityLivingBase

object Criticals : Module("Criticals", COMBAT) {
    private val criticalsModes = this.javaClass.`package`.getAllObjects<CriticalsMode>()
    private val modeModule get() = criticalsModes.find { it.modeName == mode }!!
    val noGround get() = mode == "NoGround"

    private val mode by ListValue("Mode", criticalsModes.map { it.modeName }.toTypedArray(), "Packet")
    private val delay by IntegerValue("Delay", 0, 0..5000) { mode != "NoGround" }
    private val attacks by IntegerValue("Attacks", 0, 0..10) { mode != "NoGround" }
    private val hurtTime by IntegerValue("HurtTime", 10, 0..10) { mode != "NoGround" }
    private val onlyGround by BoolValue("OnlyGround", false) { mode != "NoGround" }
    private val noMotionUp by BoolValue("NoMotionUp", false) { mode != "NoGround" }
    private val noMotionDown by BoolValue("NoMotionDown", false) { mode != "NoGround" }
    private val noRiding by BoolValue("NoRiding", true) { mode != "NoGround" }
    private val noWeb by BoolValue("NoWeb", false) { mode != "NoGround" }
    private val noClimbing by BoolValue("NoClimbing", true) { mode != "NoGround" }
    private val noWater by BoolValue("NoWater", true) { mode != "NoGround" }
    private val noLava by BoolValue("NoLava", false) { mode != "NoGround" }
    private val noFly by BoolValue("NoFly", false) { mode != "NoGround" }

    val motionY by FloatValue("Motion-Y", 0.2f, 0.01f..0.42f) { mode == "Motion" }
    val motionBoost by BoolValue("Motion-Boost", true) { mode == "Motion" }

    private val delayTimer = MSTimer()
    private var attackCounter = 0

    @EventTarget
    fun onAttack(event: AttackEvent) {
        mc.thePlayer ?: return
        if (event.targetEntity !is EntityLivingBase ||
            !delayTimer.hasTimePassed(delay) ||
            (noMotionUp && mc.thePlayer.motionY > 0) ||
            (noMotionDown && mc.thePlayer.motionY < 0 && !mc.thePlayer.onGround) ||
            (noFly && mc.thePlayer.capabilities.isFlying) ||
            event.targetEntity.hurtTime > hurtTime ||
            (noLava && mc.thePlayer.isInLava) ||
            (onlyGround && !mc.thePlayer.onGround) ||
            (noWeb && mc.thePlayer.isInWeb) ||
            (noWater && mc.thePlayer.isInWater) ||
            (noRiding && mc.thePlayer.isRiding) ||
            (noClimbing && mc.thePlayer.isOnLadder)
        ) return

        ++attackCounter

        if (attackCounter < attacks)
            return

        modeModule.onAttack(event.targetEntity)
        attackCounter = 0
        delayTimer.reset()
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        modeModule.onPacket(event)
    }

    override val tag
        get() = mode
}
