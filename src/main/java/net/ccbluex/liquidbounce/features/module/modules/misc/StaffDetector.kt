/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.misc

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import net.ccbluex.liquidbounce.LiquidBounce.hud
import net.ccbluex.liquidbounce.LiquidBounce.CLIENT_CLOUD
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.events.PacketEvent
import net.ccbluex.liquidbounce.event.events.WorldEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Notifications.Notification
import net.ccbluex.liquidbounce.utils.ClientUtils.LOGGER
import net.ccbluex.liquidbounce.utils.ClientUtils.displayClientMessage
import net.ccbluex.liquidbounce.utils.misc.HttpUtils
import net.ccbluex.liquidbounce.value.BooleanValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.network.Packet
import net.minecraft.network.play.server.*

object StaffDetector : Module("StaffDetector", ModuleCategory.MISC, gameDetecting = false) {

    // TODO: Add more Staff Mode
    private val staffmode by ListValue("StaffMode", arrayOf("BlocksMC"), "BlocksMC")
    private val tab by BooleanValue("TAB", true) { staffmode == "BlocksMC" }
    private val packet by BooleanValue("Packet", true) { staffmode == "BlocksMC" }

    private val autoLeave by ListValue("AutoLeave", arrayOf("Off", "Leave", "Lobby", "Quit"), "Off") { tab || packet }

    private val spectator by BooleanValue("StaffSpectator", false) { staffmode == "BlocksMC" && (tab || packet) }
    private val otherSpectator by BooleanValue("OtherSpectator", false) { staffmode == "BlocksMC" && (tab || packet) }

    private val inGame by BooleanValue("InGame", true) { autoLeave != "Off" && staffmode == "BlocksMC" }
    private val warn by ListValue("Warn", arrayOf("Chat", "Notification"), "Chat")

    private val checkedStaff = mutableSetOf<String>()
    private val checkedSpectator = mutableSetOf<String>()
    private val playersInSpectatorMode = mutableSetOf<String>()

    private var attemptLeave = false

    /**
     * BlocksMC Staff List
     * Last Updated: 7/02/2024
     */
    private val blocksMCStaff: Map<String, Set<String>> by lazy {
        runBlocking {
            if (mc.thePlayer == null || mc.theWorld == null) {
                return@runBlocking emptyMap()
            }
            loadStaffList("$CLIENT_CLOUD/staffs/blocksmc.com")
        }
    }

    /**
     * Reset on World Change
     */
    @EventTarget
    fun onWorld(event: WorldEvent) {
        if (checkedStaff.isNotEmpty())
            checkedStaff.clear()

        if (checkedSpectator.isNotEmpty())
            checkedSpectator.clear()

        if (playersInSpectatorMode.isNotEmpty())
            playersInSpectatorMode.clear()
    }

    private fun checkedStaffRemoved() {
        val onlinePlayers = mc.netHandler?.playerInfoMap?.mapNotNull { it?.gameProfile?.name }

        onlinePlayers?.toSet()?.let { checkedStaff.retainAll(it) }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (mc.thePlayer == null || mc.theWorld == null) {
            return
        }

        val packet = event.packet

        /**
         * OLD BlocksMC Staff Spectator Check
         * Original By HU & Modified by Eclipses
         *
         * NOTE: Doesn't detect staff spectator all the time.
         */
        if (spectator && staffmode == "BlocksMC") {
            if (packet is S3EPacketTeams) {
                val teamName = packet.name

                if (teamName.equals("Z_Spectator", true)) {
                    val players = packet.players ?: return

                    val staffSpectateList = players.filter { it in blocksMCStaff } - checkedSpectator
                    val nonStaffSpectateList = players.filter { it !in blocksMCStaff } - checkedSpectator

                    // Check for players who are using spectator menu
                    val miscSpectatorList = playersInSpectatorMode - players.toSet()

                    staffSpectateList.forEach { player ->
                        notifySpectators(player)
                    }

                    nonStaffSpectateList.forEach { player ->
                        if (otherSpectator) {
                            notifySpectators(player)
                        }
                    }

                    miscSpectatorList.forEach { player ->
                        val isStaff = player in blocksMCStaff

                        if (isStaff && spectator) {
                            displayClientMessage("§d$player §3is using the spectator menu §e(compass/left)")
                        }

                        if (!isStaff && otherSpectator) {
                            displayClientMessage("§d$player §3is using the spectator menu §e(compass/left)")
                        }
                        checkedSpectator.remove(player)
                    }

                    // Update the set of players in spectator mode
                    playersInSpectatorMode.clear()
                    playersInSpectatorMode.addAll(players)
                }
            }

            // Handle other packets
            handleOtherChecks(packet)
        }
    }

    private fun notifySpectators(player: String) {
        if (mc.thePlayer == null || mc.theWorld == null) {
            return
        }

        val isStaff = player in blocksMCStaff

        if (isStaff && spectator) {
            if (warn == "Chat") {
                displayClientMessage("§d$player §3is a spectators")
            } else {
                hud.addNotification(Notification("§d$player §3is a spectators", 3000F))
            }
        }

        if (!isStaff && otherSpectator) {
            if (warn == "Chat") {
                displayClientMessage("§d$player §3is a spectators")
            } else {
                hud.addNotification(Notification("§d$player §3is a spectators", 3000F))
            }
        }

        attemptLeave = false
        checkedSpectator.add(player)

        if (isStaff) {
            autoLeave()
        }
    }

