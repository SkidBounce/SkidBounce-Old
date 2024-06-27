/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.utils.extensions

import net.ccbluex.liquidbounce.utils.PacketUtils.PacketBuffer
import net.ccbluex.liquidbounce.utils.Rotation
import net.minecraft.network.INetHandler
import net.minecraft.network.Packet
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition
import net.minecraft.network.play.client.C03PacketPlayer.C05PacketPlayerLook
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook
import net.minecraft.network.play.client.C07PacketPlayerDigging
import net.minecraft.network.play.client.C07PacketPlayerDigging.Action.*
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraft.network.play.server.*
import net.minecraft.network.play.server.S14PacketEntity.S15PacketEntityRelMove
import net.minecraft.network.play.server.S14PacketEntity.S16PacketEntityLook
import net.minecraft.network.play.server.S14PacketEntity.S17PacketEntityLookMove
import kotlin.math.roundToInt

val <T : INetHandler> Packet<T>.actual: Packet<T>
    get() {
        val buffer = PacketBuffer()
        this.writePacketData(buffer)

        // this is safe because all packets have an empty constructor
        val packet = this::class.java.newInstance()
        packet.readPacketData(buffer)

        return packet
    }
val Packet<*>.isUse get() = this is C08PacketPlayerBlockPlacement && placedBlockDirection == 255
val Packet<*>.isRelease get() = this is C07PacketPlayerDigging && status == RELEASE_USE_ITEM
val C03PacketPlayer.hasPosition get() = this is C06PacketPlayerPosLook || this is C04PacketPlayerPosition
val C03PacketPlayer.hasRotation get() = this is C06PacketPlayerPosLook || this is C05PacketPlayerLook
val S14PacketEntity.hasPosition get() = this is S15PacketEntityRelMove || this is S17PacketEntityLookMove
val S14PacketEntity.hasRotation get() = this is S16PacketEntityLook || this is S17PacketEntityLookMove
val C03PacketPlayer.rotation get() = Rotation(yaw, pitch)

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
