package com.ldtteam.treasuredistance.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net/minecraft/client/gui/MapRenderer$MapInstance")
public class MapRendererMixin
{
    @Inject(method = "draw", at = @At("TAIL"))
    public void drawInject(final PoseStack p_93292_, final MultiBufferSource p_93293_, final boolean p_93294_, final int p_93295_, final CallbackInfo ci)
    {
        final Player player = Minecraft.getInstance().player;
        final ItemStack stack = player.getMainHandItem();
        if (stack.getItem() == Items.FILLED_MAP && stack.hasTag() && stack.getTag().contains("Decorations"))
        {
            final ListTag listTag = stack.getTag().getList("Decorations", Tag.TAG_COMPOUND);
            for (int i = 0; i < listTag.size(); i++)
            {
                final CompoundTag subTag = listTag.getCompound(i);
                if (subTag.getByte("type") == 26)
                {
                    final double x = subTag.getDouble("x");
                    final double z = subTag.getDouble("z");

                    final BlockPos goalVec = new BlockPos(x, 0, z);
                    final BlockPos targetVec = new BlockPos(player.getX(), 0, player.getZ());

                    final int dist = goalVec.distManhattan(targetVec);

                    Font font = Minecraft.getInstance().font;
                    Component component = Component.literal(dist + "blocks");
                    float f6 = (float) font.width(component);
                    float f7 = Mth.clamp(25.0F / f6, 0.0F, 6.0F / 9.0F);
                    p_93292_.pushPose();
                    p_93292_.translate(0.0F + 64.0F - f6 * f7 / 2.0F, 0.0F + 64.0F + 4.0F, -0.025F);
                    p_93292_.scale(f7, f7, 1.0F);
                    p_93292_.translate(0.0F, 0.0F, -0.1F);
                    font.drawInBatch(component, 0.0F, 0.0F, -1, false, p_93292_.last().pose(), p_93293_, false, Integer.MIN_VALUE, p_93295_);
                    p_93292_.popPose();

                    break;
                }
            }
        }
    }
}
