package io.savagedev.buckets.util;

/*
 * NBTHelper.java
 * A wrapper to easily get & set CompoundNBT tags on ItemStacks.
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

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public class NBTHelper
{
    private static void initNBTTagCompound(ItemStack itemStack) {
        if (itemStack.getTag() == null) {
            itemStack.setTag(new CompoundNBT());
        }
    }

    public static boolean hasTag(ItemStack itemStack, String keyName) {
        return itemStack.hasTag() && itemStack.getTag().contains(keyName);
    }

    public static int getInt(ItemStack itemStack, String keyName) {
        initNBTTagCompound(itemStack);

        if (!itemStack.getTag().contains(keyName)) {
            setInt(itemStack, keyName, 0);
        }

        return itemStack.getTag().getInt(keyName);
    }

    public static void setInt(ItemStack itemStack, String keyName, int keyValue) {
        initNBTTagCompound(itemStack);

        itemStack.getTag().putInt(keyName, keyValue);
    }

    public static String getString(ItemStack itemStack, String keyName) {
        initNBTTagCompound(itemStack);

        if (!itemStack.getTag().contains(keyName)) {
            setString(itemStack, keyName, "");
        }

        return itemStack.getTag().getString(keyName);
    }

    public static void setString(ItemStack itemStack, String keyName, String keyValue) {
        initNBTTagCompound(itemStack);

        itemStack.getTag().putString(keyName, keyValue);
    }

    public static long getLong(ItemStack itemStack, String keyName) {
        initNBTTagCompound(itemStack);

        if (!itemStack.getTag().contains(keyName)) {
            setLong(itemStack, keyName, 0L);
        }

        return itemStack.getTag().getLong(keyName);
    }

    public static void setLong(ItemStack itemStack, String keyName, long keyValue) {
        initNBTTagCompound(itemStack);

        itemStack.getTag().putLong(keyName, keyValue);
    }
}
