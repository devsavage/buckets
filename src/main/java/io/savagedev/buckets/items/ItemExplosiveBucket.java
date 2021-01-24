package io.savagedev.buckets.items;

/*
 * ItemExplosiveBucket.java
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
import io.savagedev.buckets.init.ModItems;
import io.savagedev.buckets.items.base.BaseItem;
import io.savagedev.buckets.util.LogHelper;
import io.savagedev.savagecore.item.ItemHelper;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class ItemExplosiveBucket extends BaseItem
{
    public ItemExplosiveBucket() {
        super(p -> p.maxStackSize(1).maxDamage(32).group(Buckets.modGroup));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        boolean isExplosiveBucket = playerIn.getHeldItem(handIn).getItem() == ModItems.EXPLOSIVE_BUCKET_FULL.get();

        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        boolean isEmptyBucket = ItemHelper.equalsIgnoreStackSize(context.getItem(), new ItemStack(ModItems.EXPLOSIVE_BUCKET_EMPTY.get()));
        boolean isExplosiveBucket = context.getItem().getItem() == ModItems.EXPLOSIVE_BUCKET_FULL.get();
        boolean isTargetTnt = context.getWorld().getBlockState(context.getPos()).getBlock() == Blocks.TNT;

        if(isTargetTnt) {
            if(isEmptyBucket) {
                ItemStack fillBucket = new ItemStack(ModItems.EXPLOSIVE_BUCKET_FULL.get());
                fillBucket.setDamage(24);

                Objects.requireNonNull(context.getPlayer()).setHeldItem(context.getHand(), fillBucket);
            } else if(isExplosiveBucket) {
                int bucketDmg = context.getItem().getDamage();
                int newDmg = context.getItem().getDamage() - 8;
                if(bucketDmg >= context.getItem().getMaxDamage() || newDmg > context.getItem().getMaxDamage()) {
                    context.getItem().setDamage(0);
                }

                context.getItem().setDamage(context.getItem().getDamage() - 8);
            }
        }

        return ActionResultType.SUCCESS;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if(stack.getItem() == ModItems.EXPLOSIVE_BUCKET_FULL.get()) {
            if(stack.getDamage() == 0) {
                tooltip.add(new StringTextComponent("Uses: 4"));
            } else {
                tooltip.add(new StringTextComponent("Uses: " + (this.getMaxDamage(stack) - this.getDamage(stack)) / 8));
            }
        }
    }
}
