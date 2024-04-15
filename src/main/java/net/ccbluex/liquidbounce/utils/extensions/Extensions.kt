/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.utils.extensions

import net.ccbluex.liquidbounce.file.FileManager.friendsConfig
import net.ccbluex.liquidbounce.utils.MinecraftInstance.Companion.mc
import net.ccbluex.liquidbounce.utils.MovementUtils.JUMP_HEIGHT
import net.ccbluex.liquidbounce.utils.MovementUtils.getJumpBoostModifier
import net.ccbluex.liquidbounce.utils.PacketUtils.sendPacket
import net.ccbluex.liquidbounce.utils.PotionUtils.Potions
import net.ccbluex.liquidbounce.utils.PotionUtils.Potions.*
import net.ccbluex.liquidbounce.utils.Rotation
import net.ccbluex.liquidbounce.utils.RotationUtils.getFixedSensitivityAngle
import net.ccbluex.liquidbounce.utils.block.BlockUtils
import net.ccbluex.liquidbounce.utils.inventory.InventoryUtils.serverSlot
import net.ccbluex.liquidbounce.utils.render.ColorUtils.stripColor
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.network.NetworkPlayerInfo
import net.minecraft.client.settings.GameSettings
import net.minecraft.client.settings.GameSettings.isKeyDown
import net.minecraft.client.settings.KeyBinding
import net.minecraft.entity.Entity
import net.minecraft.entity.boss.EntityDragon
import net.minecraft.entity.monster.EntityGhast
import net.minecraft.entity.monster.EntityGolem
import net.minecraft.entity.monster.EntityMob
import net.minecraft.entity.monster.EntitySlime
import net.minecraft.entity.passive.EntityAnimal
import net.minecraft.entity.passive.EntityBat
import net.minecraft.entity.passive.EntitySquid
import net.minecraft.entity.passive.EntityVillager
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraft.network.play.client.C0APacketAnimation
import net.minecraft.potion.Potion
import net.minecraft.potion.Potion.*
import net.minecraft.potion.PotionEffect
import net.minecraft.stats.StatList.jumpStat
import net.minecraft.util.*
import net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem
import kotlin.math.pow

fun NetworkPlayerInfo.getFullName(): String {
    if (displayName != null)
        return displayName.formattedText

    val team = playerTeam
    val name = gameProfile.name
    return team?.formatString(name) ?: name
}

/**
 * Get block by position
 */
fun BlockPos.getBlock() = BlockUtils.getBlock(this)

/**
 * Get vector of block position
 */
fun BlockPos.getVec() = Vec3(x + 0.5, y + 0.5, z + 0.5)
fun BlockPos.toVec() = Vec3(this)
fun BlockPos.isReplaceable() = BlockUtils.isReplaceable(this)
fun BlockPos.canBeClicked() = BlockUtils.canBeClicked(this)

/**
 * Provides:
 * ```
 * val (x, y, z) = blockPos
 */
operator fun Vec3i.component1() = x
operator fun Vec3i.component2() = y
operator fun Vec3i.component3() = z

/**
 * Provides:
 * ```
 * val (x, y, z) = vec
 */
operator fun Vec3.component1() = xCoord
operator fun Vec3.component2() = yCoord
operator fun Vec3.component3() = zCoord

/**
 * Provides:
 * ```
 * val (x, y, z) = mc.thePlayer
 */
operator fun Entity.component1() = posX
operator fun Entity.component2() = posY
operator fun Entity.component3() = posZ

/**
 * Provides:
 * ```
 * val (width, height) = ScaledResolution(mc)
 */
operator fun ScaledResolution.component1() = this.scaledWidth
operator fun ScaledResolution.component2() = this.scaledHeight

/**
 * Provides:
 * `vec + othervec`, `vec - othervec`, `vec * number`
 * */
operator fun Vec3.plus(vec: Vec3): Vec3 = add(vec)
operator fun Vec3.minus(vec: Vec3): Vec3 = subtract(vec)
operator fun Vec3.times(number: Double) = Vec3(xCoord * number, yCoord * number, zCoord * number)
operator fun Vec3.div(number: Double) = times(1 / number)

fun Vec3.toFloatTriple() = Triple(xCoord.toFloat(), yCoord.toFloat(), zCoord.toFloat())

