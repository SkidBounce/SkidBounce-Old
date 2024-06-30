/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.utils.background

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.io.File

abstract class Background(val backgroundFile: File) {
    companion object {
        fun createBackground(backgroundFile: File): Background = runBlocking {
            CoroutineScope(Default).async {
                val background = when (backgroundFile.extension) {
                    "png" -> ImageBackground(backgroundFile)
                    "frag", "glsl", "shader" -> ShaderBackground(backgroundFile)
                    else -> throw IllegalArgumentException("Invalid background file extension")
                }

                background.initBackground()
                background
            }.await()
        }
    }

    protected abstract fun initBackground()

    abstract fun drawBackground(width: Int, height: Int)
}
