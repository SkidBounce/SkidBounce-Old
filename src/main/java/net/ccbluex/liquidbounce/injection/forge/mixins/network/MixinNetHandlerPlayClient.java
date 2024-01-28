/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.injection.forge.mixins.network;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import io.netty.buffer.Unpooled;
import net.ccbluex.liquidbounce.event.EntityMovementEvent;
import net.ccbluex.liquidbounce.event.EventManager;
import net.ccbluex.liquidbounce.features.module.modules.misc.NoRotate;
import net.ccbluex.liquidbounce.features.module.modules.player.Blink;
import net.ccbluex.liquidbounce.features.special.ClientFixes;
import net.ccbluex.liquidbounce.utils.ClientUtils;
import net.ccbluex.liquidbounce.utils.PacketUtils;
import net.ccbluex.liquidbounce.utils.Rotation;
import net.ccbluex.liquidbounce.utils.RotationUtils;
import net.ccbluex.liquidbounce.utils.extensions.PlayerExtensionKt;
import net.ccbluex.liquidbounce.utils.misc.RandomUtils;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.PacketThreadUtil;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.*;
import net.minecraft.world.WorldSettings;
import org.apache.commons.lang3.tuple.MutableTriple;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.URI;
import java.net.URISyntaxException;

import static net.ccbluex.liquidbounce.utils.MinecraftInstance.mc;
import static net.minecraft.network.play.client.C19PacketResourcePackStatus.Action.ACCEPTED;
import static net.minecraft.network.play.client.C19PacketResourcePackStatus.Action.FAILED_DOWNLOAD;

@Mixin(NetHandlerPlayClient.class)
public abstract class MixinNetHandlerPlayClient {

    @Shadow
    public int currentServerMaxPlayers;
    @Shadow
    @Final
    private NetworkManager netManager;
    @Shadow
    private Minecraft gameController;
    @Shadow
    private WorldClient clientWorldController;

    @Shadow
    private boolean doneLoadingTerrain;

    @Shadow public abstract void addToSendQueue(Packet p_addToSendQueue_1_);

    @Inject(method = "handleResourcePack", at = @At("HEAD"), cancellable = true)
    private void handleResourcePack(final S48PacketResourcePackSend p_handleResourcePack_1_, final CallbackInfo callbackInfo) {
        final String url = p_handleResourcePack_1_.getURL();
        final String hash = p_handleResourcePack_1_.getHash();

        if (ClientFixes.INSTANCE.getBlockResourcePackExploit()) {
            try {
                final String scheme = new URI(url).getScheme();
                final boolean isLevelProtocol = "level".equals(scheme);

                if (!"http".equals(scheme) && !"https".equals(scheme) && !isLevelProtocol)
                    throw new URISyntaxException(url, "Wrong protocol");

                if (isLevelProtocol && (url.contains("..") || !url.endsWith("/resources.zip")))
                    throw new URISyntaxException(url, "Invalid levelstorage resourcepack path");
            } catch (final URISyntaxException e) {
                ClientUtils.INSTANCE.getLOGGER().error("Failed to handle resource pack", e);

                // Accepted is always sent.
                netManager.sendPacket(new C19PacketResourcePackStatus(hash, ACCEPTED));
                // But we fail of course.
                netManager.sendPacket(new C19PacketResourcePackStatus(hash, FAILED_DOWNLOAD));

                callbackInfo.cancel();
            }
        }
    }

    @Inject(method = "handleJoinGame", at = @At("HEAD"), cancellable = true)
    private void handleJoinGameWithAntiForge(S01PacketJoinGame packetIn, final CallbackInfo callbackInfo) {
        if (!ClientFixes.INSTANCE.getFmlFixesEnabled() || !ClientFixes.INSTANCE.getBlockFML() || mc.isIntegratedServerRunning())
            return;

        PacketThreadUtil.checkThreadAndEnqueue(packetIn, (NetHandlerPlayClient) (Object) this, gameController);
        gameController.playerController = new PlayerControllerMP(gameController, (NetHandlerPlayClient) (Object) this);
        clientWorldController = new WorldClient((NetHandlerPlayClient) (Object) this, new WorldSettings(0L, packetIn.getGameType(), false, packetIn.isHardcoreMode(), packetIn.getWorldType()), packetIn.getDimension(), packetIn.getDifficulty(), gameController.mcProfiler);
        gameController.gameSettings.difficulty = packetIn.getDifficulty();
        gameController.loadWorld(clientWorldController);
        gameController.thePlayer.dimension = packetIn.getDimension();
        gameController.displayGuiScreen(new GuiDownloadTerrain((NetHandlerPlayClient) (Object) this));
        gameController.thePlayer.setEntityId(packetIn.getEntityId());
        currentServerMaxPlayers = packetIn.getMaxPlayers();
        gameController.thePlayer.setReducedDebug(packetIn.isReducedDebugInfo());
        gameController.playerController.setGameType(packetIn.getGameType());
        gameController.gameSettings.sendSettingsToServer();
        netManager.sendPacket(new C17PacketCustomPayload("MC|Brand", (new PacketBuffer(Unpooled.buffer())).writeString(ClientBrandRetriever.getClientModName())));
        callbackInfo.cancel();
    }

