/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module

/**
 * @see net.ccbluex.liquidbounce.features.command.commands.AutoDisableCommand
 * @see net.ccbluex.liquidbounce.features.module.modules.client.AutoDisable
 * @see net.ccbluex.liquidbounce.file.configs.ModulesConfig
 */
class AutoDisable {
    var world = false
    var death = false
    var flag = false

    fun disable() {
        world = false
        death = false
        flag = false
    }
}
