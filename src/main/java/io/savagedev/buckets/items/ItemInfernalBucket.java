package io.savagedev.buckets.items;

/*
 * ItemInfernalBucket.java
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
import io.savagedev.buckets.api.IBucketItem;
import io.savagedev.buckets.init.ModItems;
import io.savagedev.buckets.items.base.BaseItem;
import io.savagedev.buckets.items.base.BaseItemDamageableBucket;
import io.savagedev.buckets.items.enums.DamageType;
import io.savagedev.buckets.util.LogHelper;
import io.savagedev.savagecore.item.ItemHelper;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.UseAction;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import sun.rmi.runtime.Log;

import java.util.function.Function;

public class ItemInfernalBucket extends BaseItem
{
    protected final Fluid containedFluid;

    public ItemInfernalBucket(Fluid fluid) {
        super(p -> (p.group(Buckets.modGroup).maxStackSize(1).maxDamage(5)));
        this.containedFluid = fluid;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack bucket = playerIn.getHeldItem(handIn);
        RayTraceResult target = ItemHelper.rayTrace(worldIn, playerIn, this.containedFluid == Fluids.EMPTY ? RayTraceContext.FluidMode.SOURCE_ONLY : RayTraceContext.FluidMode.NONE);
        ActionResult<ItemStack> bucketUseResult = ForgeEventFactory.onBucketUse(playerIn, worldIn, bucket, target);

        if(bucketUseResult != null) {
            return bucketUseResult;
        }

        if(target.getType() == RayTraceResult.Type.MISS) {
            playerIn.setActiveHand(handIn);
            return ActionResult.resultPass(bucket);
        } else if(target.getType() != RayTraceResult.Type.BLOCK) {
            return ActionResult.resultPass(bucket);
        } else {
            BlockRayTraceResult targetBlock = (BlockRayTraceResult) target;
            BlockPos targetBlockPos = targetBlock.getPos();
            Direction targetBlockDirection = targetBlock.getFace();

            if(worldIn.isBlockModifiable(playerIn, targetBlockPos) && playerIn.canPlayerEdit(targetBlockPos, targetBlockDirection, bucket)) {
                if(this.containedFluid == Fluids.EMPTY) {
                    BlockState targetBlockState = worldIn.getBlockState(targetBlockPos);

                    if(targetBlockState.getBlock() instanceof IBucketPickupHandler) {
                        Fluid fluid = ((IBucketPickupHandler)targetBlockState.getBlock()).pickupFluid(worldIn, targetBlockPos, targetBlockState);

                        if(fluid != Fluids.EMPTY) {
                            SoundEvent soundevent = this.containedFluid.getAttributes().getEmptySound();
                            if (soundevent == null) {
                                soundevent = SoundEvents.ITEM_BUCKET_FILL;
                            }

                            playerIn.playSound(soundevent, 1.0F, 1.0F);

                            ItemStack filledBucket = this.fillBucket(bucket, playerIn, ModItems.INFERNAL_BUCKET_FULL.get());

                            return ActionResult.resultPass(filledBucket);
                        }
                    }

                    return ActionResult.resultFail(bucket);
                }
            }
        }

        return ActionResult.resultConsume(bucket);
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        PlayerEntity playerentity = context.getPlayer();
        World world = context.getWorld();
        BlockPos blockpos = context.getPos();

        BlockPos targetPos = blockpos.offset(context.getFace());
        if (AbstractFireBlock.canLightBlock(world, targetPos, context.getPlacementHorizontalFacing())) {
            world.playSound(playerentity, targetPos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, random.nextFloat() * 0.4F + 0.8F);
            BlockState targetState = AbstractFireBlock.getFireForPlacement(world, targetPos);
            world.setBlockState(targetPos, targetState, 11);
            ItemStack itemstack = context.getItem();
            if (playerentity instanceof ServerPlayerEntity) {
                CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity)playerentity, targetPos, itemstack);
                itemstack.damageItem(1, playerentity, (player) -> {
                    player.sendBreakAnimation(context.getHand());
                });
            }

            return ActionResultType.func_233537_a_(world.isRemote());
        } else {
            return ActionResultType.FAIL;
        }
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving) {
        if(entityLiving instanceof PlayerEntity) {
            PlayerEntity playerIn = (PlayerEntity) entityLiving;
            if(!worldIn.isRemote) {
                playerIn.addPotionEffect(new EffectInstance(Effects.FIRE_RESISTANCE, 150));
                stack.damageItem(1, playerIn, (playerEntity) -> {
                    playerEntity.sendBreakAnimation(playerIn.getActiveHand());
                });
            }
        }

        return stack;
    }

    public ItemStack fillBucket(ItemStack emptyBucket, PlayerEntity playerEntity, Item fullBucket) {
        if(playerEntity.isCreative()) {
            return emptyBucket;
        } else {
            emptyBucket.shrink(1);
            if(emptyBucket.isEmpty()) {
                return new ItemStack(fullBucket);
            } else {
                if(!playerEntity.inventory.addItemStackToInventory(new ItemStack(fullBucket))) {
                    playerEntity.dropItem(new ItemStack(fullBucket), false);
                }

                return emptyBucket;
            }
        }
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 32;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.DRINK;
    }
}
