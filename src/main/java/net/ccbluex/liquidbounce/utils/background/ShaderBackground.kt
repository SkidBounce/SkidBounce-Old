/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.utils.background

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.ccbluex.liquidbounce.utils.ClientUtils.LOGGER
import net.ccbluex.liquidbounce.utils.render.shader.Shader
import net.ccbluex.liquidbounce.utils.render.shader.shaders.BackgroundShader
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats.POSITION
import java.io.File
import java.util.concurrent.*

class ShaderBackground(backgroundFile: File) : Background(backgroundFile) {

    private var shaderInitialized = false
    private lateinit var shader: Shader
    private val initializationLatch = CountDownLatch(1)

    override fun initBackground() {
        GlobalScope.launch {
            runCatching {
                shader = BackgroundShader(backgroundFile)
            }.onFailure {
                LOGGER.error("Failed to load background.", it)
            }.onSuccess {
                initializationLatch.countDown()
                shaderInitialized = true
                LOGGER.info("Successfully loaded background.")
            }
        }
    }

    override fun drawBackground(width: Int, height: Int) {
        if (!shaderInitialized) {
            runCatching {
                initializationLatch.await()
            }.onFailure {
                LOGGER.error(it.message)
                return
            }
        }

        if (shaderInitialized) {
            shader.startShader()

            val instance = Tessellator.getInstance()
            val worldRenderer = instance.worldRenderer
            worldRenderer.begin(7, POSITION)
            worldRenderer.pos(0.0, height.toDouble(), 0.0).endVertex()
            worldRenderer.pos(width.toDouble(), height.toDouble(), 0.0).endVertex()
            worldRenderer.pos(width.toDouble(), 0.0, 0.0).endVertex()
            worldRenderer.pos(0.0, 0.0, 0.0).endVertex()
            instance.draw()

            shader.stopShader()
        }
    }
}
