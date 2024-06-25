/*
 * SkidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge, Forked from LiquidBounce.
 * https://github.com/ManInMyVan/SkidBounce/
 */
package net.ccbluex.liquidbounce.injection.forge.mixins.block;

import net.ccbluex.liquidbounce.features.module.modules.movement.FastClimb;
import net.minecraft.block.BlockLadder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(BlockLadder.class)
@SideOnly(Side.CLIENT)
public abstract class MixinBlockLadder extends MixinBlock {

    @ModifyConstant(method = "setBlockBoundsBasedOnState", constant = @Constant(floatValue = 0.125f))
    private float setBlockBoundsBasedOnState(float constant) {
        return FastClimb.INSTANCE.handleEvents() && FastClimb.getMode().equals("AAC3.0.0") ? 0.99f : 0.125f;
    }
}
