/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.*
import net.ccbluex.liquidbounce.features.module.modules.combat.criticalsmodes.ncp.*
import net.ccbluex.liquidbounce.features.module.modules.combat.criticalsmodes.other.*
import net.ccbluex.liquidbounce.features.module.modules.combat.criticalsmodes.vanilla.*
import net.ccbluex.liquidbounce.features.module.modules.movement.Fly
import net.ccbluex.liquidbounce.utils.PacketUtils
import net.ccbluex.liquidbounce.utils.extensions.*
import net.ccbluex.liquidbounce.utils.timing.MSTimer
import net.ccbluex.liquidbounce.value.*
import net.minecraft.entity.EntityLivingBase
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition

object Criticals : Module("Criticals", ModuleCategory.COMBAT) {
    private val criticalsModes = arrayOf(
        AACJump,
        BlocksMC,
        BlocksMC2,
        Hop,
        Motion,
        NCPPacket,
        NCPLatest,
        NoGround,
        Packet,
        Packet2,
        TPHop,
        Visual,
        VerusJump,
    ).sortedBy { it.modeName }
    private val modeModule get() = criticalsModes.find { it.modeName == mode }!!
    val mode by ListValue(
        "Mode",
        criticalsModes.map { it.modeName }.toTypedArray(),
        "Packet"
    )
    val delay by IntegerValue("Delay", 0, 0..500) { mode != "NoGround" }
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
    val motionJump by BoolValue("Motion-DoJump", true) { mode == "Motion" }

    val ncplatestAttacks by IntegerValue("NCPLatest-Attacks", 5, 1..10) { mode == "NCPLatest" }
    val msTimer = MSTimer()

    @EventTarget
    fun onAttack(event: AttackEvent) {
        if (event.targetEntity is EntityLivingBase) {
            val entity = event.targetEntity
            if (
                !msTimer.hasTimePassed(delay) ||
                mc.thePlayer == null ||
                (noMotionUp && mc.thePlayer.motionY > 0) ||
                (noMotionDown && mc.thePlayer.motionY < 0 && !mc.thePlayer.onGround) ||
                (noFly && Fly.handleEvents()) ||
                entity.hurtTime > hurtTime ||
                (noLava && mc.thePlayer.isInLava) ||
                (onlyGround && !mc.thePlayer.onGround) ||
                (noWeb && mc.thePlayer.isInWeb) ||
                (noWater && mc.thePlayer.isInWater) ||
                (noRiding && mc.thePlayer.isRiding) ||
                (noClimbing && mc.thePlayer.isOnLadder)
            ) return
            modeModule.onAttack(entity)
            msTimer.reset()
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        modeModule.onPacket(event)
    }

    fun sendPacket(x: Double = 0.0, y: Double = 0.0, z: Double = 0.0, ground: Boolean = false) {
        val (pX, pY, pZ) = mc.thePlayer
        PacketUtils.sendPacket(C04PacketPlayerPosition(pX + x, pY + y, pZ + z, ground))
    }

    override val tag
        get() = mode
}