    /**
     * Check staff using TAB
     */
    private fun notifyStaff() {
        if (!tab)
            return

        if (mc.thePlayer == null || mc.theWorld == null) {
            return
        }

        mc.netHandler?.playerInfoMap?.mapNotNull { playerInfo ->
            val player = playerInfo?.gameProfile?.name ?: return@mapNotNull

            val isStaff = blocksMCStaff.any { entry ->
                entry.value.any { staffName -> player.contains(staffName) }
            }

            val condition = when {
                playerInfo.responseTime > 0 -> "§e(${playerInfo.responseTime}ms)"
                playerInfo.responseTime == 0 -> "§a(Joined)"
                else -> "§c(Ping error)"
            }

            val warnings = "§d$player §3is a staff §b(TAB) $condition"

            if (isStaff && player !in checkedStaff) {
                if (warn == "Chat") {
                    displayClientMessage(warnings)
                } else {
                    hud.addNotification(Notification(warnings, 3000F))
                }

                attemptLeave = false
                checkedStaff.add(player)

                autoLeave()
            }
        }
    }

    /**
     * Check staff using Packet
     */
    private fun notifyStaffPacket(staff: Entity) {
        if (!packet)
            return

        if (mc.thePlayer == null || mc.theWorld == null) {
            return
        }

        val isStaff = if (staff is EntityPlayer) {
            val playerName = staff.gameProfile.name

            blocksMCStaff.any { entry ->
                entry.value.any { staffName -> playerName.contains(staffName) }
            }
        } else {
            false
        }

        val condition = when (staff) {
            is EntityPlayer -> {
                val responseTime = mc.netHandler?.getPlayerInfo(staff.uniqueID)?.responseTime ?: 0
                when {
                    responseTime > 0 -> "§e(${responseTime}ms)"
                    responseTime == 0 -> "§a(Joined)"
                    else -> "§c(Ping error)"
                }
            }
            else -> ""
        }

        val playerName = if (staff is EntityPlayer) staff.gameProfile.name else ""

        val warnings = "§c[STAFF] §d${playerName} §3is a staff §b(Packet) $condition"

        if (isStaff && playerName !in checkedStaff) {
            if (warn == "Chat") {
                displayClientMessage(warnings)
            } else {
                hud.addNotification(Notification(warnings, 3000F))
            }

            attemptLeave = false
            checkedStaff.add(playerName)

            autoLeave()
        }
    }

    private fun autoLeave() {
        val firstSlotItemStack = mc.thePlayer.inventory.mainInventory[0] ?: return

        if (inGame && (firstSlotItemStack.item == Items.compass || firstSlotItemStack.item == Items.bow)) {
            return
        }

        if (!attemptLeave) {
            when (autoLeave.lowercase()) {
                "off" -> return
                "leave" -> mc.thePlayer.sendChatMessage("/leave")
                "lobby" -> mc.thePlayer.sendChatMessage("/lobby")
                "quit" -> mc.theWorld.sendQuittingDisconnectingPacket()
            }
        }
        attemptLeave = true
    }

    private fun handleOtherChecks(packet: Packet<*>?) {
        if (mc.thePlayer == null || mc.theWorld == null) {
            return
        }

        when (packet) {
            is S01PacketJoinGame -> handleStaff(mc.theWorld.getEntityByID(packet.entityId))
            is S0CPacketSpawnPlayer -> handleStaff(mc.theWorld.getEntityByID(packet.entityID))
            is S18PacketEntityTeleport -> handleStaff(mc.theWorld.getEntityByID(packet.entityId))
            is S1CPacketEntityMetadata -> handleStaff(mc.theWorld.getEntityByID(packet.entityId))
            is S1DPacketEntityEffect -> handleStaff(mc.theWorld.getEntityByID(packet.entityId))
            is S1EPacketRemoveEntityEffect -> handleStaff(mc.theWorld.getEntityByID(packet.entityId))
            is S19PacketEntityStatus -> handleStaff(mc.theWorld.getEntityByID(packet.entityId))
            is S19PacketEntityHeadLook -> handleStaff(packet.getEntity(mc.theWorld))
            is S49PacketUpdateEntityNBT -> handleStaff(packet.getEntity(mc.theWorld))
            is S1BPacketEntityAttach -> handleStaff(mc.theWorld.getEntityByID(packet.entityId))
            is S04PacketEntityEquipment -> handleStaff(mc.theWorld.getEntityByID(packet.entityID))
        }
    }

    private fun handleStaff(staff: Entity?) {
        mc.thePlayer ?: return
        mc.theWorld ?: return
        staff ?: return

        checkedStaffRemoved()

        notifyStaff()
        notifyStaffPacket(staff)
    }

    private suspend fun loadStaffList(url: String): Map<String, Set<String>> {
        try {
            val (response, code) = fetchDataAsync(url)

            if (code == 200) {
                val staffList = response.split("\n").filter { it.isNotBlank() && it.isNotEmpty() }.toSet()

                displayClientMessage("§aSuccessfully loaded §9${staffList.size} §astaff names.")
                return mapOf(url to staffList)
            } else displayClientMessage("§cFailed to load staff list. §9(ERROR CODE: $code)")
        } catch (e: Exception) {
            LOGGER.error("§cFailed to load staff list. §9(${e.message})")
            e.printStackTrace()
        }
        return emptyMap()
    }

    private suspend fun fetchDataAsync(url: String): Pair<String, Int> = coroutineScope {
        async(IO) {
            val (response, code) = HttpUtils.request(url, "GET")
            Pair(response, code)
        }.await()
    }

    /**
     * HUD TAG
     */
    override val tag
        get() = staffmode
}