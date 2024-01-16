/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement

import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.utils.MovementUtils.direction
import net.ccbluex.liquidbounce.utils.block.BlockUtils.collideBlockIntersects
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.init.Blocks
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.util.AxisAlignedBB
import kotlin.math.cos
import kotlin.math.sin

object Spider : Module("Spider", ModuleCategory.MOVEMENT) {
    private val mode by ListValue("Mode", arrayOf("Vanilla", "Checker", "Collide", "AAC3.3.12", "AACGlide"), "Vanilla")
    private val collideGlitch by BoolValue("Collide-Glitch", true) { mode == "Collide" }
    private val collideJumpMotion by FloatValue("Collide-JumpMotion", 0.42f, 0.1f..1f) { mode == "Collide" }
    private val collideFast by BoolValue("Collide-Fast", true) { mode == "Collide" }
    private val collideFastSpeed by FloatValue("Collide-FastSpeed", 0.3f, 0f..1f) { mode == "Collide" && collideFast }
    private val CheckerMotion by FloatValue("Checker-Motion", 0f, 0f..1f) { mode == "Checker" }
    private val vanillaFastStop by BoolValue("Vanilla-FastStop", true) { mode == "Vanilla" }
    private val vanillaMotion by FloatValue("Vanilla-Motion", 0.42f, 0.1f..1f) { mode == "Vanilla" }

    private var glitch = false
    private var waited = 0

    @EventTarget
    fun onMove(event: MoveEvent) {
        mc.thePlayer ?: return

        if (!mc.thePlayer.isCollidedHorizontally || mc.thePlayer.isOnLadder || mc.thePlayer.isInWater || mc.thePlayer.isInLava)
            return

        if (mode == "Vanilla") {
            event.y = vanillaMotion.toDouble()
            mc.thePlayer.motionY = if (vanillaFastStop) 0.0 else vanillaMotion.toDouble()
        }
    }

    @EventTarget
    fun onUpdate(event: MotionEvent) {
        mc.thePlayer ?: return

        if (event.eventState != EventState.POST)
            return

        when (mode) {
            "Collide" -> {
                if (mc.thePlayer.motionY < 0)
                    glitch = collideGlitch
                if (mc.thePlayer.isCollidedHorizontally)
                    if (mc.thePlayer.onGround) {
                        mc.thePlayer.jump()
                        mc.thePlayer.motionY = collideJumpMotion.toDouble()
                    } else if (collideFast && mc.thePlayer.motionY < 0)
                        mc.thePlayer.motionY = -collideFastSpeed.toDouble()
            }
            "Checker" -> {
                val isInsideBlock = collideBlockIntersects(mc.thePlayer.entityBoundingBox) {
                    it != Blocks.air
                }
                val motion = CheckerMotion

                if (isInsideBlock && motion != 0f)
                    mc.thePlayer.motionY = motion.toDouble()
            }
            "AAC3.3.12" -> if (mc.thePlayer.isCollidedHorizontally && !mc.thePlayer.isOnLadder) {
                waited++
                when (waited) {
                    1, 12, 23 -> mc.thePlayer.motionY = 0.43
                    29 -> mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.5, mc.thePlayer.posZ)
                    else -> if (waited >= 30) waited = 0
                }
            } else if (mc.thePlayer.onGround) waited = 0
            "AACGlide" -> {
                if (mc.thePlayer.isCollidedHorizontally && !mc.thePlayer.isOnLadder)
                    mc.thePlayer.motionY = -0.19
            }
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet

        if (packet is C03PacketPlayer) {
            if (glitch) {
                val yaw = direction
                packet.x -= sin(yaw) * 0.00000001
                packet.z += cos(yaw) * 0.00000001
                glitch = false
            }
        }
    }

    @EventTarget
    fun onBlockBB(event: BlockBBEvent) {
        mc.thePlayer ?: return

        val mode = mode

        when (mode) {
            /*
            if (event.y > mc.thePlayer.posY &&
                collideBlockIntersects(mc.thePlayer.entityBoundingBox) { block: Block? -> block !is BlockAir }
                || mc.thePlayer.isCollidedHorizontally
                ) event.boundingBox = AxisAlignedBB.fromBounds(0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
            */
            "Checker" -> if (event.y > mc.thePlayer.posY) event.boundingBox = null
            "Collide" ->
                if (event.block == Blocks.air && event.y < mc.thePlayer.posY && mc.thePlayer.isCollidedHorizontally
                    && !mc.thePlayer.isOnLadder && !mc.thePlayer.isInWater && !mc.thePlayer.isInLava)
                    event.boundingBox = AxisAlignedBB.fromBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)
                        .offset(mc.thePlayer.posX, mc.thePlayer.posY.toInt() - 1.0, mc.thePlayer.posZ)
        }
    }

    override val tag
        get() = mode
}