/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.client

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.events.PacketEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory.CLIENT
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.utils.PacketType
import net.ccbluex.liquidbounce.utils.PacketType.SERVER
import net.ccbluex.liquidbounce.utils.PacketUtils.PacketBuffer
import net.ccbluex.liquidbounce.utils.PacketUtils.type
import net.ccbluex.liquidbounce.utils.extensions.actual
import net.ccbluex.liquidbounce.utils.extensions.hasPosition
import net.ccbluex.liquidbounce.utils.extensions.hasRotation
import net.ccbluex.liquidbounce.value.BooleanValue
import net.ccbluex.liquidbounce.value.Value
import net.minecraft.network.handshake.client.C00Handshake
import net.minecraft.network.login.client.C00PacketLoginStart
import net.minecraft.network.login.client.C01PacketEncryptionResponse
import net.minecraft.network.login.server.S00PacketDisconnect
import net.minecraft.network.login.server.S01PacketEncryptionRequest
import net.minecraft.network.login.server.S02PacketLoginSuccess
import net.minecraft.network.login.server.S03PacketEnableCompression
import net.minecraft.network.play.client.*
import net.minecraft.network.play.client.C02PacketUseEntity.Action.INTERACT_AT
import net.minecraft.network.play.client.C03PacketPlayer.*
import net.minecraft.network.play.server.*
import net.minecraft.network.play.server.S14PacketEntity.*
import net.minecraft.network.status.client.C00PacketServerQuery
import net.minecraft.network.status.client.C01PacketPing
import net.minecraft.network.status.server.S00PacketServerInfo
import net.minecraft.network.status.server.S01PacketPong
import net.minecraft.util.Vec3
import net.minecraft.util.Vec3i

