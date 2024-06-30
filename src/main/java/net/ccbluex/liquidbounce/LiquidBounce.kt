/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce

import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import net.ccbluex.liquidbounce.api.ClientUpdate.gitInfo
import net.ccbluex.liquidbounce.cape.CapeService
import net.ccbluex.liquidbounce.event.EventManager
import net.ccbluex.liquidbounce.event.EventManager.callEvent
import net.ccbluex.liquidbounce.event.EventManager.registerListener
import net.ccbluex.liquidbounce.event.events.*
import net.ccbluex.liquidbounce.features.command.CommandManager
import net.ccbluex.liquidbounce.features.command.CommandManager.registerCommands
import net.ccbluex.liquidbounce.features.module.ModuleManager
import net.ccbluex.liquidbounce.features.module.ModuleManager.registerModules
import net.ccbluex.liquidbounce.features.special.*
import net.ccbluex.liquidbounce.features.special.ClientRichPresence.showRichPresenceValue
import net.ccbluex.liquidbounce.file.FileManager
import net.ccbluex.liquidbounce.file.FileManager.loadAllConfigs
import net.ccbluex.liquidbounce.file.FileManager.saveAllConfigs
import net.ccbluex.liquidbounce.lang.LanguageManager.loadLanguages
import net.ccbluex.liquidbounce.script.ScriptManager
import net.ccbluex.liquidbounce.script.ScriptManager.enableScripts
import net.ccbluex.liquidbounce.script.ScriptManager.loadScripts
import net.ccbluex.liquidbounce.script.remapper.Remapper
import net.ccbluex.liquidbounce.script.remapper.Remapper.loadSrg
import net.ccbluex.liquidbounce.tabs.*
import net.ccbluex.liquidbounce.ui.client.GuiClientConfiguration.Companion.updateClientWindow
import net.ccbluex.liquidbounce.ui.client.altmanager.GuiAltManager.Companion.loadActiveGenerators
import net.ccbluex.liquidbounce.ui.client.clickgui.ClickGui
import net.ccbluex.liquidbounce.ui.client.hud.HUD
import net.ccbluex.liquidbounce.ui.font.Fonts.loadFonts
import net.ccbluex.liquidbounce.utils.*
import net.ccbluex.liquidbounce.utils.ClientUtils.LOGGER
import net.ccbluex.liquidbounce.utils.ClientUtils.disableFastRender
import net.ccbluex.liquidbounce.utils.background.Background
import net.ccbluex.liquidbounce.utils.inventory.InventoryUtils
import net.ccbluex.liquidbounce.utils.render.MiniMapRegister
import net.ccbluex.liquidbounce.utils.timing.TickedActions
import kotlin.concurrent.thread

object LiquidBounce {

    // Client information
    const val CLIENT_NAME = "SkidBounce"
    val clientVersionText = gitInfo["git.build.version"]?.toString() ?: "unknown"
    val clientCommit = gitInfo["git.commit.id.abbrev"]?.let { "git-$it" } ?: "unknown"
    const val IN_DEV = true
    const val CLIENT_CREATOR = "CCBlueX"
    const val CLIENT_WEBSITE = "liquidbounce.net"
    const val CLIENT_CLOUD = "https://cloud.liquidbounce.net/LiquidBounce"

    val clientTitle = "$CLIENT_NAME $clientVersionText ${if (IN_DEV) clientCommit else ""}"

    var isStarting = true

    // Managers
    val moduleManager = ModuleManager
    val commandManager = CommandManager
    val eventManager = EventManager
    val fileManager = FileManager
    val scriptManager = ScriptManager

    // HUD & ClickGUI
    val hud = HUD

    val clickGui = ClickGui

    // Menu Background
    var background: Background? = null

    // Discord RPC
    val clientRichPresence = ClientRichPresence

    /**
     * Execute if client will be started
     */
    fun startClient() {
        isStarting = true

        LOGGER.info("Starting $CLIENT_NAME $clientVersionText ${if (IN_DEV) clientCommit else ""}")

        runBlocking {
            runCatching {
                async {
                    loadLanguages()

                    registerListener(RotationUtils)
                    registerListener(ClientFixes)
                    registerListener(BungeeCordSpoof)
                    registerListener(CapeService)
                    registerListener(InventoryUtils)
                    registerListener(MiniMapRegister)
                    registerListener(TickedActions)
                    registerListener(MovementUtils)
                    registerListener(PacketUtils)
                    registerListener(TimerBalanceUtils)
                    registerListener(BPSUtils)

                    loadFonts()

                    registerCommands()

                    registerModules()

                    runCatching {
                        // Remapper
                        loadSrg()

                        if (!Remapper.mappingsLoaded) {
                            error("Failed to load SRG mappings.")
                        }

                        // ScriptManager
                        loadScripts()
                        enableScripts()
                    }.onFailure {
                        LOGGER.error("Failed to load scripts.", it)
                    }

                    loadAllConfigs()

                    updateClientWindow()

                    // Tabs
                    BlocksTab
                    ExploitsTab
                    HeadsTab

                    disableFastRender()

                    // Load alt generators
                    loadActiveGenerators()

                    // Setup Discord RPC
                    if (showRichPresenceValue) {
                        thread {
                            try {
                                clientRichPresence.setup()
                            } catch (throwable: Throwable) {
                                LOGGER.error("Failed to setup Discord RPC.", throwable)
                            }
                        }
                    }

                    // Login into known token if not empty
                    if (CapeService.knownToken.isNotBlank()) {
                        runCatching {
                            CapeService.login(CapeService.knownToken)
                        }.onFailure {
                            LOGGER.error("Failed to login into known cape token.", it)
                        }.onSuccess {
                            LOGGER.info("Successfully logged in into known cape token.")
                        }
                    }

                    // Refresh cape service
                    CapeService.refreshCapeCarriers {
                        LOGGER.info("Successfully loaded ${CapeService.capeCarriers.size} cape carriers.")
                    }
                }.await() // Wait to load

                // Load background
                FileManager.loadBackground()
            }.onFailure {
                LOGGER.error("Failed to start client ${it.message}")
            }.onSuccess {
                // Set is starting status
                isStarting = false

                callEvent(StartupEvent())
                LOGGER.info("Successfully started client")
            }
        }

        // Load background
        runCatching {
            FileManager.loadBackground()
        }

        isStarting = false

        callEvent(StartupEvent())
    }

    /**
     * Execute if client will be stopped
     */
    fun stopClient() {
        // Call client shutdown
        callEvent(ClientShutdownEvent())

        // Save all available configs
        saveAllConfigs()

        // Shutdown discord rpc
        clientRichPresence.shutdown()
    }
}
