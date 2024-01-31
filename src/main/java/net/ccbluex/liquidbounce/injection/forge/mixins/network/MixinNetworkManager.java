/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.injection.forge.mixins.network;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.connection.UserConnectionImpl;
import com.viaversion.viaversion.protocol.ProtocolPipelineImpl;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import de.florianmichael.vialoadingbase.netty.event.CompressionReorderEvent;
import de.florianmichael.viamcp.MCPVLBPipeline;
import de.florianmichael.viamcp.ViaMCP;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import net.ccbluex.liquidbounce.event.EventManager;
import net.ccbluex.liquidbounce.event.EventState;
import net.ccbluex.liquidbounce.event.PacketEvent;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.util.*;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.InetAddress;

import static net.minecraft.network.NetworkManager.CLIENT_EPOLL_EVENTLOOP;
import static net.minecraft.network.NetworkManager.CLIENT_NIO_EVENTLOOP;

@Mixin(NetworkManager.class)
public class MixinNetworkManager {

    @Shadow
    private Channel channel;

    @Inject(method = "channelRead0", at = @At("HEAD"), cancellable = true)
    private void read(ChannelHandlerContext context, Packet<?> packet, CallbackInfo callback) {
        final PacketEvent event = new PacketEvent(packet, EventState.RECEIVE);
        EventManager.INSTANCE.callEvent(event);

        if(event.isCancelled()) {
            callback.cancel();
        }
    }

    @Inject(method = "sendPacket(Lnet/minecraft/network/Packet;)V", at = @At("HEAD"), cancellable = true)
    private void send(Packet<?> packet, CallbackInfo callback) {
        final PacketEvent event = new PacketEvent(packet, EventState.SEND);
        EventManager.INSTANCE.callEvent(event);

        if(event.isCancelled()) {
            callback.cancel();
        }
    }

    @Inject(method = "setCompressionTreshold", at = @At("TAIL"))
    private void fixCompression(int p_setCompressionTreshold_1_, CallbackInfo ci) {
        this.channel.pipeline().fireUserEventTriggered(new CompressionReorderEvent());
    }
    
    // TODO: convert to @Inject
    /**
     * @author ManInMyVan / SkidBounce
     * @reason ViaMCP
     */
    @Overwrite
    @SideOnly(Side.CLIENT)
    public static NetworkManager createNetworkManagerAndConnect(InetAddress p_createNetworkManagerAndConnect_0_, int p_createNetworkManagerAndConnect_1_, boolean p_createNetworkManagerAndConnect_2_) {
        final NetworkManager networkmanager = new NetworkManager(EnumPacketDirection.CLIENTBOUND);
        Class oclass;
        LazyLoadBase lazyloadbase;
        if (Epoll.isAvailable() && p_createNetworkManagerAndConnect_2_) {
            oclass = EpollSocketChannel.class;
            lazyloadbase = CLIENT_EPOLL_EVENTLOOP;
        } else {
            oclass = NioSocketChannel.class;
            lazyloadbase = CLIENT_NIO_EVENTLOOP;
        }

        ((Bootstrap)((Bootstrap)((Bootstrap)(new Bootstrap()).group((EventLoopGroup)lazyloadbase.getValue())).handler(new ChannelInitializer<Channel>() {
            protected void initChannel(Channel p_initChannel_1_) throws Exception {
                try {
                    p_initChannel_1_.config().setOption(ChannelOption.TCP_NODELAY, true);
                } catch (ChannelException var3) {
                }
                p_initChannel_1_.pipeline().addLast((String)"timeout", (ChannelHandler)(new ReadTimeoutHandler(30))).addLast((String)"splitter", (ChannelHandler)(new MessageDeserializer2())).addLast((String)"decoder", (ChannelHandler)(new MessageDeserializer(EnumPacketDirection.CLIENTBOUND))).addLast((String)"prepender", (ChannelHandler)(new MessageSerializer2())).addLast((String)"encoder", (ChannelHandler)(new MessageSerializer(EnumPacketDirection.SERVERBOUND))).addLast((String)"packet_handler", (ChannelHandler)networkmanager);

                if (p_initChannel_1_ instanceof SocketChannel && ViaLoadingBase.getInstance().getTargetVersion().getVersion() != ViaMCP.NATIVE_VERSION) {
                    final UserConnection user = new UserConnectionImpl(p_initChannel_1_, true);
                    new ProtocolPipelineImpl(user);

                    p_initChannel_1_.pipeline().addLast(new MCPVLBPipeline(user));
                }
            }
        })).channel(oclass)).connect(p_createNetworkManagerAndConnect_0_, p_createNetworkManagerAndConnect_1_).syncUninterruptibly();
        return networkmanager;
    }
}
