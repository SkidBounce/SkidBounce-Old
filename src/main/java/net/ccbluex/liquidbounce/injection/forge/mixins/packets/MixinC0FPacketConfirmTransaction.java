/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.injection.forge.mixins.packets;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.io.IOException;

@SideOnly(Side.CLIENT)
@Mixin(C0FPacketConfirmTransaction.class)
public class MixinC0FPacketConfirmTransaction {
    @Shadow private int windowId;

    @Shadow private short uid;

    @Shadow private boolean accepted;

    /**
     * @author ManInMyVan / SkidBounce
     * @reason ViaMCP
     */
    @Overwrite
    public void writePacketData(PacketBuffer buf) throws IOException {
        if (ViaLoadingBase.getInstance().getTargetVersion().isNewerThanOrEqualTo(ProtocolVersion.v1_17)) {
            buf.writeInt(this.windowId);
        } else {
            buf.writeByte(this.windowId);
            buf.writeShort(this.uid);
            buf.writeByte(this.accepted ? 1 : 0);
        }
    }
}
