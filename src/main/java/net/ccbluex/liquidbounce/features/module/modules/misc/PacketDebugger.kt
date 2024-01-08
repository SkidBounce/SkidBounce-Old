package net.ccbluex.liquidbounce.features.module.modules.misc

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.minecraft.network.handshake.client.C00Handshake
import net.minecraft.network.login.client.*
import net.minecraft.network.login.server.*
import net.minecraft.network.play.client.*
import net.minecraft.network.play.client.C03PacketPlayer.*
import net.minecraft.network.play.server.*
import net.minecraft.network.play.server.S14PacketEntity.S15PacketEntityRelMove
import net.minecraft.network.play.server.S14PacketEntity.S16PacketEntityLook
import net.minecraft.network.play.server.S14PacketEntity.S17PacketEntityLookMove
import net.minecraft.network.status.client.*
import net.minecraft.network.status.server.*
import net.minecraft.util.BlockPos
import net.minecraft.util.Vec3

object PacketDebugger : Module("PacketDebugger", ModuleCategory.MISC) {
    private val fields by BoolValue("ShowFields", true)
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
    private val CHS by BoolValue("C00Handshake", false)
    private val CL0 by BoolValue("C00PacketLoginStart", false)
    private val CL1 by BoolValue("C01PacketEncryptionResponse", false)
    private val SL0 by BoolValue("S00PacketDisconnect", false)
    private val SL1 by BoolValue("S01PacketEncryptionRequest", false)
    private val SL2 by BoolValue("S02PacketLoginSuccess", false)
    private val SL3 by BoolValue("S03PacketEnableCompression", false)
    private val CS0 by BoolValue("C00PacketServerQuery", false)
    private val CS1 by BoolValue("C01PacketPing", false)
    private val SS0 by BoolValue("S00PacketServerInfo", false)
    private val SS1 by BoolValue("S01PacketPong", false)
    private val C00 by BoolValue("C00PacketKeepAlive", false)
    private val C01 by BoolValue("C01PacketChatMessage", false)
    private val C02 by BoolValue("C02PacketUseEntity", false)
    private val C03 by BoolValue("C03PacketPlayer", false)
    private val C04 by BoolValue("C04PacketPlayerPosition", false)
    private val C05 by BoolValue("C05PacketPlayerLook", false)
    private val C06 by BoolValue("C06PacketPlayerPosLook", false)
    private val C07 by BoolValue("C07PacketPlayerDigging", false)
    private val C08 by BoolValue("C08PacketPlayerBlockPlacement", false)
    private val C09 by BoolValue("C09PacketHeldItemChange", false)
    private val C0A by BoolValue("C0APacketAnimation", false)
    private val C0B by BoolValue("C0BPacketEntityAction", false)
    private val C0C by BoolValue("C0CPacketInput", false)
    private val C0D by BoolValue("C0DPacketCloseWindow", false)
    private val C0E by BoolValue("C0EPacketClickWindow", false)
    private val C0F by BoolValue("C0FPacketConfirmTransaction", false)
    private val C10 by BoolValue("C10PacketCreativeInventoryAction", false)
    private val C11 by BoolValue("C11PacketEnchantItem", false)
    private val C12 by BoolValue("C12PacketUpdateSign", false)
    private val C13 by BoolValue("C13PacketPlayerAbilities", false)
    private val C14 by BoolValue("C14PacketTabComplete", false)
    private val C15 by BoolValue("C15PacketClientSettings", false)
    private val C16 by BoolValue("C16PacketClientStatus", false)
    private val C17 by BoolValue("C17PacketCustomPayload", false)
    private val C18 by BoolValue("C18PacketSpectate", false)
    private val C19 by BoolValue("C19PacketResourcePackStatus", false)
    private val S00 by BoolValue("S00PacketKeepAlive", false)
    private val S01 by BoolValue("S01PacketJoinGame", false)
    private val S02 by BoolValue("S02PacketChat", false)
    private val S03 by BoolValue("S03PacketTimeUpdate", false)
    private val S04 by BoolValue("S04PacketEntityEquipment", false)
    private val S05 by BoolValue("S05PacketSpawnPosition", false)
    private val S06 by BoolValue("S06PacketUpdateHealth", false)
    private val S07 by BoolValue("S07PacketRespawn", false)
    private val S08 by BoolValue("S08PacketPlayerPosLook",false)
    private val S09 by BoolValue("S09PacketHeldItemChange", false)
    private val S0A by BoolValue("S0APacketUseBed", false)
    private val S0B by BoolValue("S0BPacketAnimation", false)
    private val S0C by BoolValue("S0CPacketSpawnPlayer", false)
    private val S0D by BoolValue("S0DPacketCollectItem", false)
    private val S0E by BoolValue("S0EPacketSpawnObject", false)
    private val S0F by BoolValue("S0FPacketSpawnMob", false)
    private val S10 by BoolValue("S10PacketSpawnPainting", false)
    private val S11 by BoolValue("S11PacketSpawnExperienceOrb", false)
    private val S12 by BoolValue("S12PacketEntityVelocity", false)
    private val S13 by BoolValue("S13PacketDestroyEntities", false)
    private val S14 by BoolValue("S14PacketEntity", false)
    private val S15 by BoolValue("S15PacketEntityRelMove", false)
    private val S16 by BoolValue("S16PacketEntityLook", false)
    private val S17 by BoolValue("S17PacketEntityLookMove", false)
    private val S18 by BoolValue("S18PacketEntityTeleport", false)
    private val S19 by BoolValue("S19PacketEntityHeadLook", false)
    private val S1A by BoolValue("S19PacketEntityStatus", false)
    private val S1B by BoolValue("S1BPacketEntityAttach", false)
    private val S1C by BoolValue("S1CPacketEntityMetadata", false)
    private val S1D by BoolValue("S1DPacketEntityEffect", false)
    private val S1E by BoolValue("S1EPacketRemoveEntityEffect", false)
    private val S1F by BoolValue("S1FPacketSetExperience", false)
    private val S20 by BoolValue("S20PacketEntityProperties", false)
    private val S21 by BoolValue("S21PacketChunkData",false)
    private val S22 by BoolValue("S22PacketMultiBlockChange", false)
    private val S23 by BoolValue("S23PacketBlockChange", false)
    private val S24 by BoolValue("S24PacketBlockAction",false)
    private val S25 by BoolValue("S25PacketBlockBreakAnim", false)
    private val S26 by BoolValue("S26PacketMapChunkBulk", false)
    private val S27 by BoolValue("S27PacketExplosion",false)
    private val S28 by BoolValue("S28PacketEffect",false)
    private val S29 by BoolValue("S29PacketSoundEffect", false)
    private val S2A by BoolValue("S2APacketParticles", false)
    private val S2B by BoolValue("S2BPacketChangeGameState",false)
    private val S2C by BoolValue("S2CPacketSpawnGlobalEntity", false)
    private val S2D by BoolValue("S2DPacketOpenWindow", false)
    private val S2E by BoolValue("S2EPacketCloseWindow",false)
    private val S2F by BoolValue("S2FPacketSetSlot", false)
    private val S30 by BoolValue("S30PacketWindowItems", false)
    private val S31 by BoolValue("S31PacketWindowProperty", false)
    private val S32 by BoolValue("S32PacketConfirmTransaction", false)
    private val S33 by BoolValue("S33PacketUpdateSign", false)
    private val S34 by BoolValue("S34PacketMaps", false)
    private val S35 by BoolValue("S35PacketUpdateTileEntity", false)
    private val S36 by BoolValue("S36PacketSignEditorOpen", false)
    private val S37 by BoolValue("S37PacketStatistics", false)
    private val S38 by BoolValue("S38PacketPlayerListItem", false)
    private val S39 by BoolValue("S39PacketPlayerAbilities", false)
    private val S3A by BoolValue("S3APacketTabComplete", false)
    private val S3B by BoolValue("S3BPacketScoreboardObjective", false)
    private val S3C by BoolValue("S3CPacketUpdateScore", false)
    private val S3D by BoolValue("S3DPacketDisplayScoreboard", false)
    private val S3E by BoolValue("S3EPacketTeams", false)
    private val S3F by BoolValue("S3FPacketCustomPayload", false)
    private val S40 by BoolValue("S40PacketDisconnect", false)
    private val S41 by BoolValue("S41PacketServerDifficulty", false)
    private val S42 by BoolValue("S42PacketCombatEvent", false)
    private val S43 by BoolValue("S43PacketCamera", false)
    private val S44 by BoolValue("S44PacketWorldBorder", false)
    private val S45 by BoolValue("S45PacketTitle", false)
    private val S46 by BoolValue("S46PacketSetCompressionLevel", false)
    private val S47 by BoolValue("S47PacketPlayerListHeaderFooter", false)
    private val S48 by BoolValue("S48PacketResourcePackSend", false)
    private val S49 by BoolValue("S49PacketUpdateEntityNBT", false)

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (event.isCancelled) return
        val packet = event.packet
        val clazz: Class<*> = if (packet.javaClass.isMemberClass) packet.javaClass.declaringClass else packet.javaClass
        when (packet) {
            is C00Handshake -> if (!CHS) return
            is C00PacketLoginStart -> if (!CL0) return
            is C01PacketEncryptionResponse -> if (!CL1) return
            is S00PacketDisconnect -> if (!SL0) return
            is S01PacketEncryptionRequest -> if (!SL1) return
            is S02PacketLoginSuccess -> if (!SL2) return
            is S03PacketEnableCompression -> if (!SL3) return
            is C00PacketServerQuery -> if (!CS0) return
            is C01PacketPing -> if (!CS1) return
            is S00PacketServerInfo -> if (!SS0) return
            is S01PacketPong -> if (!SS1) return
            is C00PacketKeepAlive -> if (!C00) return
            is C01PacketChatMessage -> if (!C01) return
            is C02PacketUseEntity -> if (!C02) return
            is C04PacketPlayerPosition -> if (!C04) return
            is C05PacketPlayerLook -> if (!C05) return
            is C06PacketPlayerPosLook -> if (!C06) return
            is C03PacketPlayer -> if (!C03) return
            is C07PacketPlayerDigging -> if (!C07) return
            is C08PacketPlayerBlockPlacement -> if (!C08) return
            is C09PacketHeldItemChange -> if (!C09) return
            is C0APacketAnimation -> if (!C0A) return
            is C0BPacketEntityAction -> if (!C0B) return
            is C0CPacketInput -> if (!C0C) return
            is C0DPacketCloseWindow -> if (!C0D) return
            is C0EPacketClickWindow -> if (!C0E) return
            is C0FPacketConfirmTransaction -> if (!C0F) return
            is C10PacketCreativeInventoryAction -> if (!C10) return
            is C11PacketEnchantItem -> if (!C11) return
            is C12PacketUpdateSign -> if (!C12) return
            is C13PacketPlayerAbilities -> if (!C13) return
            is C14PacketTabComplete -> if (!C14) return
            is C15PacketClientSettings -> if (!C15) return
            is C16PacketClientStatus -> if (!C16) return
            is C17PacketCustomPayload -> if (!C17) return
            is C18PacketSpectate -> if (!C18) return
            is C19PacketResourcePackStatus -> if (!C19) return
            is S00PacketKeepAlive -> if (!S00) return
            is S01PacketJoinGame -> if (!S01) return
            is S02PacketChat -> if (!S02) return
            is S03PacketTimeUpdate -> if (!S03) return
            is S04PacketEntityEquipment -> if (!S04) return
            is S05PacketSpawnPosition -> if (!S05) return
            is S06PacketUpdateHealth -> if (!S06) return
            is S07PacketRespawn -> if (!S07) return
            is S08PacketPlayerPosLook -> if (!S08) return
            is S09PacketHeldItemChange -> if (!S09) return
            is S0APacketUseBed -> if (!S0A) return
            is S0BPacketAnimation -> if (!S0B) return
            is S0CPacketSpawnPlayer -> if (!S0C) return
            is S0DPacketCollectItem -> if (!S0D) return
            is S0EPacketSpawnObject -> if (!S0E) return
            is S0FPacketSpawnMob -> if (!S0F) return
            is S10PacketSpawnPainting -> if (!S10) return
            is S11PacketSpawnExperienceOrb -> if (!S11) return
            is S12PacketEntityVelocity -> if (!S12) return
            is S13PacketDestroyEntities -> if (!S13) return
            is S15PacketEntityRelMove -> if (!S15) return
            is S16PacketEntityLook -> if (!S16) return
            is S17PacketEntityLookMove -> if (!S17) return
            is S14PacketEntity -> if (!S14) return
            is S18PacketEntityTeleport -> if (!S18) return
            is S19PacketEntityHeadLook -> if (!S19) return
            is S19PacketEntityStatus -> if (!S1A) return
            is S1BPacketEntityAttach -> if (!S1B) return
            is S1CPacketEntityMetadata -> if (!S1C) return
            is S1DPacketEntityEffect -> if (!S1D) return
            is S1EPacketRemoveEntityEffect -> if (!S1E) return
            is S1FPacketSetExperience -> if (!S1F) return
            is S20PacketEntityProperties -> if (!S20) return
            is S21PacketChunkData -> if (!S21) return
            is S22PacketMultiBlockChange -> if (!S22) return
            is S23PacketBlockChange -> if (!S23) return
            is S24PacketBlockAction -> if (!S24) return
            is S25PacketBlockBreakAnim -> if (!S25) return
            is S26PacketMapChunkBulk -> if (!S26) return
            is S27PacketExplosion -> if (!S27) return
            is S28PacketEffect -> if (!S28) return
            is S29PacketSoundEffect -> if (!S29) return
            is S2APacketParticles -> if (!S2A) return
            is S2BPacketChangeGameState -> if (!S2B) return
            is S2CPacketSpawnGlobalEntity -> if (!S2C) return
            is S2DPacketOpenWindow -> if (!S2D) return
            is S2EPacketCloseWindow -> if (!S2E) return
            is S2FPacketSetSlot -> if (!S2F) return
            is S30PacketWindowItems -> if (!S30) return
            is S31PacketWindowProperty -> if (!S31) return
            is S32PacketConfirmTransaction -> if (!S32) return
            is S33PacketUpdateSign -> if (!S33) return
            is S34PacketMaps -> if (!S34) return
            is S35PacketUpdateTileEntity -> if (!S35) return
            is S36PacketSignEditorOpen -> if (!S36) return
            is S37PacketStatistics -> if (!S37) return
            is S38PacketPlayerListItem -> if (!S38) return
            is S39PacketPlayerAbilities -> if (!S39) return
            is S3APacketTabComplete -> if (!S3A) return
            is S3BPacketScoreboardObjective -> if (!S3B) return
            is S3CPacketUpdateScore -> if (!S3C) return
            is S3DPacketDisplayScoreboard -> if (!S3D) return
            is S3EPacketTeams -> if (!S3E) return
            is S3FPacketCustomPayload -> if (!S3F) return
            is S40PacketDisconnect -> if (!S40) return
            is S41PacketServerDifficulty -> if (!S41) return
            is S42PacketCombatEvent -> if (!S42) return
            is S43PacketCamera -> if (!S43) return
            is S44PacketWorldBorder -> if (!S44) return
            is S45PacketTitle -> if (!S45) return
            is S46PacketSetCompressionLevel -> if (!S46) return
            is S47PacketPlayerListHeaderFooter -> if (!S47) return
            is S48PacketResourcePackSend -> if (!S48) return
            is S49PacketUpdateEntityNBT -> if (!S49) return
            else -> return
        }
        ClientUtils.displayChatMessage("§6${packet.javaClass.simpleName}")
        if (!fields) return
        clazz.declaredFields.forEach {
            it.isAccessible = true
            when (it.name) {
                "field_179726_a" -> null
                "field_149590_d" -> if (packet is C12PacketUpdateSign) ClientUtils.displayChatMessage("  §9${if (fieldMap[it.name] != null) fieldMap[it.name] else it.name}§7: ${packet.lines.asList()}")
                else -> {
                    val field = it.get(packet)
                    val color = when (field) {
                        is BlockPos, is Vec3 -> ""
                        null -> "§4"
                        is String -> "§2"
                        is Number -> "§e"
                        true -> "§a"
                        false -> "§c"
                        else -> "§d"
                    }
                    val displayedField = when (field) {
                        is String -> "\"${field}\""
                        is Vec3 -> "§e${field.xCoord}§7, §e${field.yCoord}§7, §e${field.zCoord}"
                        is BlockPos -> "§e${field.x}§7, §e${field.y}§7, §e${field.z}"
                        else -> "$field"
                    }
                    ClientUtils.displayChatMessage("  §9${if (fieldMap[it.name] != null) fieldMap[it.name] else it.name}§7: $color$displayedField")
                }
            }
        }
    }
}
