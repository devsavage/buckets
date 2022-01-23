package io.savagedev.buckets.items;

/*
 * ItemShimmeringBucket.java
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
import io.savagedev.savagecore.item.ItemHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class ItemShimmeringBucket extends BaseItem
{
    public ItemShimmeringBucket() {
        super(p -> p.stacksTo(1).durability(256).tab(Buckets.modGroup));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack bucket = playerIn.getItemInHand(handIn);
        HitResult target = ItemHelper.rayTrace(worldIn, playerIn, ClipContext.Fluid.SOURCE_ONLY);
        BlockHitResult targetBlock = (BlockHitResult) target;
        BlockPos targetBlockPos = targetBlock.getBlockPos();
        BlockState targetBlockState = worldIn.getBlockState(targetBlock.getBlockPos());

        if(targetBlockState.getBlock() instanceof BucketPickup) {
            ItemStack itemStack = ((BucketPickup)targetBlockState.getBlock()).pickupBlock(worldIn, targetBlockPos, targetBlockState);
            Fluid fluid = targetBlockState.getFluidState().getType();

            if(fluid != Fluids.EMPTY) {
                SoundEvent soundevent = Fluids.LAVA.getAttributes().getEmptySound();
                if (soundevent == null) {
                    soundevent = SoundEvents.BUCKET_FILL_LAVA;
                }

                playerIn.playSound(soundevent, 1.0F, 1.0F);

                int minExp = 3;
                int maxExp = 6;
                int finalExp = minExp + worldIn.random.nextInt(maxExp - minExp + 1);

                if(!worldIn.isClientSide) {
                    int expSplit = ExperienceOrb.getExperienceValue(finalExp);
                    finalExp -= expSplit;
                    worldIn.addFreshEntity(new ExperienceOrb(worldIn, playerIn.getX() + 0.5D, playerIn.getY() + 0.5D, playerIn.getZ() + 0.5D, expSplit));
                }

                bucket.hurtAndBreak(1, playerIn, (playerEntity -> {
                    playerEntity.broadcastBreakEvent(handIn);
                }));

                return InteractionResultHolder.success(bucket);
            }
        }

        return InteractionResultHolder.pass(bucket);
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return Rarity.UNCOMMON;
    }
}
