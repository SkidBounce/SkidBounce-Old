/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.targets

import net.ccbluex.liquidbounce.event.events.AttackEvent
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.events.PacketEvent
import net.ccbluex.liquidbounce.event.events.WorldEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory.TARGETS
import net.ccbluex.liquidbounce.utils.extensions.getFullName
import net.ccbluex.liquidbounce.utils.render.ColorUtils.stripColor
import net.ccbluex.liquidbounce.value.BooleanValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.network.play.server.S0BPacketAnimation
import net.minecraft.network.play.server.S13PacketDestroyEntities
import net.minecraft.network.play.server.S14PacketEntity
import net.minecraft.network.play.server.S1CPacketEntityMetadata
import net.minecraft.network.play.server.S20PacketEntityProperties

object AntiBot : Module("AntiBot", TARGETS) {

    private val tab by BooleanValue("Tab", true)
    private val tabMode by ListValue("TabMode", arrayOf("Equals", "Contains"), "Contains") { tab }

    private val entityID by BooleanValue("EntityID", true)
    private val invalidUUID by BooleanValue("InvalidUUID", true)
    private val color by BooleanValue("Color", false)

    private val livingTime by BooleanValue("LivingTime", false)
    private val livingTimeTicks by IntValue("LivingTimeTicks", 40, 1..200) { livingTime }

    private val ground by BooleanValue("Ground", true)
    private val air by BooleanValue("Air", false)
    private val invalidGround by BooleanValue("InvalidGround", true)
    private val swing by BooleanValue("Swing", false)
    private val health by BooleanValue("Health", false)
    private val derp by BooleanValue("Derp", true)
    private val wasInvisible by BooleanValue("WasInvisible", false)
    private val armor by BooleanValue("Armor", false)
    private val ping by BooleanValue("Ping", false)
    private val needHit by BooleanValue("NeedHit", false)
    private val duplicateInWorld by BooleanValue("DuplicateInWorld", false)
    private val duplicateInTab by BooleanValue("DuplicateInTab", false)
    private val properties by BooleanValue("Properties", false)
    private val metadata by BooleanValue("Metadata", false)

    private val alwaysInRadius by BooleanValue("AlwaysInRadius", false)
    private val alwaysRadius by FloatValue("AlwaysInRadiusBlocks", 20f, 5f..30f) { alwaysInRadius }

    private val groundList = mutableListOf<Int>()
    private val airList = mutableListOf<Int>()
    private val invalidGroundList = mutableMapOf<Int, Int>()
    private val swingList = mutableListOf<Int>()
    private val invisibleList = mutableListOf<Int>()
    private val metadataList = mutableListOf<Int>()
    private val propertiesList = mutableListOf<Int>()
    private val hitList = mutableListOf<Int>()
    private val notAlwaysInRadiusList = mutableListOf<Int>()
    private val worldPlayerNames = mutableSetOf<String>()
    private val worldDuplicateNames = mutableSetOf<String>()
    private val tabPlayerNames = mutableSetOf<String>()
    private val tabDuplicateNames = mutableSetOf<String>()

    fun isBot(entity: Entity): Boolean {
        // Check if entity is a player
        if (entity !is EntityPlayer)
            return false

        // Check if anti bot is enabled
        if (!handleEvents())
            return false

        // Anti Bot checks

        if (color && "§" !in entity.displayName.formattedText.replace("§r", ""))
            return true

        if (livingTime && entity.ticksExisted < livingTimeTicks)
            return true

        if (ground && entity.entityId !in groundList)
            return true

        if (air && entity.entityId !in airList)
            return true

        if (swing && entity.entityId !in swingList)
            return true

        if (health && entity.health > 20F)
            return true

        if (entityID && (entity.entityId >= 1000000000 || entity.entityId <= -1))
            return true

        if (derp && (entity.rotationPitch > 90F || entity.rotationPitch < -90F))
            return true

        if (wasInvisible && entity.entityId in invisibleList)
            return true

        if (properties && entity.entityId !in propertiesList)
            return true

        if (metadata && entity.entityId !in metadataList)
            return true

        if (armor) {
            if (entity.inventory.armorInventory[0] == null && entity.inventory.armorInventory[1] == null &&
                entity.inventory.armorInventory[2] == null && entity.inventory.armorInventory[3] == null
            )
                return true
        }

        if (ping) {
            if (mc.netHandler.getPlayerInfo(entity.uniqueID)?.responseTime == 0 ||
                mc.netHandler.getPlayerInfo(entity.uniqueID)?.responseTime == null)
                return true
        }

        if (invalidUUID && mc.netHandler.getPlayerInfo(entity.uniqueID) == null) {
            return true
        }

        if (needHit && entity.entityId !in hitList)
            return true

        if (invalidGround && invalidGroundList.getOrDefault(entity.entityId, 0) >= 10)
            return true

        if (tab) {
            val equals = tabMode == "Equals"
            val targetName = stripColor(entity.displayName.formattedText)

            val shouldReturn = mc.netHandler.playerInfoMap.any { networkPlayerInfo ->
                val networkName = stripColor(networkPlayerInfo.getFullName())
                if (equals) {
                    targetName == networkName
                } else {
                    networkName in targetName
                }
            }
            return !shouldReturn
        }

        if (duplicateInWorld) {
            for (player in mc.theWorld.playerEntities.filterNotNull()) {
                val playerName = player.name

                if (worldPlayerNames.contains(playerName)) {
                    worldDuplicateNames.add(playerName)
                } else {
                    worldPlayerNames.add(playerName)
                }
            }

            if (worldDuplicateNames.isNotEmpty()) {
                val duplicateCount = worldDuplicateNames.size
                if (mc.theWorld.playerEntities.count { it.name in worldDuplicateNames } > duplicateCount) {
                    return true
                }
            }
        }

        if (duplicateInTab) {
            for (networkPlayerInfo in mc.netHandler.playerInfoMap.filterNotNull()) {
                val playerName = stripColor(networkPlayerInfo.getFullName())

                if (tabPlayerNames.contains(playerName)) {
                    tabDuplicateNames.add(playerName)
                } else {
                    tabPlayerNames.add(playerName)
                }
            }

            if (tabDuplicateNames.isNotEmpty()) {
                val duplicateCount = tabDuplicateNames.size
                if (mc.netHandler.playerInfoMap.count { stripColor(it.getFullName()) in tabDuplicateNames } > duplicateCount) {
                    return true
                }
            }
        }

        if (alwaysInRadius && entity.entityId !in notAlwaysInRadiusList)
            return true

        return entity.name.isEmpty() || entity.name == mc.thePlayer.name
    }

