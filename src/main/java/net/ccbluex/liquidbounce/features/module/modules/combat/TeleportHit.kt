/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.event.EventState.PRE
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.events.MotionEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory.COMBAT
import net.ccbluex.liquidbounce.utils.EntityUtils.isSelected
import net.ccbluex.liquidbounce.utils.PacketUtils.sendPacket
import net.ccbluex.liquidbounce.utils.PathUtils.findPath
import net.ccbluex.liquidbounce.utils.RaycastUtils.raycastEntity
import net.ccbluex.liquidbounce.utils.Rotation
import net.ccbluex.liquidbounce.utils.RotationUtils.getVectorForRotation
import net.ccbluex.liquidbounce.utils.extensions.jmp
import net.minecraft.entity.EntityLivingBase
import net.minecraft.network.play.client.C02PacketUseEntity
import net.minecraft.network.play.client.C02PacketUseEntity.Action.ATTACK
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition

class TeleportHit : Module("TeleportHit", COMBAT) {
    private var targetEntity: EntityLivingBase? = null
    private var shouldHit = false

    @EventTarget
    fun onMotion(event: MotionEvent) {
        if (event.eventState != PRE) return

        val facedEntity = raycastEntity(100.0) { it is EntityLivingBase }

        mc.thePlayer ?: return

        if (mc.gameSettings.keyBindAttack.isKeyDown && isSelected(facedEntity, true)) {
            if (facedEntity!!.getDistanceSqToEntity(mc.thePlayer) >= 1) targetEntity = facedEntity as EntityLivingBase?
        }

        if (targetEntity != null) {
            if (!shouldHit) {
                shouldHit = true
                return
            }

            if (mc.thePlayer.fallDistance > 0f) {
                val rotationVector = getVectorForRotation(Rotation(mc.thePlayer.rotationYaw, 0f))
                val x = mc.thePlayer.posX + rotationVector.xCoord * (mc.thePlayer.getDistanceToEntity(targetEntity) - 1f)
                val z = mc.thePlayer.posZ + rotationVector.zCoord * (mc.thePlayer.getDistanceToEntity(targetEntity) - 1f)
                val y = targetEntity!!.position.y + 0.25

                findPath(x, y + 1, z, 4.0).forEach {
                    sendPacket(C04PacketPlayerPosition(it.x, it.y, it.z, false))
                }

                mc.thePlayer.swingItem()
                sendPacket(C02PacketUseEntity(targetEntity, ATTACK))
                mc.thePlayer.onCriticalHit(targetEntity)
                shouldHit = false
                targetEntity = null
            } else if (mc.thePlayer.onGround) mc.thePlayer.jmp()
        } else shouldHit = false
    }
}