fun Float.toRadians() = this * 0.017453292f
fun Float.toRadiansD() = toRadians().toDouble()
fun Float.toDegrees() = this * 57.29578f
fun Float.toDegreesD() = toDegrees().toDouble()

fun Double.toRadians() = this * 0.017453292
fun Double.toRadiansF() = toRadians().toFloat()
fun Double.toDegrees() = this * 57.295779513
fun Double.toDegreesF() = toDegrees().toFloat()

/**
 * Provides: (step is 0.1 by default)
 * ```
 *      for (x in 0.1..0.9 step 0.05) {}
 *      for (y in 0.1..0.9) {}
 */
class RangeIterator(private val range: ClosedFloatingPointRange<Double>, private val step: Double = 0.1): Iterator<Double> {
    private var value = range.start

    override fun hasNext() = value < range.endInclusive

    override fun next(): Double {
        val returned = value
        value = (value + step).coerceAtMost(range.endInclusive)
        return returned
    }
}
operator fun ClosedFloatingPointRange<Double>.iterator() = RangeIterator(this)
infix fun ClosedFloatingPointRange<Double>.step(step: Double) = RangeIterator(this, step)

fun ClosedFloatingPointRange<Float>.random(): Double {
    require(start.isFinite())
    require(endInclusive.isFinite())
    return start + (endInclusive - start) * Math.random()
}

/**
 * Conditionally shuffles an `Iterable`
 * @param shuffle determines if the returned `Iterable` is shuffled
 */
fun <T> Iterable<T>.shuffled(shuffle: Boolean) = toMutableList().apply { if (shuffle) shuffle() }

fun AxisAlignedBB.lerpWith(x: Double, y: Double, z: Double) =
    Vec3(minX + (maxX - minX) * x, minY + (maxY - minY) * y, minZ + (maxZ - minZ) * z)

fun AxisAlignedBB.lerpWith(point: Vec3) = lerpWith(point.xCoord, point.yCoord, point.zCoord)
fun AxisAlignedBB.lerpWith(value: Double) = lerpWith(value, value, value)

operator fun AxisAlignedBB.component1() = maxX
operator fun AxisAlignedBB.component2() = maxY
operator fun AxisAlignedBB.component3() = maxZ
operator fun AxisAlignedBB.component4() = minX
operator fun AxisAlignedBB.component5() = minY
operator fun AxisAlignedBB.component6() = minZ

val AxisAlignedBB.center
    get() = lerpWith(0.5)

fun Block.lerpWith(x: Double, y: Double, z: Double) = Vec3(
    blockBoundsMinX + (blockBoundsMaxX - blockBoundsMinX) * x,
    blockBoundsMinY + (blockBoundsMaxY - blockBoundsMinY) * y,
    blockBoundsMinZ + (blockBoundsMaxZ - blockBoundsMinZ) * z
)

/**
 * @see kotlin.math.round
 */
fun Number.round(digits: Number = 0, base: Number = 10): Double {
    val multi = base.toDouble().pow(digits.toDouble())
    return kotlin.math.round(this.toDouble() * multi) / multi
}

infix fun <T> Collection<T?>.overlapsWith(other: Collection<T?>) = this.any { it in other }

/**
 * Allows to get the distance between the current entity and [entity] from the nearest corner of the bounding box
 */
fun Entity.getDistanceToEntityBox(entity: Entity) = eyes.distanceTo(getNearestPointBB(eyes, entity.hitBox))

fun Entity.getDistanceToBox(box: AxisAlignedBB) = eyes.distanceTo(getNearestPointBB(eyes, box))

fun getNearestPointBB(eye: Vec3, box: AxisAlignedBB): Vec3 {
    val origin = doubleArrayOf(eye.xCoord, eye.yCoord, eye.zCoord)
    val destMins = doubleArrayOf(box.minX, box.minY, box.minZ)
    val destMaxs = doubleArrayOf(box.maxX, box.maxY, box.maxZ)
    for (i in 0..2) {
        if (origin[i] > destMaxs[i]) origin[i] = destMaxs[i] else if (origin[i] < destMins[i]) origin[i] = destMins[i]
    }
    return Vec3(origin[0], origin[1], origin[2])
}

val EntityPlayer.ping get() = mc.netHandler.getPlayerInfo(uniqueID)?.responseTime ?: 0

val Entity.isAnimal
    get() = this is EntityAnimal
            || this is EntitySquid
            || this is EntityGolem
            || this is EntityBat