object PacketDebugger : Module("PacketDebugger", CLIENT, gameDetecting = false) {
    private val fieldsValue = BooleanValue("ShowFields", true)
    private val fields by fieldsValue
    private val rawFieldNamesValue = BooleanValue("RawFieldNames", false) { fields }
    private val rawFieldNames by rawFieldNamesValue
    private val fieldMap = hashMapOf(
        "field_149600_a" to "Version",
        "field_149598_b" to "Ip",
        "field_149599_c" to "Port",
        "field_149597_d" to "RequestedState",
        "field_149305_a" to "Profile",
        "field_149302_a" to "SecretKey",
        "field_149301_b" to "VerifyToken",
        "field_149605_a" to "Reason",
        "field_149612_a" to "HashedServerId",
        "field_149610_b" to "PublicKey",
        "field_149611_c" to "VerifyToken",
        "field_149602_a" to "Profile",
        "field_179733_a" to "Threshold",
        "field_149290_a" to "Time",
        "field_149297_a" to "GSON",
        "field_149296_b" to "Response",
        "field_149293_a" to "Time",
        "field_149461_a" to "Id",
        "field_149440_a" to "Message",
        "field_149567_a" to "Entity",
        "field_149566_b" to "Action",
        "field_179713_c" to "HitVec",
        "field_149479_a" to "X",
        "field_149477_b" to "Y",
        "field_149478_c" to "Z",
        "field_149476_e" to "Yaw",
        "field_149473_f" to "Pitch",
        "field_149474_g" to "Ground",
        "field_149480_h" to "Moving",
        "field_149481_i" to "Rotating",
        "field_179717_a" to "Position",
        "field_179716_b" to "Facing",
        "field_149508_e" to "Status",
        "field_179725_b" to "Position",
        "field_149579_d" to "Direction",
        "field_149580_e" to "Item",
        "field_149577_f" to "OffsetX",
        "field_149578_g" to "OffsetY",
        "field_149584_h" to "OffsetZ",
        "field_149615_a" to "Slot",
        "field_149517_a" to "Entity",
        "field_149515_b" to "Action",
        "field_149516_c" to "AuxData",
        "field_149624_a" to "StrafeSpeed",
        "field_149622_b" to "ForwardSpeed",
        "field_149623_c" to "Jumping",
        "field_149621_d" to "Sneaking",
        "field_149556_a" to "Window",
        "field_149554_a" to "Window",
        "field_149552_b" to "Slot",
        "field_149553_c" to "Button",
        "field_149550_d" to "Id",
        "field_149551_e" to "Item",
        "field_149549_f" to "Mode",
        "field_149536_a" to "Window",
        "field_149534_b" to "Id",
        "field_149535_c" to "Accepted",
        "field_149629_a" to "Slot",
        "field_149628_b" to "Item",
        "field_149541_a" to "Window",
        "field_149540_b" to "Button",
        "field_179723_a" to "Position",
        "field_149590_d" to "Lines",
        "field_149500_a" to "Invulnerable",
        "field_149498_b" to "Flying",
        "field_149499_c" to "AllowFlying",
        "field_149496_d" to "CreativeMode",
        "field_149497_e" to "FlySpeed",
        "field_149495_f" to "WalkSpeed",
        "field_149420_a" to "Message",
        "field_179710_b" to "TargetedBlock",
        "field_149530_a" to "Language",
        "field_149528_b" to "RenderDistance",
        "field_149529_c" to "ChatVisibility",
        "field_149526_d" to "ChatColors",
        "field_179711_e" to "SkinLayers",
        "field_149437_a" to "Status",
        "field_149562_a" to "Channel",
        "field_149561_c" to "Data",
        "field_179729_a" to "UUID",
        "field_179720_a" to "Hash",
        "field_179719_b" to "Status",
        "field_149136_a" to "Id",
        "field_149206_a" to "Entity",
        "field_149204_b" to "Hardcore",
        "field_149205_c" to "Gamemode",
        "field_149202_d" to "Dimension",
        "field_149203_e" to "Difficulty",
        "field_149200_f" to "MaxPlayers",
        "field_149201_g" to "WorldType",
        "field_179745_h" to "ReducedDebugInfo",
        "field_148919_a" to "Message",
        "field_179842_b" to "Type",
        "field_149369_a" to "TotalWorldTime",
        "field_149368_b" to "WorldTime",
        "field_149394_a" to "Entity",
        "field_149392_b" to "Slot",
        "field_149393_c" to "Item",
        "field_179801_a" to "Position",
        "field_149336_a" to "Health",
        "field_149334_b" to "Hunger",
        "field_149335_c" to "Saturation",
        "field_149088_a" to "Dimension",
        "field_149086_b" to "Difficulty",
        "field_149087_c" to "Gamemode",
        "field_149085_d" to "WorldType",
        "field_148940_a" to "X",
        "field_148938_b" to "Y",
        "field_148939_c" to "Z",
        "field_148936_d" to "Yaw",
        "field_148937_e" to "Pitch",
        "field_179835_f" to "Flags",
        "field_149387_a" to "Slot",
        "field_149097_a" to "Player",
        "field_179799_b" to "BedPosition",
        "field_148981_a" to "Entity",
        "field_148980_b" to "Type",
        "field_148957_a" to "Entity",
        "field_179820_b" to "Player",
        "field_148956_c" to "X",
        "field_148953_d" to "Y",
        "field_148954_e" to "Z",
        "field_148951_f" to "Yaw",
        "field_148952_g" to "Pitch",
        "field_148959_h" to "Slot",
        "field_148960_i" to "Watcher",
        "field_148958_j" to "Data",
        "field_149357_a" to "CollectedItem",
        "field_149356_b" to "Entity",
        "field_149018_a" to "Entity",
        "field_149016_b" to "X",
        "field_149017_c" to "Y",
        "field_149014_d" to "Z",
        "field_149015_e" to "SpeedX",
        "field_149012_f" to "SpeedY",
        "field_149013_g" to "SpeedZ",
        "field_149021_h" to "Pitch",
        "field_149022_i" to "Yaw",
        "field_149019_j" to "Type",
        "field_149020_k" to "Data",
        "field_149042_a" to "Entity",
        "field_149040_b" to "Type",
        "field_149041_c" to "X",
        "field_149038_d" to "Y",
        "field_149039_e" to "Z",
        "field_149036_f" to "VelocityX",
        "field_149037_g" to "VelocityY",
        "field_149047_h" to "VelocityZ",
        "field_149048_i" to "Yaw",
        "field_149045_j" to "Pitch",
        "field_149046_k" to "HeadPitch",
        "field_149043_l" to "DataManager",
        "field_149044_m" to "Data",
        "field_148973_a" to "Entity",
        "field_179838_b" to "Position",
        "field_179839_c" to "Facing",
        "field_148968_f" to "Name",
        "field_148992_a" to "Entity",
        "field_148990_b" to "X",
        "field_148991_c" to "Y",
        "field_148988_d" to "Z",
        "field_148989_e" to "Xp",
        "field_149417_a" to "Entity",
        "field_149415_b" to "MotionX",
        "field_149416_c" to "MotionY",
        "field_149414_d" to "MotionZ",
        "field_149100_a" to "Entities",
        "field_149072_b" to "X",
        "field_149073_c" to "Y",
        "field_149070_d" to "Z",
        "field_149071_e" to "Yaw",
        "field_149068_f" to "Pitch",
        "field_179743_g" to "Ground",
        "field_149069_g" to "Rotating",
        "field_149458_a" to "Entity",
        "field_149456_b" to "X",
        "field_149457_c" to "Y",
        "field_149454_d" to "Z",
        "field_149455_e" to "Yaw",
        "field_149453_f" to "Pitch",
        "field_179698_g" to "Ground",
        "field_149384_a" to "Entity",
        "field_149383_b" to "Yaw",
        "field_149164_a" to "Entity",
        "field_149163_b" to "LogicOpcode",
        "field_149408_a" to "Leash",
        "field_149406_b" to "Entity",
        "field_149407_c" to "Vehicle",
        "field_149379_a" to "Entity",
        "field_149378_b" to "Data",
        "field_149434_a" to "Entity",
        "field_149432_b" to "Effect",
        "field_149433_c" to "Amplifier",
        "field_149431_d" to "Duration",
        "field_179708_e" to "HideParticles",
        "field_149079_a" to "Entity",
        "field_149078_b" to "Effect",
        "field_149401_a" to "Bar",
        "field_149399_b" to "Experience",
        "field_149400_c" to "Level",
        "field_149445_a" to "Entity",
        "field_149444_b" to "Snapshots",
        "field_149284_a" to "X",
        "field_149282_b" to "Z",
        "field_179758_c" to "Data",
        "field_149279_g" to "FullChunk",
        "field_148925_b" to "ChunkPos",
        "field_179845_b" to "ChangedBlocks",
        "field_179828_a" to "Position",
        "field_148883_d" to "State",
        "field_179826_a" to "Position",
        "field_148872_d" to "Instrument",
        "field_148873_e" to "Pitch",
        "field_148871_f" to "Block",
        "field_148852_a" to "Breaker",
        "field_179822_b" to "Position",
        "field_148849_e" to "Progress",
        "field_149266_a" to "XPositions",
        "field_149264_b" to "ZPositions",
        "field_179755_c" to "Data",
        "field_149267_h" to "Overworld",
        "field_149158_a" to "X",
        "field_149156_b" to "Y",
        "field_149157_c" to "Z",
        "field_149154_d" to "Strength",
        "field_149155_e" to "AffectedBlockPositions",
        "field_149152_f" to "MotionX",
        "field_149153_g" to "MotionY",
        "field_149159_h" to "MotionZ",
        "field_149251_a" to "Type",
        "field_179747_b" to "Position",
        "field_149249_b" to "Data",
        "field_149246_f" to "ServerWide",
        "field_149219_a" to "Name",
        "field_149217_b" to "X",
        "field_149218_c" to "Y",
        "field_149215_d" to "Z",
        "field_149216_e" to "Volume",
        "field_149214_f" to "Pitch",
        "field_179751_a" to "Type",
        "field_149234_b" to "X",
        "field_149235_c" to "Y",
        "field_149232_d" to "Z",
        "field_149233_e" to "XOffset",
        "field_149230_f" to "YOffset",
        "field_149231_g" to "ZOffset",
        "field_149237_h" to "Speed",
        "field_149238_i" to "Count",
        "field_179752_j" to "LongDistance",
        "field_179753_k" to "Arguments",
        "field_149142_a" to "MESSAGE_NAMES",
        "field_149140_b" to "State",
        "field_149141_c" to "Value",
        "field_149059_a" to "Entity",
        "field_149057_b" to "X",
        "field_149058_c" to "Y",
        "field_149055_d" to "Z",
        "field_149056_e" to "Type",
        "field_148909_a" to "Window",
        "field_148907_b" to "Type",
        "field_148908_c" to "Title",
        "field_148905_d" to "Slots",
        "field_148904_f" to "Entity",
        "field_148896_a" to "Window",
        "field_149179_a" to "Window",
        "field_149177_b" to "Slot",
        "field_149178_c" to "Item",
        "field_148914_a" to "Window",
        "field_148913_b" to "Stacks",
        "field_149186_a" to "Window",
        "field_149184_b" to "Index",
        "field_149185_c" to "Value",
        "field_148894_a" to "Window",
        "field_148892_b" to "ActionNumber",
        "field_148893_c" to "Accepted",
        "field_179706_a" to "World",
        "field_179705_b" to "Position",
        "field_149349_d" to "Lines",
        "field_149191_a" to "Id",
        "field_179739_b" to "Scale",
        "field_179740_c" to "VisiblePlayers",
        "field_179737_d" to "MinX",
        "field_179738_e" to "MinY",
        "field_179735_f" to "MaxX",
        "field_179736_g" to "MaxY",
        "field_179741_h" to "Data",
        "field_179824_a" to "Position",
        "field_148859_d" to "Metadata",
        "field_148860_e" to "NBT",
        "field_179778_a" to "Position",
        "field_148976_a" to "Statistics",
        "field_179770_a" to "Action",
        "field_179769_b" to "Players",
        "field_149119_a" to "Invulnerable",
        "field_149117_b" to "Flying",
        "field_149118_c" to "AllowFlying",
        "field_149115_d" to "CreativeMode",
        "field_149116_e" to "FlySpeed",
        "field_149114_f" to "WalkSpeed",
        "field_149632_a" to "Matches",
        "field_149343_a" to "Name",
        "field_149341_b" to "Value",
        "field_179818_c" to "Type",
        "field_149342_c" to "Action",
        "field_149329_a" to "Name",
        "field_149327_b" to "Objective",
        "field_149328_c" to "Value",
        "field_149326_d" to "Action",
        "field_149374_a" to "Position",
        "field_149373_b" to "Name",
        "field_149320_a" to "Name",
        "field_149318_b" to "DisplayName",
        "field_149319_c" to "Prefix",
        "field_149316_d" to "Suffix",
        "field_179816_e" to "NameTagVisibility",
        "field_179815_f" to "Color",
        "field_149317_e" to "Players",
        "field_149314_f" to "Action",
        "field_149315_g" to "FriendlyFlags",
        "field_149172_a" to "Channel",
        "field_149171_b" to "Data",
        "field_149167_a" to "Reason",
        "field_179833_a" to "Difficulty",
        "field_179832_b" to "DifficultyLocked",
        "field_179776_a" to "EventType",
        "field_179774_b" to "Player",
        "field_179775_c" to "Entity",
        "field_179772_d" to "Duration",
        "field_179773_e" to "DeathMessage",
        "field_179795_a" to "Action",
        "field_179793_b" to "Size",
        "field_179794_c" to "CenterX",
        "field_179791_d" to "CenterZ",
        "field_179792_e" to "TargetSize",
        "field_179789_f" to "Diameter",
        "field_179790_g" to "TimeUntilTarget",
        "field_179796_h" to "WarningTime",
        "field_179797_i" to "WarningDistance",
        "field_179812_a" to "Type",
        "field_179810_b" to "Message",
        "field_179811_c" to "FadeInTime",
        "field_179808_d" to "DisplayTime",
        "field_179809_e" to "FadeOutTime",
        "field_179761_a" to "Threshold",
        "field_179703_a" to "Header",
        "field_179702_b" to "Footer",
        "field_179786_a" to "URL",
        "field_179785_b" to "Hash",
        "field_179766_a" to "Entity",
        "field_179765_b" to "NBT",
        "field_149074_a" to "Entity",
        "field_179781_a" to "Entity",
    )

