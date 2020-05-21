package io.savagedev.buckets.items;

/*
 * ItemBigBucket.java
 * Copyright (C) 2020 Savage - github.com/devsavage
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
import io.savagedev.buckets.api.IBucketItem;
import io.savagedev.buckets.items.base.BaseItemDamageableBucket;
import io.savagedev.buckets.items.enums.DamageType;
import io.savagedev.buckets.items.enums.ItemBigBucketItem;
import io.savagedev.buckets.util.ModReference;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public class ItemBigBucket extends BaseItemDamageableBucket implements IBucketItem
{
    public final ItemBigBucketItem bucketItem;

    public ItemBigBucket(ItemBigBucketItem bucketItem) {
        super(p -> (p.group(Buckets.modGroup).maxStackSize(1).maxDamage(bucketItem.getBucketDamage())), bucketItem.getFluidType(), DamageType.BIG);
        this.bucketItem = bucketItem;
    }

    @Override
    public Item getEmptyBucketItem() {
        return Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation(ModReference.MOD_ID, this.bucketItem.getEmptyBucket()))).getItem();
    }

    @Override
    public Item getLavaBucketItem() {
        return Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation(ModReference.MOD_ID, this.bucketItem.getLavaBucket()))).getItem();
    }

    @Override
    public Item getWaterBucketItem() {
        return Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation(ModReference.MOD_ID, this.bucketItem.getWaterBucket()))).getItem();
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if(stack.getMaxDamage() == 0) {
            tooltip.add(new StringTextComponent("Uses: 1"));
        } else {
            tooltip.add(new StringTextComponent("Uses: " + (this.getMaxDamage(stack) - this.getDamage(stack)) / 100));
        }
    }
}
