/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat.criticalsmodes

import net.ccbluex.liquidbounce.event.events.PacketEvent
import net.ccbluex.liquidbounce.utils.MinecraftInstance
import net.ccbluex.liquidbounce.utils.PacketUtils
import net.ccbluex.liquidbounce.utils.extensions.component1
import net.ccbluex.liquidbounce.utils.extensions.component2
import net.ccbluex.liquidbounce.utils.extensions.component3
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.potion.Potion

open class CriticalsMode(val modeName: String) : MinecraftInstance() {
    open fun onUpdate() {}
    open fun onAttack(entity: Entity) {}
    open fun onPacket(event: PacketEvent) {}

    protected fun sendPacket(x: Double, y: Double, z: Double, ground: Boolean) {
        val (pX, pY, pZ) = mc.thePlayer
        PacketUtils.sendPacket(C03PacketPlayer.C04PacketPlayerPosition(pX + x, pY + y, pZ + z, ground))
    }

    protected fun sendPacket(y: Double, ground: Boolean) = sendPacket(0.0, y, 0.0, ground)

    protected fun crit(entity: Entity) {
        mc.thePlayer.run {
            if (fallDistance > 0.0F
                && !onGround
                && !isOnLadder
                && !isInWater
                && !isPotionActive(Potion.blindness)
                && ridingEntity == null
                && entity is EntityLivingBase
            ) return

            onCriticalHit(entity)
        }
    }
}