    @EventTarget(ignoreCondition = true)
    fun onPacket(event: PacketEvent) {
        if (mc.thePlayer == null || mc.theWorld == null)
            return

        when (val packet = event.packet) {
            is S14PacketEntity -> {
                val entity = packet.getEntity(mc.theWorld)

                if (entity is EntityPlayer) {
                    if (entity.onGround && entity.entityId !in groundList)
                        groundList += entity.entityId

                    if (!entity.onGround && entity.entityId !in airList)
                        airList += entity.entityId

                    if (entity.onGround) {
                        if (entity.fallDistance > 0.0 || entity.posY == entity.prevPosY) {
                            invalidGroundList[entity.entityId] = invalidGroundList.getOrDefault(entity.entityId, 0) + 1
                        } else if (!entity.isCollidedVertically) {
                            invalidGroundList[entity.entityId] = invalidGroundList.getOrDefault(entity.entityId, 0) + 1
                        }
                    } else {
                        val currentVL = invalidGroundList.getOrDefault(entity.entityId, 0)
                        if (currentVL > 0) {
                            invalidGroundList[entity.entityId] = currentVL - 1
                        } else {
                            invalidGroundList.remove(entity.entityId)
                        }
                    }

                    if ((entity.isInvisible || entity.isInvisibleToPlayer(mc.thePlayer)) && entity.entityId !in invisibleList)
                        invisibleList += entity.entityId

                    if (alwaysInRadius) {
                        val distance = mc.thePlayer.getDistanceToEntity(entity)

                        if (distance < alwaysRadius) {
                            if (entity.entityId in notAlwaysInRadiusList) {
                                notAlwaysInRadiusList.remove(entity.entityId)
                            }
                        } else {
                            if (entity.entityId !in notAlwaysInRadiusList) {
                                notAlwaysInRadiusList.add(entity.entityId)
                            }
                        }
                    }
                }
            }

            is S0BPacketAnimation -> {
                val entity = mc.theWorld.getEntityByID(packet.entityID)

                if (entity != null && entity is EntityLivingBase && packet.animationType == 0
                    && entity.entityId !in swingList
                )
                    swingList += entity.entityId
            }

            is S20PacketEntityProperties -> {
                propertiesList += packet.entityId
            }

            is S1CPacketEntityMetadata -> {
                metadataList += packet.entityId
            }

            is S13PacketDestroyEntities -> {
                for (entityID in packet.entityIDs) {
                    // Check if entityID exists in groundList and remove if found
                    if (entityID in groundList) groundList -= entityID

                    // Check if entityID exists in airList and remove if found
                    if (entityID in airList) airList -= entityID

                    // Check if entityID exists in invalidGroundList and remove if found
                    if (entityID in invalidGroundList) invalidGroundList -= entityID

                    // Check if entityID exists in swingList and remove if found
                    if (entityID in swingList) swingList -= entityID

                    // Check if entityID exists in invisibleList and remove if found
                    if (entityID in invisibleList) invisibleList -= entityID

                    // Check if entityID exists in notAlwaysInRadiusList and remove if found
                    if (entityID in notAlwaysInRadiusList) notAlwaysInRadiusList -= entityID
                }
            }
        }
    }

    @EventTarget(ignoreCondition = true)
    fun onAttack(e: AttackEvent) {
        val entity = e.targetEntity

        if (entity != null && entity is EntityLivingBase && entity.entityId !in hitList)
            hitList += entity.entityId
    }

    @EventTarget(ignoreCondition = true)
    fun onWorld(event: WorldEvent) {
        hitList.clear()
        swingList.clear()
        groundList.clear()
        invalidGroundList.clear()
        invisibleList.clear()
        metadataList.clear()
        notAlwaysInRadiusList.clear()
        worldPlayerNames.clear()
        worldDuplicateNames.clear()
        tabPlayerNames.clear()
        tabDuplicateNames.clear()
    }
}
