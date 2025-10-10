package com.ldtteam.treasuredistance.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.MapRenderState;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.component.MapDecorations;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.joml.Vector2d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net/minecraft/client/renderer/MapRenderer")
public class MapRendererMixin
{
    @Inject(method = "render", at = @At("TAIL"))
    public void renderInject(MapRenderState p_360365_, PoseStack p_93292_, SubmitNodeCollector p_434609_, boolean p_93294_, int p_93295_, final CallbackInfo ci)
    {
        final Player player = Minecraft.getInstance().player;

        final ItemStack stack = player.getMainHandItem();
        if (stack.getItem() == Items.FILLED_MAP)
        {
            for (MapDecorations.Entry deco : stack.getComponents().get(DataComponents.MAP_DECORATIONS).decorations().values())
            {
                if (deco.type().value().showOnItemFrame())
                {
                    final double x = deco.x();
                    final double z = deco.z();

                    final Vector2d goalVec = new Vector2d(x, z);
                    final Vector2d targetVec = new Vector2d(player.getX(), player.getZ());

                    final int dist = (int) goalVec.distance(targetVec);
                    Font font = Minecraft.getInstance().font;
                    Component component = Component.literal(dist + " " + Component.translatable("soundCategory.block").getString());

                    float textWidth = (float) font.width(component);
                    float offset = Mth.clamp(25.0F / textWidth, 0.0F, 6.0F / 9.0F);
                    p_93292_.pushPose();
                    p_93292_.translate(64.0F - textWidth * offset / 2.0F, 64.0F + 4.0F, -0.025F);
                    p_93292_.scale(offset, offset, -1.2F);
                    p_93292_.translate(0.0F, 0.0F, 0.1F);

                    p_434609_.order(1)
                        .submitText(
                            p_93292_,
                            0.0F,
                            0.0F,
                            component.getVisualOrderText(),
                            false,
                            Font.DisplayMode.NORMAL,
                            p_93295_,
                            -1,
                            Integer.MIN_VALUE,
                            0
                        );

                    p_93292_.popPose();

                    break;
                }
            }
        }
    }
}
