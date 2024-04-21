/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.events.MoveEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory.MOVEMENT
import net.ccbluex.liquidbounce.utils.block.BlockUtils.getBlock
import net.ccbluex.liquidbounce.utils.misc.FallingPlayer
import net.ccbluex.liquidbounce.value.BooleanValue
import net.ccbluex.liquidbounce.value.IntValue
import net.minecraft.block.BlockAir
import net.minecraft.util.BlockPos

object SafeWalk : Module("SafeWalk", MOVEMENT) {

    private val airSafe by BooleanValue("AirSafe", false)
    private val maxFallDistanceValue = IntValue("MaxFallDistance", 5, 0..100)

    private var lastGroundY: Double? = null
    private var lastCollisionY: Int? = null

    @EventTarget
    fun onMove(event: MoveEvent) {
        val player = mc.thePlayer ?: return
        if (player.capabilities.allowFlying || player.capabilities.isFlying
            || !mc.playerController.gameIsSurvivalOrAdventure()
        ) return

        if (!maxFallDistanceValue.isMinimal && player.onGround && getBlock(BlockPos(player).down()) !is BlockAir) {
            lastGroundY = player.posY
            lastCollisionY = FallingPlayer(player, true).findCollision(60)?.pos?.y
        }

        if (airSafe || player.onGround) {
            event.isSafeWalk = maxFallDistanceValue.isMinimal
                    || (lastGroundY != null && lastCollisionY != null
                    && lastGroundY!! - lastCollisionY!! > maxFallDistanceValue.get() + 1)
        }
    }
}
