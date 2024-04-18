/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.ui.client.hud.element

/**
 * CustomHUD Side
 *
 * Allows to change default x and y position by side
 */
class Side(var horizontal: Horizontal, var vertical: Vertical) {

    companion object {
        /**
         * Default element side
         */
        fun default() = Side(Horizontal.LEFT, Vertical.UP)
    }

    /**
     * Horizontal side
     */
    enum class Horizontal(val sideName: String) {

        LEFT("Left"),
        MIDDLE("Middle"),
        RIGHT("Right");

        companion object {
            fun getByName(name: String) = values().find { it.sideName == name }
        }
    }

    /**
     * Vertical side
     */
    enum class Vertical(val sideName: String) {

        UP("Up"),
        MIDDLE("Middle"),
        DOWN("Down");

        companion object {
            fun getByName(name: String) = values().find { it.sideName == name }
        }
    }
}
