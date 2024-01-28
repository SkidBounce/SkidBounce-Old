/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.utils.MovementUtils.isMoving
import net.ccbluex.liquidbounce.utils.block.BlockUtils.getBlock
import net.ccbluex.liquidbounce.utils.block.BlockUtils.getMaterial
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.init.Blocks
import net.minecraft.util.BlockPos
import org.apache.commons.lang3.BooleanUtils.xor

object IceSpeed : Module("IceSpeed", ModuleCategory.MOVEMENT) {
    private val mode by ListValue("Mode", arrayOf("Friction", "AAC", "Spartan").sortedArray(), "Friction")
    private val iceFriction by FloatValue("IceFriction", 0.39f, 0.1f..0.98f) { mode == "Friction" }
    private val strafeIceFriction by FloatValue("StrafeIceFriction", 0.39f, 0.1f..0.98f) { mode == "Friction" }
    private val packediceFriction by FloatValue("PackedIceFriction", 0.39f, 0.1f..0.98f) { mode == "Friction" }
    private val strafePackediceFriction by FloatValue("StrafePackedIceFriction", 0.39f, 0.1f..0.98f) { mode == "Friction" }
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
        if (mode == "Friction") {
            if (xor(mc.thePlayer.moveForward != 0f, mc.thePlayer.moveStrafing != 0f)) {
                Blocks.ice.slipperiness = iceFriction
                Blocks.packed_ice.slipperiness = packediceFriction
            } else {
                Blocks.ice.slipperiness = strafeIceFriction
                Blocks.packed_ice.slipperiness = strafePackediceFriction
            }
        }

        val thePlayer = mc.thePlayer ?: return

        if (thePlayer.onGround && !thePlayer.isOnLadder && !thePlayer.isSneaking && thePlayer.isSprinting && isMoving) {
            if (mode == "AAC") {
                getMaterial(thePlayer.position.down()).let {
                    if (it == Blocks.ice || it == Blocks.packed_ice) {
                        thePlayer.motionX *= 1.342
                        thePlayer.motionZ *= 1.342
                        Blocks.ice.slipperiness = 0.6f
                        Blocks.packed_ice.slipperiness = 0.6f
                    }
                }
            }
            if (mode == "Spartan") {
                getMaterial(thePlayer.position.down()).let {
                    if (it == Blocks.ice || it == Blocks.packed_ice) {
                        val upBlock = getBlock(BlockPos(thePlayer).up(2))

                        if (upBlock != Blocks.air) {
                            thePlayer.motionX *= 1.342
                            thePlayer.motionZ *= 1.342
                        } else {
                            thePlayer.motionX *= 1.18
                            thePlayer.motionZ *= 1.18
                        }

                        Blocks.ice.slipperiness = 0.6f
                        Blocks.packed_ice.slipperiness = 0.6f
                    }
                }
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
