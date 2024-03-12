/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.client

import net.ccbluex.liquidbounce.LiquidBounce.hud
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory.CLIENT
import net.ccbluex.liquidbounce.features.module.ModuleManager.modules
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Notification
import net.ccbluex.liquidbounce.utils.ClientUtils.displayClientMessage
import net.ccbluex.liquidbounce.value.BoolValue
import net.minecraft.network.play.server.S08PacketPlayerPosLook

/**
 * @see net.ccbluex.liquidbounce.features.command.commands.AutoDisableCommand
 * @see net.ccbluex.liquidbounce.features.module.AutoDisable
 * @see net.ccbluex.liquidbounce.file.configs.ModulesConfig
 */
object AutoDisable : Module("AutoDisable", CLIENT, canBeEnabled = false) {
    private val chat by BoolValue("Chat", true)
    private val notification by BoolValue("Notification", false)

    @EventTarget
    fun onPacket(event: PacketEvent) {
        when (event.packet) {
            is S08PacketPlayerPosLook -> modules.forEach { disable("Flag", it.AutoDisable.flag, it) }
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (mc.thePlayer?.isDead != false)
            modules.forEach { disable("Death", it.AutoDisable.death, it) }
    }

    @EventTarget
    fun onWorld(event: WorldEvent) {
        modules.forEach { disable("World Change", it.AutoDisable.world, it) }
    }

    private fun disable(reason: String, disable: Boolean, module: Module) {
        if (!disable || !module.state)
            return

        // Disable Module
        module.state = false
        module.onDisable()

        // TODO: translations
        val name = module.getName()
        if (chat) displayClientMessage("§cDisabled §a$name §cdue to §a$reason")
        if (notification) hud.addNotification(Notification("Disabled $name due to $reason", 2000F))
    }

    override fun handleEvents() = true
}
