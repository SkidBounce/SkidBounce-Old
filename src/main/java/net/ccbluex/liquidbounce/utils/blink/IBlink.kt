/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.utils.blink

import net.ccbluex.liquidbounce.utils.blink.BlinkHandler.clientBlinkStates
import net.ccbluex.liquidbounce.utils.blink.BlinkHandler.serverBlinkStates

interface IBlink {
    var blinkingClient: Boolean
        get() = this in clientBlinkStates
        set(value) {
            if (value) clientBlinkStates.add(this)
            else clientBlinkStates.remove(this)
        }

    var blinkingServer: Boolean
        get() = this in serverBlinkStates
        set(value) {
            if (value) serverBlinkStates.add(this)
            else serverBlinkStates.remove(this)
        }

    fun release(client: Boolean = true, server: Boolean = true, overrideBlink: Boolean = false) {
        BlinkHandler.release(client, server, overrideBlink)
    }
}
