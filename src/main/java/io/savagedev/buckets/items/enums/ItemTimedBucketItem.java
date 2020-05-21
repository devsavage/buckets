package io.savagedev.buckets.items.enums;

/*
 * ItemTimedBucketItem.java
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

public enum ItemTimedBucketItem implements IStringSerializable
{
    WOODEN_BUCKET_EMPTY(ModNames.Items.WOODEN_BUCKET, Fluids.EMPTY, 0),
    WOODEN_BUCKET_WATER(ModNames.Items.WOODEN_BUCKET, Fluids.WATER, 20),
    WOODEN_BUCKET_LAVA(ModNames.Items.WOODEN_BUCKET, Fluids.LAVA, 20),

    COBBLESTONE_BUCKET_EMPTY(ModNames.Items.COBBLESTONE_BUCKET, Fluids.EMPTY, 0),
    COBBLESTONE_BUCKET_WATER(ModNames.Items.COBBLESTONE_BUCKET, Fluids.WATER, 60),
    COBBLESTONE_BUCKET_LAVA(ModNames.Items.COBBLESTONE_BUCKET, Fluids.LAVA, 60),

    SMOOTHSTONE_BUCKET_EMPTY(ModNames.Items.SMOOTHSTONE_BUCKET, Fluids.EMPTY, 0),
    SMOOTHSTONE_BUCKET_WATER(ModNames.Items.SMOOTHSTONE_BUCKET, Fluids.WATER, 300),
    SMOOTHSTONE_BUCKET_LAVA(ModNames.Items.SMOOTHSTONE_BUCKET, Fluids.LAVA, 300);

    private static final ItemTimedBucketItem[] VALUES = Arrays.stream(values()).sorted(Comparator.comparing(ItemTimedBucketItem::getName)).toArray((bucketName) -> {
        return new ItemTimedBucketItem[bucketName];
    });

    private final String bucketName;
    private final Fluid fluidDef;
    private final int bucketMaxTime;

    private ItemTimedBucketItem(String bucketNameIn, Fluid fluidDef, int bucketMaxTimeIn) {
        this.bucketName = bucketNameIn;
        this.fluidDef = fluidDef;
        this.bucketMaxTime = bucketMaxTimeIn;
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

    @Override
    public String getName() {
        return this.bucketName + "_" + this.fluidDef.getRegistryName().getPath();
    }

    public int getBucketMaxTime() {
        return bucketMaxTime;
    }
}
