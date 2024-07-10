/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.file.configs

import com.google.gson.JsonNull
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import me.liuli.elixir.utils.set
import net.ccbluex.liquidbounce.LiquidBounce.moduleManager
import net.ccbluex.liquidbounce.file.FileConfig
import net.ccbluex.liquidbounce.file.FileManager.PRETTY_GSON
import java.io.*

class ModulesConfig(file: File) : FileConfig(file) {

    /**
     * Load config from file
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    override fun loadConfig() {
        val jsonElement = JsonParser().parse(file.bufferedReader())
        if (jsonElement is JsonNull) return

        for ((key, value) in jsonElement.asJsonObject.entrySet().iterator()) {
            val module = moduleManager[key] ?: continue

            val jsonModule = value as JsonObject
            module.state = jsonModule["State"].asBoolean
            module.keyBind = jsonModule["KeyBind"].asInt

            if (jsonModule.has("AutoDisable")) {
                module.AutoDisable.flag = jsonModule["AutoDisable"].asJsonObject["flag"].asBoolean
                module.AutoDisable.world = jsonModule["AutoDisable"].asJsonObject["world"].asBoolean
                module.AutoDisable.death = jsonModule["AutoDisable"].asJsonObject["death"].asBoolean
            }
        }
    }

    /**
     * Save config to file
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    override fun saveConfig() {
        val jsonObject = JsonObject()
        for (module in moduleManager.modules) {
            jsonObject[module.name] = JsonObject().apply {
                addProperty("State", module.state)
                addProperty("KeyBind", module.keyBind)
                this["AutoDisable"] = JsonObject().apply {
                    addProperty("flag", module.AutoDisable.flag)
                    addProperty("world", module.AutoDisable.world)
                    addProperty("death", module.AutoDisable.death)
                }
            }
        }
        file.writeText(PRETTY_GSON.toJson(jsonObject))
    }
}
