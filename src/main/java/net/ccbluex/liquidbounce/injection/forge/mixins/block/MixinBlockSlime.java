package net.ccbluex.liquidbounce.injection.forge.mixins.block;

import net.ccbluex.liquidbounce.features.module.modules.movement.NoSlow;
import net.minecraft.block.BlockSlime;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(BlockSlime.class)
@SideOnly(Side.CLIENT)
public class MixinBlockSlime {
    @ModifyConstant(method = "onEntityCollidedWithBlock", constant = @Constant(doubleValue = 0.4D, ordinal = -1))
    private double onEntityCollidedWithBlock(double constant) {
        return (NoSlow.INSTANCE.getState() && NoSlow.INSTANCE.getSlime()) ? NoSlow.INSTANCE.getSlimeMultiplier() : 0.4D;
    }
    @ModifyConstant(method = "onEntityCollidedWithBlock", constant = @Constant(doubleValue = 0.2D, ordinal = -1))
    private double onEntityCollidedWithBlockY(double constant) {
        return (NoSlow.INSTANCE.getState() && NoSlow.INSTANCE.getSlime()) ? NoSlow.INSTANCE.getSlimeYMultiplier() : 0.2D;
    }
}