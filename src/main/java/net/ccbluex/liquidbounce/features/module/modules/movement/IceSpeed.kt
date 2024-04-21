/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.events.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory.MOVEMENT
import net.ccbluex.liquidbounce.utils.MovementUtils.isMoving
import net.ccbluex.liquidbounce.utils.block.BlockUtils.getBlock
import net.ccbluex.liquidbounce.utils.block.BlockUtils.getMaterial
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.init.Blocks
import net.minecraft.util.BlockPos
import org.apache.commons.lang3.BooleanUtils.xor

object IceSpeed : Module("IceSpeed", MOVEMENT) {
    private val mode by ListValue("Mode", arrayOf("Friction", "AAC", "Spartan", "TakaAC").sortedArray(), "Friction")
    private val iceFriction by FloatValue("IceFriction", 0.39f, 0.1f..0.98f) { mode == "Friction" }
    private val strafeIceFriction by FloatValue("StrafeIceFriction", 0.39f, 0.1f..0.98f) { mode == "Friction" }
    private val packediceFriction by FloatValue("PackedIceFriction", 0.39f, 0.1f..0.98f) { mode == "Friction" }
    private val strafePackediceFriction by FloatValue(
        "StrafePackedIceFriction",
        0.39f,
        0.1f..0.98f
    ) { mode == "Friction" }
    private val takaacSpeed by FloatValue("TakaAC-Speed", 0.2f, 0f..1f) { mode == "TakaAC" }
    override fun onEnable() {
        if (mode == "Friction") {
            Blocks.ice.slipperiness = iceFriction
            Blocks.packed_ice.slipperiness = packediceFriction
        }
        super.onEnable()
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        Blocks.ice.slipperiness = 0.98f
        Blocks.packed_ice.slipperiness = 0.98f

        if (mode in arrayOf("Spartan", "AAC") && (
                    !mc.thePlayer.onGround ||
                            mc.thePlayer.isOnLadder ||
                            mc.thePlayer.isSneaking ||
                            !mc.thePlayer.isSprinting ||
                            !isMoving ||
                            mc.thePlayer == null
                    )
        ) return

        when (mode) {
            "Friction" -> {
                if (xor(mc.thePlayer.moveForward != 0f, mc.thePlayer.moveStrafing != 0f)) {
                    Blocks.ice.slipperiness = iceFriction
                    Blocks.packed_ice.slipperiness = packediceFriction
                } else {
                    Blocks.ice.slipperiness = strafeIceFriction
                    Blocks.packed_ice.slipperiness = strafePackediceFriction
                }
            }

            "Spartan" -> {
                getMaterial(mc.thePlayer.position.down()).let {
                    if (it == Blocks.ice || it == Blocks.packed_ice) {
                        val upBlock = getBlock(BlockPos(mc.thePlayer).up(2))

                        if (upBlock != Blocks.air) {
                            mc.thePlayer.motionX *= 1.342
                            mc.thePlayer.motionZ *= 1.342
                        } else {
                            mc.thePlayer.motionX *= 1.18
                            mc.thePlayer.motionZ *= 1.18
                        }

                        Blocks.ice.slipperiness = 0.6f
                        Blocks.packed_ice.slipperiness = 0.6f
                    }
                }
            }

            "AAC" -> {
                getMaterial(mc.thePlayer.position.down()).let {
                    if (it == Blocks.ice || it == Blocks.packed_ice) {
                        mc.thePlayer.motionX *= 1.342
                        mc.thePlayer.motionZ *= 1.342
                        Blocks.ice.slipperiness = 0.6f
                        Blocks.packed_ice.slipperiness = 0.6f
                    }
                }
            }

            "TakaAC" -> {
                val speed = if (
                    mc.thePlayer.isJumping || getBlock(mc.thePlayer.position.up(2)) != Blocks.air
                ) takaacSpeed else 0.6f

                Blocks.ice.slipperiness = speed
                Blocks.packed_ice.slipperiness = speed
            }
        }
    }

    override fun onDisable() {
        Blocks.ice.slipperiness = 0.98f
        Blocks.packed_ice.slipperiness = 0.98f
        super.onDisable()
    }

    override val tag
        get() = mode
}
