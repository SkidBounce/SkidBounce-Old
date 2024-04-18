/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.utils

import net.ccbluex.liquidbounce.utils.render.shader.Shader
import net.ccbluex.liquidbounce.utils.render.shader.shaders.BackgroundShader
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import java.io.File

class ShaderBackground(backgroundFile: File) : Background(backgroundFile) {

    private lateinit var shader: Shader

    override fun initBackground() {
        shader = BackgroundShader(backgroundFile)
    }

    override fun drawBackground(width: Int, height: Int) {
        shader.startShader()

        val instance = Tessellator.getInstance()
        val worldRenderer = instance.worldRenderer
        worldRenderer.begin(7, DefaultVertexFormats.POSITION)
        worldRenderer.pos(0.0, height.toDouble(), 0.0).endVertex()
        worldRenderer.pos(width.toDouble(), height.toDouble(), 0.0).endVertex()
        worldRenderer.pos(width.toDouble(), 0.0, 0.0).endVertex()
        worldRenderer.pos(0.0, 0.0, 0.0).endVertex()
        instance.draw()

        shader.stopShader()
    }
}