    private var settings = arrayListOf<Value<*>>(fieldsValue, rawFieldNamesValue)

    init {
        arrayOf(
            C00Handshake::class.java,
            C00PacketLoginStart::class.java,
            C01PacketEncryptionResponse::class.java,
            S00PacketDisconnect::class.java,
            S01PacketEncryptionRequest::class.java,
            S02PacketLoginSuccess::class.java,
            S03PacketEnableCompression::class.java,
            C00PacketServerQuery::class.java,
            C01PacketPing::class.java,
            S00PacketServerInfo::class.java,
            S01PacketPong::class.java,
            C00PacketKeepAlive::class.java,
            C01PacketChatMessage::class.java,
            C02PacketUseEntity::class.java,
            C03PacketPlayer::class.java,
            C04PacketPlayerPosition::class.java,
            C05PacketPlayerLook::class.java,
            C06PacketPlayerPosLook::class.java,
            C07PacketPlayerDigging::class.java,
            C08PacketPlayerBlockPlacement::class.java,
            C09PacketHeldItemChange::class.java,
            C0APacketAnimation::class.java,
            C0BPacketEntityAction::class.java,
            C0CPacketInput::class.java,
            C0DPacketCloseWindow::class.java,
            C0EPacketClickWindow::class.java,
            C0FPacketConfirmTransaction::class.java,
            C10PacketCreativeInventoryAction::class.java,
            C11PacketEnchantItem::class.java,
            C12PacketUpdateSign::class.java,
            C13PacketPlayerAbilities::class.java,
            C14PacketTabComplete::class.java,
            C15PacketClientSettings::class.java,
            C16PacketClientStatus::class.java,
            C17PacketCustomPayload::class.java,
            C18PacketSpectate::class.java,
            C19PacketResourcePackStatus::class.java,
            S00PacketKeepAlive::class.java,
            S01PacketJoinGame::class.java,
            S02PacketChat::class.java,
            S03PacketTimeUpdate::class.java,
            S04PacketEntityEquipment::class.java,
            S05PacketSpawnPosition::class.java,
            S06PacketUpdateHealth::class.java,
            S07PacketRespawn::class.java,
            S08PacketPlayerPosLook::class.java,
            S09PacketHeldItemChange::class.java,
            S0APacketUseBed::class.java,
            S0BPacketAnimation::class.java,
            S0CPacketSpawnPlayer::class.java,
            S0DPacketCollectItem::class.java,
            S0EPacketSpawnObject::class.java,
            S0FPacketSpawnMob::class.java,
            S10PacketSpawnPainting::class.java,
            S11PacketSpawnExperienceOrb::class.java,
            S12PacketEntityVelocity::class.java,
            S13PacketDestroyEntities::class.java,
            S14PacketEntity::class.java,
            S15PacketEntityRelMove::class.java,
            S16PacketEntityLook::class.java,
            S17PacketEntityLookMove::class.java,
            S18PacketEntityTeleport::class.java,
            S19PacketEntityHeadLook::class.java,
            S19PacketEntityStatus::class.java,
            S1BPacketEntityAttach::class.java,
            S1CPacketEntityMetadata::class.java,
            S1DPacketEntityEffect::class.java,
            S1EPacketRemoveEntityEffect::class.java,
            S1FPacketSetExperience::class.java,
            S20PacketEntityProperties::class.java,
            S21PacketChunkData::class.java,
            S22PacketMultiBlockChange::class.java,
            S23PacketBlockChange::class.java,
            S24PacketBlockAction::class.java,
            S25PacketBlockBreakAnim::class.java,
            S26PacketMapChunkBulk::class.java,
            S27PacketExplosion::class.java,
            S28PacketEffect::class.java,
            S29PacketSoundEffect::class.java,
            S2APacketParticles::class.java,
            S2BPacketChangeGameState::class.java,
            S2CPacketSpawnGlobalEntity::class.java,
            S2DPacketOpenWindow::class.java,
            S2EPacketCloseWindow::class.java,
            S2FPacketSetSlot::class.java,
            S30PacketWindowItems::class.java,
            S31PacketWindowProperty::class.java,
            S32PacketConfirmTransaction::class.java,
            S33PacketUpdateSign::class.java,
            S34PacketMaps::class.java,
            S35PacketUpdateTileEntity::class.java,
            S36PacketSignEditorOpen::class.java,
            S37PacketStatistics::class.java,
            S38PacketPlayerListItem::class.java,
            S39PacketPlayerAbilities::class.java,
            S3APacketTabComplete::class.java,
            S3BPacketScoreboardObjective::class.java,
            S3CPacketUpdateScore::class.java,
            S3DPacketDisplayScoreboard::class.java,
            S3EPacketTeams::class.java,
            S3FPacketCustomPayload::class.java,
            S40PacketDisconnect::class.java,
            S41PacketServerDifficulty::class.java,
            S42PacketCombatEvent::class.java,
            S43PacketCamera::class.java,
            S44PacketWorldBorder::class.java,
            S45PacketTitle::class.java,
            S46PacketSetCompressionLevel::class.java,
            S47PacketPlayerListHeaderFooter::class.java,
            S48PacketResourcePackSend::class.java,
            S49PacketUpdateEntityNBT::class.java
        ).forEach {
            settings += BooleanValue(it.simpleName, false)
        }
    }

