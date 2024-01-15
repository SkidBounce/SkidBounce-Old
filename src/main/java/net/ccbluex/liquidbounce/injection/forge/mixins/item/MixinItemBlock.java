/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.injection.forge.mixins.item;

import de.florianmichael.viamcp.fixes.FixedSoundEngine;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ItemBlock.class)
@SideOnly(Side.CLIENT)
public abstract class MixinItemBlock {
    @Shadow @Final public Block block;

    @Shadow public abstract ItemBlock setUnlocalizedName(String p_setUnlocalizedName_1_);

    @Shadow public abstract String getUnlocalizedName();

    /**
     * @author ManInMyVan / SkidBounce
     * @reason ViaMCP
     */
    @Overwrite
    public boolean onItemUse(ItemStack p_onItemUse_1_, EntityPlayer p_onItemUse_2_, World p_onItemUse_3_, BlockPos p_onItemUse_4_, EnumFacing p_onItemUse_5_, float p_onItemUse_6_, float p_onItemUse_7_, float p_onItemUse_8_) {
        return FixedSoundEngine.onItemUse(this.setUnlocalizedName(this.getUnlocalizedName()), p_onItemUse_1_, p_onItemUse_2_, p_onItemUse_3_, p_onItemUse_4_, p_onItemUse_5_, p_onItemUse_6_, p_onItemUse_7_, p_onItemUse_8_);
    }
}
