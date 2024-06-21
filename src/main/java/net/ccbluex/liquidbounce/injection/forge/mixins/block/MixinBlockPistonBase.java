/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.injection.forge.mixins.block;

import net.ccbluex.liquidbounce.features.module.modules.movement.NoPush;
import net.minecraft.block.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockPistonBase.class)
public class MixinBlockPistonBase {
    // TODO: there must be a better way to do this
    @Redirect(method = "doMove", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setTileEntity(Lnet/minecraft/util/BlockPos;Lnet/minecraft/tileentity/TileEntity;)V", ordinal = 1))
    private void pistonNoPush(World world, BlockPos pos, TileEntity tileEntity) {
        if (!NoPush.INSTANCE.handleEvents() || !NoPush.INSTANCE.getPistons()) world.setTileEntity(pos, tileEntity);
    }

    @Redirect(method = "doMove", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setTileEntity(Lnet/minecraft/util/BlockPos;Lnet/minecraft/tileentity/TileEntity;)V", ordinal = 0))
    private void pistonBlockNoPush(World world, BlockPos pos, TileEntity tileEntity) {
        if (!NoPush.INSTANCE.handleEvents() || !NoPush.INSTANCE.getPistonBlocks()) world.setTileEntity(pos, tileEntity);
    }
}
