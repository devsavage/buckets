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
import io.savagedev.buckets.init.ModItems;
import io.savagedev.buckets.items.base.BaseItem;
import io.savagedev.buckets.util.ModTooltips;
import io.savagedev.savagecore.item.ItemHelper;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.event.ForgeEventFactory;

import javax.annotation.Nullable;
import java.util.List;

public class ItemInfernalBucket extends BaseItem
{
    protected final Fluid containedFluid;

    public ItemInfernalBucket(Fluid fluid) {
        super(p -> (p.tab(Buckets.modGroup).stacksTo(1).durability(5)));
        this.containedFluid = fluid;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack bucket = playerIn.getItemInHand(handIn);
        HitResult target = ItemHelper.rayTrace(worldIn, playerIn, this.containedFluid == Fluids.EMPTY ? ClipContext.Fluid.SOURCE_ONLY : ClipContext.Fluid.NONE);
        InteractionResultHolder<ItemStack> bucketUseResult = ForgeEventFactory.onBucketUse(playerIn, worldIn, bucket, target);

        if (bucketUseResult != null) {
            return bucketUseResult;
        }

        if (target.getType() == HitResult.Type.MISS) {
            playerIn.startUsingItem(handIn);
            return InteractionResultHolder.pass(bucket);
        } else if (target.getType() != HitResult.Type.BLOCK) {
            return InteractionResultHolder.pass(bucket);
        } else {
            BlockHitResult targetBlock = (BlockHitResult) target;
            BlockPos targetBlockPos = targetBlock.getBlockPos();
            Direction targetBlockDirection = targetBlock.getDirection();

            if (worldIn.mayInteract(playerIn, targetBlockPos) && playerIn.mayUseItemAt(targetBlockPos, targetBlockDirection, bucket)) {
                if (this.containedFluid == Fluids.EMPTY) {
                    BlockState targetBlockState = worldIn.getBlockState(targetBlockPos);

                    if (targetBlockState.getBlock() instanceof BucketPickup) {
                        ItemStack itemStack = ((BucketPickup)targetBlockState.getBlock()).pickupBlock(worldIn, targetBlockPos, targetBlockState);
                        Fluid fluid = targetBlockState.getFluidState().getType();

                        if (fluid != Fluids.EMPTY) {
                            SoundEvent soundevent = this.containedFluid.getAttributes().getEmptySound();
                            if (soundevent == null) {
                                soundevent = SoundEvents.BUCKET_FILL;
                            }

                            playerIn.playSound(soundevent, 1.0F, 1.0F);

                            ItemStack filledBucket = this.fillBucket(bucket, playerIn, ModItems.INFERNAL_BUCKET_FULL.get());

                            return InteractionResultHolder.pass(filledBucket);
                        }
                    }

                    return InteractionResultHolder.fail(bucket);
                }
            }
        }

        return InteractionResultHolder.consume(bucket);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player playerentity = context.getPlayer();
        Level world = context.getLevel();
        BlockPos blockpos = context.getClickedPos();

        BlockPos targetPos = blockpos.offset(context.getClickedPos());
        if (BaseFireBlock.canBePlacedAt(world, targetPos, context.getHorizontalDirection())) {
            world.playSound(playerentity, targetPos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, world.random.nextFloat() * 0.4F + 0.8F);
            BlockState targetState = BaseFireBlock.getState(world, targetPos);
            world.setBlock(targetPos, targetState, 11);
            ItemStack itemstack = context.getItemInHand();
            if (playerentity instanceof ServerPlayer) {
                CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer) playerentity, targetPos, itemstack);
                itemstack.hurtAndBreak(1, playerentity, (player) -> {
                    player.broadcastBreakEvent(context.getHand());
                });
            }

            return InteractionResult.sidedSuccess(world.isClientSide());
        } else {
            return InteractionResult.FAIL;
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level worldIn, LivingEntity entityLiving) {
        if (entityLiving instanceof Player) {
            Player playerIn = (Player) entityLiving;
            if (!worldIn.isClientSide) {
                playerIn.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 150));
                stack.hurtAndBreak(1, playerIn, (playerEntity) -> {
                    playerEntity.broadcastBreakEvent(playerIn.getUsedItemHand());
                });
            }
        }

        return stack;
    }

    public ItemStack fillBucket(ItemStack emptyBucket, Player playerEntity, Item fullBucket) {
        if (playerEntity.isCreative()) {
            return emptyBucket;
        } else {
            emptyBucket.shrink(1);
            if (emptyBucket.isEmpty()) {
                return new ItemStack(fullBucket);
            } else {
                if (!playerEntity.getInventory().add(new ItemStack(fullBucket))) {
                    playerEntity.drop(new ItemStack(fullBucket), false);
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
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public void appendHoverText(ItemStack p_41421_, @Nullable Level p_41422_, List<Component> tooltip, TooltipFlag p_41424_) {
        if(Screen.hasShiftDown()) tooltip.add(new TextComponent(ModTooltips.INFERNAL));
    }
}
