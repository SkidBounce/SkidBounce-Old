/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.utils

import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.texture.DynamicTexture
import java.io.File
import javax.imageio.ImageIO

class ImageBackground(backgroundFile: File) : Background(backgroundFile) {

    private val resourceLocation = ClientUtils.resource("background.png")

    override fun initBackground() {
        val image = ImageIO.read(backgroundFile.inputStream())
        MinecraftInstance.mc.textureManager.loadTexture(resourceLocation, DynamicTexture(image))
    }

    override fun drawBackground(width: Int, height: Int) {
        MinecraftInstance.mc.textureManager.bindTexture(resourceLocation)
        GlStateManager.color(1f, 1f, 1f, 1f)
        Gui.drawScaledCustomSizeModalRect(0, 0, 0f, 0f, width, height, width, height, width.toFloat(), height.toFloat())
    }
}
