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
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.UseAction;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class ItemExplosiveBucket extends BaseItem
{
    public ItemExplosiveBucket() {
        super(p -> p.maxStackSize(1).maxDamage(32).group(Buckets.modGroup));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack bucket = playerIn.getHeldItem(handIn);

        if(bucket.getItem() == ModItems.EXPLOSIVE_BUCKET_FULL.get()) {
            playerIn.setActiveHand(handIn);
            return ActionResult.resultConsume(bucket);
        }

        return ActionResult.resultPass(bucket);
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
        if (entityLiving instanceof PlayerEntity) {
            PlayerEntity playerIn = (PlayerEntity) entityLiving;
            boolean isExplosiveBucket = playerIn.getHeldItem(Hand.MAIN_HAND).getItem() == ModItems.EXPLOSIVE_BUCKET_FULL.get();

            if (!worldIn.isRemote && isExplosiveBucket) {
                float launchVelocity = 0.4F;
                LogHelper.debug(launchVelocity);
                TNTEntity tntentity = new TNTEntity(worldIn, playerIn.getPosition().getX(), playerIn.getPosition().getY(), playerIn.getPosition().getZ(), playerIn);
                this.launch(tntentity, playerIn.rotationPitch, playerIn.rotationYaw, 0.0F, launchVelocity, 1.0F);
                worldIn.addEntity(tntentity);
                worldIn.playSound(null, tntentity.getPosX(), tntentity.getPosY(), tntentity.getPosZ(), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);

                stack.damageItem(8, playerIn, (playerEntity) -> playerEntity.sendBreakAnimation(playerIn.getActiveHand()));
            }
        }
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
                context.getWorld().setBlockState(context.getPos(), Blocks.AIR.getDefaultState());
            } else if(isExplosiveBucket) {
                int bucketDmg = context.getItem().getDamage();
                int newDmg = context.getItem().getDamage() - 8;
                if(bucketDmg >= context.getItem().getMaxDamage() || newDmg > context.getItem().getMaxDamage()) {
                    context.getItem().setDamage(0);
                }

                context.getItem().setDamage(context.getItem().getDamage() - 8);

                if(bucketDmg > 0) {
                    context.getWorld().setBlockState(context.getPos(), Blocks.AIR.getDefaultState());
                }
            }
        }

        return ActionResultType.SUCCESS;
    }


    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    public void shoot(Entity entity, double x, double y, double z, float velocity, float inaccuracy) {
        Random rand = new Random();
        Vector3d vector3d = (new Vector3d(x, y, z)).normalize().add(rand.nextGaussian() * (double)0.0025F * (double)inaccuracy, rand.nextGaussian() * (double)0.0025F * (double)inaccuracy, rand.nextGaussian() * (double)0.0025F * (double)inaccuracy).scale(velocity);
        entity.setMotion(vector3d);
        float getHorizontal = MathHelper.sqrt(horizontalMag(vector3d));
        entity.rotationYaw = (float)(MathHelper.atan2(vector3d.x, vector3d.z) * (double)(180F / (float)Math.PI));
        entity.rotationPitch = (float)(MathHelper.atan2(vector3d.y, getHorizontal) * (double)(180F / (float)Math.PI));
        entity.prevRotationYaw = entity.rotationYaw;
        entity.prevRotationPitch = entity.rotationPitch;
    }

    public void launch(Entity entity, float x, float y, float z, float velocity, float inaccuracy) {
        float pX = -MathHelper.sin(y * ((float)Math.PI / 180F)) * MathHelper.cos(x * ((float)Math.PI / 180F));
        float pY = -MathHelper.sin((x + z) * ((float)Math.PI / 180F));
        float pZ = MathHelper.cos(y * ((float)Math.PI / 180F)) * MathHelper.cos(x * ((float)Math.PI / 180F));
        this.shoot(entity, pX, pY, pZ, velocity, inaccuracy);
        Vector3d vector3d = entity.getMotion();
        entity.setMotion(entity.getMotion().add(vector3d.x, entity.isOnGround() ? 0.0D : vector3d.y, vector3d.z));
    }

    public static double horizontalMag(Vector3d vec) {
        return vec.x * vec.x + vec.z * vec.z;
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