    @EventTarget(priority = Int.MIN_VALUE)
    fun onPacket(event: PacketEvent) {
        if (!state || event.isCancelled && event.packet.type != SERVER) return
        val packet = event.packet

        val enabled = settings.filterIsInstance<BooleanValue>().find { it.name == packet.javaClass.simpleName } ?: return
        if (!enabled.get())
            return

        val javaClass = packet.javaClass
        var lines = arrayOf("§6${javaClass.simpleName}")

        if (fields) {
            @Suppress("NAME_SHADOWING")
            val packet = packet.actual

            val packetFields = if (javaClass.isMemberClass)
                javaClass.declaringClass.declaredFields else javaClass.declaredFields

            for (field in packetFields) {
                // Filter fields that aren't written to the packet
                if (field.name in arrayOf(
                    "field_179726_a",
                    "field_149480_h", // Moving (C03)
                    "field_149481_i", // Rotating (C03)
                    "field_149069_g" // Rotating (S14)
                )) continue

                when (field.name) {
                    // HitVec
                    "field_179713_c" -> if (packet is C02PacketUseEntity && packet.action != INTERACT_AT) continue
                    // X, Y, Z
                    "field_149479_a", "field_149477_b", "field_149478_c" ->  if (packet is C03PacketPlayer && !packet.hasPosition) continue
                    // Yaw, Pitch
                    "field_149476_e", "field_149473_f" -> if (packet is C03PacketPlayer && !packet.hasRotation) continue
                    // TargetedBlock
                    "field_179710_b" -> if (packet is C14PacketTabComplete && packet.targetBlock == null) continue
                    // Yaw, Pitch
                    "field_149071_e", "field_149068_f" -> if (packet is S14PacketEntity && !packet.hasRotation) continue
                    // X, Y, Z, Ground
                    "field_149072_b", "field_149073_c", "field_149070_d", "field_179743_g" -> if (packet is S14PacketEntity && !packet.hasPosition) continue
                }

                field.isAccessible = true

                val name = fieldMap[field.name].let {
                    if (it == null || rawFieldNames) field.name else it
                }

                val value: Any? = when {
                    field.name == "field_149590_d" && packet is C12PacketUpdateSign -> packet.lines.asList()
                    else -> field.get(packet)
                }

                lines += "§9$name§7: ${format(value)}"
            }
        }

        ClientUtils.displayChatMessage(lines.joinToString("\n  "))
    }

    fun format(value: Any?, arrayBrackets: Boolean = true): String {
        when (value) {
            is Vec3 -> return format(listOf(value.xCoord, value.yCoord, value.zCoord), false)
            is Vec3i -> return format(listOf(value.x, value.y, value.z), false)
            is Array<*> -> return format(value.toList(), arrayBrackets)
        }

        val color = when (value) {
            is Collection<*> -> ""
            null -> "§4"
            is String -> "§2"
            is Number -> "§e"
            true -> "§a"
            false -> "§c"
            else -> "§d"
        }

        val display = when (value) {
            is String -> "\"$value\""
            is Collection<*> -> (if (arrayBrackets) "§7[" else "") + (value.joinToString("§7, ") { format(it) }) + (if (arrayBrackets) "§7]" else "")
            else -> "$value"
        }

        return "$color$display"
    }

    override val values: List<Value<*>>
        get() = settings
}
