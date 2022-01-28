package io.savagedev.buckets.items;

/*
 * ItemMagmaCreamBucket.java
 * Copyright (C) 2021 Savage - github.com/devsavage
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

import io.savagedev.buckets.Buckets;
import io.savagedev.buckets.items.base.BaseItem;
import io.savagedev.buckets.util.ModTooltips;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.MagmaCube;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class ItemMagmaCreamBucket extends BaseItem
{
    protected Random random = new Random();

    public ItemMagmaCreamBucket() {
        super(p -> p.stacksTo(1).durability(256).tab(Buckets.modGroup));
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player playerIn, LivingEntity target, InteractionHand hand) {
        double rand  = Math.random();
        int dropAmt = 1 + this.random.nextInt(2);

        if(target instanceof MagmaCube) {
            if(rand < 0.05D) {
                for(int i = 0; i < dropAmt; ++i) {
                    target.spawnAtLocation(Items.MAGMA_CREAM, 2);
                }

                return InteractionResult.SUCCESS;
            }
        }

        stack.hurtAndBreak(1, playerIn, (playerEntity) -> {
            playerEntity.broadcastBreakEvent(InteractionHand.MAIN_HAND);
        });

        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        if(Screen.hasShiftDown()) tooltip.add(new TextComponent(ModTooltips.MAGMA));
    }
}
