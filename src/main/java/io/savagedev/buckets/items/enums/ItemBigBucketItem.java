package io.savagedev.buckets.items.enums;

/*
 * ItemBigBucketItem.java
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

import io.savagedev.buckets.util.ModNames;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.IStringSerializable;

import java.util.Arrays;
import java.util.Comparator;

public enum ItemBigBucketItem implements IStringSerializable
{
    OBSIDIAN_BUCKET_EMPTY(ModNames.Items.OBSIDIAN_BUCKET, Fluids.EMPTY, 0),
    OBSIDIAN_BUCKET_LAVA(ModNames.Items.OBSIDIAN_BUCKET, Fluids.LAVA,  300),
    OBSIDIAN_BUCKET_WATER(ModNames.Items.OBSIDIAN_BUCKET, Fluids.WATER,  300),

    GOLD_BUCKET_EMPTY(ModNames.Items.GOLD_BUCKET, Fluids.EMPTY, 0),
    GOLD_BUCKET_LAVA(ModNames.Items.GOLD_BUCKET, Fluids.LAVA,  400),
    GOLD_BUCKET_WATER(ModNames.Items.GOLD_BUCKET, Fluids.WATER,  400),

    EMERALD_BUCKET_EMPTY(ModNames.Items.EMERALD_BUCKET, Fluids.EMPTY, 0),
    EMERALD_BUCKET_LAVA(ModNames.Items.EMERALD_BUCKET, Fluids.LAVA,  500),
    EMERALD_BUCKET_WATER(ModNames.Items.EMERALD_BUCKET, Fluids.WATER,  500),

    QUARTZ_BUCKET_EMPTY(ModNames.Items.QUARTZ_BUCKET, Fluids.EMPTY, 0),
    QUARTZ_BUCKET_LAVA(ModNames.Items.QUARTZ_BUCKET, Fluids.LAVA,  500),
    QUARTZ_BUCKET_WATER(ModNames.Items.QUARTZ_BUCKET, Fluids.WATER,  500),

    DIAMOND_BUCKET_EMPTY(ModNames.Items.DIAMOND_BUCKET, Fluids.EMPTY, 0),
    DIAMOND_BUCKET_LAVA(ModNames.Items.DIAMOND_BUCKET, Fluids.LAVA,  600),
    DIAMOND_BUCKET_WATER(ModNames.Items.DIAMOND_BUCKET, Fluids.WATER,  600);

    private static final ItemBigBucketItem[] VALUES = Arrays.stream(values()).sorted(Comparator.comparing(ItemBigBucketItem::getString)).toArray((bucketName) -> {
        return new ItemBigBucketItem[bucketName];
    });

    private final String bucketName;
    private final Fluid fluidDef;
    private final int bucketMaxDamage;

    private ItemBigBucketItem(String bucketNameIn, Fluid fluidDef, int bucketMaxDamageIn) {
        this.bucketName = bucketNameIn;
        this.fluidDef = fluidDef;
        this.bucketMaxDamage = bucketMaxDamageIn;
    }

    public Fluid getFluidType() {
        return fluidDef;
    }

    public String getEmptyBucket() {
        return this.bucketName + "_empty";
    }

    public String getLavaBucket() {
        return this.bucketName + "_lava";
    }

    public String getWaterBucket() {
        return this.bucketName + "_water";
    }

    public int getBucketDamage() {
        return bucketMaxDamage;
    }

    @Override
    public String getString() {
        return this.bucketName + "_" + this.fluidDef.getRegistryName().getPath();
    }
}
