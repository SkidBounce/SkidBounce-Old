/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.utils.extensions

import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraft.network.play.server.*
import kotlin.math.roundToInt

val C08PacketPlayerBlockPlacement.isUse get() = placedBlockDirection == 255

var S12PacketEntityVelocity.realMotionX
    get() = motionX / 8000.0
    set(value) { motionX = (value * 8000.0).roundToInt() }
var S12PacketEntityVelocity.realMotionY
    get() = motionY / 8000.0
    set(value) { motionX = (value * 8000.0).roundToInt() }
var S12PacketEntityVelocity.realMotionZ
    get() = motionZ / 8000.0
    set(value) { motionX = (value * 8000.0).roundToInt() }

val S14PacketEntity.realMotionX
    get() = func_149062_c() / 32.0
val S14PacketEntity.realMotionY
    get() = func_149061_d() / 32.0
val S14PacketEntity.realMotionZ
    get() = func_149064_e() / 32.0

var S0EPacketSpawnObject.realX
    get() = x / 32.0
    set(value) { x = (value * 32.0).roundToInt() }
var S0EPacketSpawnObject.realY
    get() = y / 32.0
    set(value) { y = (value * 32.0).roundToInt() }
var S0EPacketSpawnObject.realZ
    get() = z / 32.0
    set(value) { z = (value * 32.0).roundToInt() }

val S0CPacketSpawnPlayer.realX
    get() = x / 32.0
val S0CPacketSpawnPlayer.realY
    get() = y / 32.0
val S0CPacketSpawnPlayer.realZ
    get() = z / 32.0

val S0FPacketSpawnMob.realX
    get() = x / 32.0
val S0FPacketSpawnMob.realY
    get() = y / 32.0
val S0FPacketSpawnMob.realZ
    get() = z / 32.0

val S18PacketEntityTeleport.realX
    get() = x / 32.0
val S18PacketEntityTeleport.realY
    get() = y / 32.0
val S18PacketEntityTeleport.realZ
    get() = z / 32.0
