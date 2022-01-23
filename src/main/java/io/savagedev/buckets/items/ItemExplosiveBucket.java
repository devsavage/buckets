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

import com.mojang.math.Vector3d;
import io.savagedev.buckets.Buckets;
import io.savagedev.buckets.init.BucketsConfig;
import io.savagedev.buckets.init.ModItems;
import io.savagedev.buckets.items.base.BaseItem;
import io.savagedev.buckets.util.LogHelper;
import io.savagedev.savagecore.item.ItemHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class ItemExplosiveBucket extends BaseItem
{
    public ItemExplosiveBucket() {
        super(p -> p.stacksTo(1).durability(32).tab(Buckets.modGroup));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack bucket = playerIn.getItemInHand(handIn);

        if(bucket.getItem() == ModItems.EXPLOSIVE_BUCKET_FULL.get()) {
            playerIn.startUsingItem(handIn);
            return InteractionResultHolder.consume(bucket);
        }

        return InteractionResultHolder.pass(bucket);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level worldIn, LivingEntity entityLiving, int timeLeft) {
        if (entityLiving instanceof Player) {
            Player playerIn = (Player) entityLiving;
            boolean isExplosiveBucket = playerIn.getItemInHand(InteractionHand.MAIN_HAND).getItem() == ModItems.EXPLOSIVE_BUCKET_FULL.get();

            if (!worldIn.isClientSide && isExplosiveBucket) {
                float launchVelocity = this.calculateVelocity();
                PrimedTnt tntentity = new PrimedTnt(worldIn, playerIn.getOnPos().getX(), playerIn.getOnPos().getY(), playerIn.getOnPos().getZ(), playerIn);
                this.launch(tntentity, playerIn.getXRot(), playerIn.getYRot(), 0.0F, launchVelocity, 1.0F);
                worldIn.addFreshEntity(tntentity);
                worldIn.playSound(null, tntentity.getX(), tntentity.getY(), tntentity.getZ(), SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);

                stack.hurtAndBreak(8, playerIn, (playerEntity) -> playerEntity.broadcastBreakEvent(playerIn.getUsedItemHand()));
            }
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        boolean isEmptyBucket = ItemHelper.equalsIgnoreStackSize(context.getItemInHand(), new ItemStack(ModItems.EXPLOSIVE_BUCKET_EMPTY.get()));
        boolean isExplosiveBucket = context.getItemInHand().getItem() == ModItems.EXPLOSIVE_BUCKET_FULL.get();
        boolean isTargetTnt = context.getLevel().getBlockState(context.getClickedPos()).getBlock() == Blocks.TNT;

        if(isTargetTnt) {
            if(isEmptyBucket) {
                ItemStack fillBucket = new ItemStack(ModItems.EXPLOSIVE_BUCKET_FULL.get());
                fillBucket.setDamageValue(24);

                Objects.requireNonNull(context.getPlayer()).setItemInHand(context.getHand(), fillBucket);
                context.getLevel().setBlockAndUpdate(context.getClickedPos(), Blocks.AIR.defaultBlockState());
            } else if(isExplosiveBucket) {
                int bucketDmg = context.getItemInHand().getDamageValue();
                int newDmg = context.getItemInHand().getDamageValue() - 8;
                if(bucketDmg >= context.getItemInHand().getMaxDamage() || newDmg > context.getItemInHand().getMaxDamage()) {
                    context.getItemInHand().setDamageValue(0);
                }

                context.getItemInHand().setDamageValue(context.getItemInHand().getDamageValue() - 8);

                if(bucketDmg > 0) {
                    context.getLevel().setBlockAndUpdate(context.getClickedPos(), Blocks.AIR.defaultBlockState());
                }
            }
        }

        return InteractionResult.SUCCESS;
    }


    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    public void shoot(Entity entity, double x, double y, double z, float velocity, float inaccuracy) {
        Random rand = new Random();
        Vec3 vector3d = (new Vec3(x, y, z)).normalize().add(rand.nextGaussian() * (double)0.0025F * (double)inaccuracy, rand.nextGaussian() * (double)0.0025F * (double)inaccuracy, rand.nextGaussian() * (double)0.0025F * (double)inaccuracy).scale(velocity);
        entity.setDeltaMovement(vector3d);
        float getHorizontal = Mth.sqrt((float)horizontalMag(vector3d));
        entity.setXRot((float)(Mth.atan2(vector3d.x, vector3d.z) * (double)(180F / (float)Math.PI)));
        entity.setYRot((float)(Mth.atan2(vector3d.y, getHorizontal) * (double)(180F / (float)Math.PI)));
        entity.xRotO = entity.getXRot();
        entity.yRotO = entity.getYRot();
    }

    public void launch(Entity entity, float x, float y, float z, float velocity, float inaccuracy) {
        float pX = -Mth.sin(y * ((float)Math.PI / 180F)) * Mth.cos(x * ((float)Math.PI / 180F));
        float pY = -Mth.sin((x + z) * ((float)Math.PI / 180F));
        float pZ = Mth.cos(y * ((float)Math.PI / 180F)) * Mth.cos(x * ((float)Math.PI / 180F));
        this.shoot(entity, pX, pY, pZ, velocity, inaccuracy);
        Vec3 vector3d = entity.getDeltaMovement();
        entity.setDeltaMovement(entity.getDeltaMovement().add(vector3d.x, entity.isOnGround() ? 0.0D : vector3d.y, vector3d.z));
    }

    public static double horizontalMag(Vec3 vec) {
        return vec.x * vec.x + vec.z * vec.z;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        if(stack.getItem() == ModItems.EXPLOSIVE_BUCKET_FULL.get()) {
            if(stack.getDamageValue() == 0) {
                tooltip.add(new TextComponent("Uses: 4"));
            } else {
                tooltip.add(new TextComponent("Uses: " + (this.getMaxDamage(stack) - this.getDamage(stack)) / 8));
            }
        }
    }

    private float calculateVelocity() {
        return BucketsConfig.MAX_EXPLOSIVE_BUCKET_VELOCITY.get().floatValue();
    }
}