val Entity.isMob
    get() = this is EntityMob
            || this is EntityVillager
            || this is EntitySlime
            || this is EntityGhast
            || this is EntityDragon

val EntityPlayer.isClientFriend
    get() = if (name != null) friendsConfig.isFriend(stripColor(name)) else false

val Entity?.rotation
    get() = Rotation(this?.rotationYaw ?: 0f, this?.rotationPitch ?: 0f)

val Entity.hitBox: AxisAlignedBB
    get() = entityBoundingBox.expand(
        collisionBorderSize.toDouble(),
        collisionBorderSize.toDouble(),
        collisionBorderSize.toDouble()
    )

val Entity.eyes: Vec3
    get() = getPositionEyes(1f)

val Entity.prevPos: Vec3
    get() = Vec3(this.prevPosX, this.prevPosY, this.prevPosZ)

val Entity.currPos: Vec3
    get() = this.positionVector

fun Entity.setPosAndPrevPos(currPos: Vec3, prevPos: Vec3 = currPos) {
    setPosition(currPos.xCoord, currPos.yCoord, currPos.zCoord)
    prevPosX = prevPos.xCoord
    prevPosY = prevPos.yCoord
    prevPosZ = prevPos.zCoord
}

fun EntityPlayerSP.setFixedSensitivityAngles(yaw: Float? = null, pitch: Float? = null) {
    if (yaw != null) fixedSensitivityYaw = yaw
    if (pitch != null) fixedSensitivityPitch = pitch
}

var EntityPlayerSP.fixedSensitivityYaw
    get() = getFixedSensitivityAngle(mc.thePlayer.rotationYaw)
    set(yaw) {
        rotationYaw = getFixedSensitivityAngle(yaw, rotationYaw)
    }

var EntityPlayerSP.fixedSensitivityPitch
    get() = getFixedSensitivityAngle(rotationPitch)
    set(pitch) {
        rotationPitch = getFixedSensitivityAngle(pitch.coerceIn(-90f, 90f), rotationPitch)
    }

// Makes fixedSensitivityYaw, ... += work
operator fun EntityPlayerSP.plusAssign(value: Float) {
    fixedSensitivityYaw += value
    fixedSensitivityPitch += value
}

val Entity.interpolatedPosition
    get() = Vec3(
        prevPosX + (posX - prevPosX) * mc.timer.renderPartialTicks,
        prevPosY + (posY - prevPosY) * mc.timer.renderPartialTicks,
        prevPosZ + (posZ - prevPosZ) * mc.timer.renderPartialTicks
    )

fun EntityPlayerSP.stopXZ() {
    motionX = 0.0
    motionZ = 0.0
}

fun EntityPlayerSP.stop() {
    stopXZ()
    motionY = 0.0
}

// Modified mc.playerController.onPlayerRightClick() that sends correct stack in its C08
fun EntityPlayerSP.onPlayerRightClick(
    clickPos: BlockPos, side: EnumFacing, clickVec: Vec3,
    stack: ItemStack? = inventory.mainInventory[serverSlot],
): Boolean {
    if (clickPos !in worldObj.worldBorder)
        return false

    val (facingX, facingY, facingZ) = (clickVec - clickPos.toVec()).toFloatTriple()

    val sendClick = {
        sendPacket(
            C08PacketPlayerBlockPlacement(
                clickPos,
                side.index,
                stack,
                facingX,
                facingY,
                facingZ
            )
        )
        true
    }

    // If player is a spectator, send click and return true
    if (mc.playerController.isSpectator)
        return sendClick()

    val item = stack?.item

    if (item?.onItemUseFirst(stack, this, worldObj, clickPos, side, facingX, facingY, facingZ) == true)
        return true

    val blockState = BlockUtils.getState(clickPos)

    // If click had activated a block, send click and return true
    if ((!isSneaking || item == null || item.doesSneakBypassUse(worldObj, clickPos, this))
        && blockState?.block?.onBlockActivated(worldObj,
                                               clickPos,
                                               blockState,
                                               this,
                                               side,
                                               facingX,
                                               facingY,
                                               facingZ
        ) == true)
        return sendClick()

    if (item is ItemBlock && !item.canPlaceBlockOnSide(worldObj, clickPos, side, this, stack))
        return false

    sendClick()

    if (stack == null)
        return false

    val prevMetadata = stack.metadata
    val prevSize = stack.stackSize

    return stack.onItemUse(this, worldObj, clickPos, side, facingX, facingY, facingZ).also {
        if (mc.playerController.isInCreativeMode) {
            stack.itemDamage = prevMetadata
            stack.stackSize = prevSize
        } else if (stack.stackSize <= 0) {
            onPlayerDestroyItem(this, stack)
        }
    }
}