    @Inject(method = "handleEntityMovement", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;onGround:Z"))
    private void handleEntityMovementEvent(S14PacketEntity packetIn, final CallbackInfo callbackInfo) {
        final Entity entity = packetIn.getEntity(clientWorldController);

        if (entity != null)
            EventManager.INSTANCE.callEvent(new EntityMovementEvent(entity));
    }

    @Inject(method = "handlePlayerPosLook", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayer;setPositionAndRotation(DDDFF)V", shift = At.Shift.BEFORE))
    private void injectNoRotateSetPositionOnly(S08PacketPlayerPosLook p_handlePlayerPosLook_1_, CallbackInfo ci) {
        NoRotate module = NoRotate.INSTANCE;

        // Save the server's requested rotation before it resets the rotations
        module.setSavedRotation(PlayerExtensionKt.getRotation(Minecraft.getMinecraft().thePlayer));
    }

    @Redirect(method = "handlePlayerPosLook", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/NetworkManager;sendPacket(Lnet/minecraft/network/Packet;)V"))
    private void injectNoRotateSetAndAntiServerRotationOverride(NetworkManager instance, Packet p_sendPacket_1_) {
        Blink module2 = Blink.INSTANCE;
        boolean shouldTrigger = module2.blinkingSend();
        PacketUtils.sendPacket(p_sendPacket_1_, shouldTrigger);

        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        NoRotate module = NoRotate.INSTANCE;

        if (player == null || !module.shouldModify(player)) {
            return;
        }

        int sign = RandomUtils.INSTANCE.nextBoolean() ? 1 : -1;

        Rotation rotation = player.ticksExisted == 0 ? RotationUtils.INSTANCE.getServerRotation() : module.getSavedRotation();

        Rotation currentRotation = RotationUtils.INSTANCE.getCurrentRotation();

        if (currentRotation != null && module.getAffectServerRotation()) {
            RotationUtils.INSTANCE.setSetbackRotation(new MutableTriple<>(PlayerExtensionKt.getRotation(player), true, currentRotation));
        }

        // Slightly modify the client-side rotations, so they pass the rotation difference check in onUpdateWalkingPlayer, EntityPlayerSP.
        player.rotationYaw = (rotation.getYaw() + 0.000001f * sign) % 360.0F;
        player.rotationPitch = (rotation.getPitch() + 0.000001f * sign) % 360.0F;
        RotationUtils.INSTANCE.syncRotations();
    }

    /**
     * @author ManInMyVan / SkidBounce
     * @reason ViaMCP
     */
    @Overwrite
    public void handleConfirmTransaction(S32PacketConfirmTransaction p_handleConfirmTransaction_1_) {
        PacketThreadUtil.checkThreadAndEnqueue(p_handleConfirmTransaction_1_, mc.getNetHandler(), this.gameController);
        if (ViaLoadingBase.getInstance().getTargetVersion().isNewerThanOrEqualTo(ProtocolVersion.v1_17)) {
            this.addToSendQueue(new C0FPacketConfirmTransaction(p_handleConfirmTransaction_1_.getWindowId(), (short) 0, false));
            return;
        }
        Container container = null;
        EntityPlayer entityplayer = this.gameController.thePlayer;
        if (p_handleConfirmTransaction_1_.getWindowId() == 0) {
            container = entityplayer.inventoryContainer;
        } else if (p_handleConfirmTransaction_1_.getWindowId() == entityplayer.openContainer.windowId) {
            container = entityplayer.openContainer;
        }

        if (container != null && !p_handleConfirmTransaction_1_.func_148888_e()) {
            this.addToSendQueue(new C0FPacketConfirmTransaction(p_handleConfirmTransaction_1_.getWindowId(), p_handleConfirmTransaction_1_.getActionNumber(), true));
        }
    }
}
