/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.injection.forge.mixins.entity;

import net.ccbluex.liquidbounce.cape.CapeAPI;
import net.ccbluex.liquidbounce.cape.CapeInfo;
import net.ccbluex.liquidbounce.features.module.modules.misc.NameProtect;
import net.ccbluex.liquidbounce.features.module.modules.render.NoFOV;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

import static net.ccbluex.liquidbounce.utils.MinecraftInstance.mc;

@Mixin(AbstractClientPlayer.class)
@SideOnly(Side.CLIENT)
public abstract class MixinAbstractClientPlayer extends MixinEntityPlayer {
    @Unique private CapeInfo skidBounce$capeInfo;

    @Inject(method = "getLocationCape", at = @At("HEAD"), cancellable = true)
    private void getCape(CallbackInfoReturnable<ResourceLocation> callbackInfoReturnable) {
        if (skidBounce$capeInfo == null) {
            CapeAPI.INSTANCE.loadCape(getUniqueID(), newCapeInfo -> {
                skidBounce$capeInfo = newCapeInfo;
                return null;
            });
        }

        if (skidBounce$capeInfo != null && skidBounce$capeInfo.isCapeAvailable()) {
            callbackInfoReturnable.setReturnValue(skidBounce$capeInfo.getResourceLocation());
        }
    }

    @Inject(method = "getFovModifier", at = @At("HEAD"), cancellable = true)
    private void getFovModifier(CallbackInfoReturnable<Float> callbackInfoReturnable) {
        if (NoFOV.INSTANCE.handleEvents()) {
            float newFOV = NoFOV.INSTANCE.getFov();

            if (!isUsingItem()) {
                callbackInfoReturnable.setReturnValue(newFOV);
                return;
            }

            if (getItemInUse().getItem() != Items.bow) {
                callbackInfoReturnable.setReturnValue(newFOV);
                return;
            }

            int i = getItemInUseDuration();
            float f1 = (float) i / 20f;
            f1 = f1 > 1f ? 1f : f1 * f1;
            newFOV *= 1f - f1 * 0.15f;
            callbackInfoReturnable.setReturnValue(newFOV);
        }
    }

    @Inject(method = "getLocationSkin()Lnet/minecraft/util/ResourceLocation;", at = @At("HEAD"), cancellable = true)
    private void getSkin(CallbackInfoReturnable<ResourceLocation> callbackInfoReturnable) {
        if (NameProtect.INSTANCE.handleEvents() && NameProtect.INSTANCE.getSkinProtect()) {
            if (!NameProtect.INSTANCE.getAllPlayers() && !Objects.equals(getGameProfile().getName(), mc.thePlayer.getGameProfile().getName()))
                return;

            callbackInfoReturnable.setReturnValue(DefaultPlayerSkin.getDefaultSkin(getUniqueID()));
        }
    }
}
