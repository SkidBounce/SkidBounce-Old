/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.utils

import net.ccbluex.liquidbounce.features.module.modules.targets.*
import net.ccbluex.liquidbounce.features.module.modules.targets.AntiBot.isBot
import net.ccbluex.liquidbounce.utils.extensions.*
import net.ccbluex.liquidbounce.utils.misc.StringUtils.contains
import net.minecraft.entity.*
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.Vec3
import kotlin.math.*

object EntityUtils : MinecraftInstance() {

    private val healthSubstrings = arrayOf("hp", "health", "❤", "lives")

    fun isSelected(entity: Entity?, canAttackCheck: Boolean): Boolean {
        if (entity is EntityLivingBase && (Dead.handleEvents() || entity.isEntityAlive) && entity != mc.thePlayer) {
            if (Invisible.handleEvents() || !entity.isInvisible) {
                if (Players.handleEvents() && entity is EntityPlayer) {
                    if (canAttackCheck) {
                        if (isBot(entity))
                            return false

                        if (entity.isClientFriend() && !Friends.handleEvents())
                            return false

                        if (entity.isSpectator) return false

                        return !Teams.handleEvents() || !Teams.isInYourTeam(entity)
                    }
                    return true
                }

                return Mobs.handleEvents() && entity.isMob() || Animals.handleEvents() && entity.isAnimal()
            }
        }
        return false
    }

    fun isLookingOnEntities(entity: Entity, maxAngleDifference: Double): Boolean {
        val player = mc.thePlayer ?: return false
        val playerRotation = player.rotationYawHead
        val playerPitch = player.rotationPitch

        val maxAngleDifferenceRadians = Math.toRadians(maxAngleDifference)

        val lookVec = Vec3(
            -sin(playerRotation.toRadiansD()),
            -sin(playerPitch.toRadiansD()),
            cos(playerRotation.toRadiansD())
        ).normalize()

        val playerPos = player.positionVector.addVector(0.0, player.eyeHeight.toDouble(), 0.0)
        val entityPos = entity.positionVector.addVector(0.0, entity.eyeHeight.toDouble(), 0.0)

        val directionToEntity = entityPos.subtract(playerPos).normalize()
        val dotProductThreshold = lookVec.dotProduct(directionToEntity)

        return dotProductThreshold > cos(maxAngleDifferenceRadians)
    }

    fun getHealth(entity: EntityLivingBase, fromScoreboard: Boolean = false, absorption: Boolean = true): Float {
        if (fromScoreboard && entity is EntityPlayer) run {
            val scoreboard = entity.worldScoreboard
            val objective = scoreboard.getValueFromObjective(entity.name, scoreboard.getObjectiveInDisplaySlot(2))

            if (healthSubstrings !in objective.objective?.displayName)
                return@run

            val scoreboardHealth = objective.scorePoints

            if (scoreboardHealth > 0)
                return scoreboardHealth.toFloat()
        }

        var health = entity.health

        if (absorption)
            health += entity.absorptionAmount

        return if (health > 0) health else 20f
    }

}
