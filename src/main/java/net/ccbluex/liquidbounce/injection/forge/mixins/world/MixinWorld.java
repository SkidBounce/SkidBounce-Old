/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.injection.forge.mixins.world;

import de.florianmichael.viamcp.fixes.FixedSoundEngine;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(World.class)
public abstract class MixinWorld {
    @Shadow
    public abstract IBlockState getBlockState(BlockPos pos);

    @Shadow public abstract World init();

    /**
     * @author ManInMyVan / SkidBounce
     * @reason ViaMCP
     */
    @Overwrite
    public boolean destroyBlock(BlockPos p_destroyBlock_1_, boolean p_destroyBlock_2_) {
        return FixedSoundEngine.destroyBlock(this.init(), p_destroyBlock_1_, p_destroyBlock_2_);
    }
}