// Modified mc.playerController.sendUseItem() that sends correct stack in its C08
fun EntityPlayerSP.sendUseItem(stack: ItemStack): Boolean {
    if (mc.playerController.isSpectator)
        return false

    sendPacket(C08PacketPlayerBlockPlacement(stack))

    val prevSize = stack.stackSize

    val newStack = stack.useItemRightClick(worldObj, this)

    return if (newStack != stack || newStack.stackSize != prevSize) {
        if (newStack.stackSize <= 0) {
            mc.thePlayer.inventory.mainInventory[serverSlot] = null
            onPlayerDestroyItem(mc.thePlayer, newStack)
        } else
            mc.thePlayer.inventory.mainInventory[serverSlot] = newStack

        true
    } else false
}

fun EntityPlayer.fakeJump() {
    isAirBorne = true
    triggerAchievement(jumpStat)
}

fun EntityPlayer.jmp(
    motion: Number = JUMP_HEIGHT,
    boost: Boolean = true,
    ignoreJumpBoost: Boolean = false,
    ignoreGround: Boolean = false,
    whenJumping: Boolean = false,
) = jump(motion, boost, ignoreJumpBoost, ignoreGround, whenJumping)

fun EntityPlayer.jump(
    motion: Number = JUMP_HEIGHT,
    boost: Boolean = true,
    ignoreJumpBoost: Boolean = false,
    ignoreGround: Boolean = false,
    whenJumping: Boolean = false,
) {
    if (!ignoreGround && !onGround) return
    if (!whenJumping && mc.gameSettings.keyBindJump.pressed) return

    val x = motionX
    val z = motionZ

    jump()
    motionY = getJumpBoostModifier(motion.toDouble(), !ignoreJumpBoost)

    if (!boost) {
        motionX = x
        motionZ = z
    }
}

infix fun EntityPlayer.has(potion: Potions) = isPotionActive(potion.potion)
infix fun EntityPlayer.has(potion: Potion) = isPotionActive(potion)
fun EntityPlayer.get(potion: Potions): PotionEffect? = getActivePotionEffect(potion.potion)
fun EntityPlayer.get(potion: Potion): PotionEffect? = getActivePotionEffect(potion)

infix fun Entity.isInsideOf(material: Material) = isInsideOfMaterial(material)

val Potion.potion
    get() = Potions.entries.find { it.potion == this }

val PotionEffect?.level
    get() = this?.let { amplifier + 1 } ?: 0

fun EntityPlayer.swing(type: String) {
    when (type) {
        "Normal" -> swingItem()
        "Packet" -> sendPacket(C0APacketAnimation())
        "Visual" -> {
            if (swingProgressInt < 0 || !isSwingInProgress || swingProgressInt >= armSwingAnimationEnd * 0.5)
                swingProgressInt = -1
            isSwingInProgress = true
        }
    }
}

val Entity.inLiquid get() = isInWater || isInLava

fun String.toLowerCamelCase() = this.replaceFirst(this.toCharArray()[0], this.toCharArray()[0].lowercaseChar())

/**
 * Thing I'll probably only use for debugging
 * @return [String] form of [Double] in a human-readable format
 */
@Suppress("unused")
fun Double.toPlainString(): String {
    if (this == 0.0) return "0.0"
    val str = String.format("%.32f", this).trim('0')
    return when {
        str.endsWith('.') -> "${str}0"
        str.startsWith('.') -> "0$str"
        else -> str
    }
}

fun Timer.resetSpeed() { timerSpeed = 1f }
fun KeyBinding.update() { pressed = isActuallyPressed }
val KeyBinding.isActuallyPressed get() = isKeyDown(this)
fun GameSettings.updateKeys() = keyBindings.forEach { it.update() }
fun GameSettings.updateKeys(vararg keys: KeyBinding) = keys.forEach { it.update() }
val C08PacketPlayerBlockPlacement.isUse get() = placedBlockDirection == 255
