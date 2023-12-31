/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.event.AttackEvent
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.modules.combat.criticalsmodes.other.*
import net.ccbluex.liquidbounce.features.module.modules.movement.Fly
import net.ccbluex.liquidbounce.utils.timing.MSTimer
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.entity.EntityLivingBase
import net.minecraft.network.play.client.C03PacketPlayer

object Criticals : Module("Criticals", ModuleCategory.COMBAT) {

    private val criticalsModes = arrayOf(
        AACJump,
        BlocksMC,
        BlocksMC2,
        Hop,
        Motion,
        NCPPacket,
        NoGround,
        Packet,
        Packet2,
        TPHop,
        Visual,
        VerusJump,
    )
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
        if (event.packet is C03PacketPlayer && mode == "NoGround")
            event.packet.onGround = false
    }

    override val tag
        get() = mode
}
//                "tphop" -> {
//                    sendPackets(
//                        C04PacketPlayerPosition(x, y + 0.02, z, false),
//                        C04PacketPlayerPosition(x, y + 0.01, z, false)
//                    )
//                    thePlayer.setPosition(x, y + 0.01, z)
//                }
//            }
