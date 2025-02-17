package com.hbm.hazard.type;

import com.hbm.config.BombConfig;
import com.hbm.entity.logic.EntityNukeExplosionMK5;
import com.hbm.explosion.ExplosionNT.ExAttrib;
import com.hbm.handler.radiation.ChunkRadiationManager;
import com.hbm.main.MainRegistry;
import com.hbm.packet.PacketDispatcher;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import com.hbm.config.RadiationConfig;
import com.hbm.explosion.ExplosionNukeSmall;
import com.hbm.hazard.modifier.HazardModifier;
import com.hbm.util.I18nUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import java.util.List;

public class HazardTypeHydrogolic extends HazardTypeBase {

    @Override
    public void onUpdate(EntityLivingBase target, float level, ItemStack stack) {

        if(RadiationConfig.disableHydro)
            return;

        if(target.isWet() && stack.stackSize > 0) {
            stack.stackSize = 0;
            target.worldObj.spawnEntityInWorld(EntityNukeExplosionMK5.statFac(target.worldObj, (int)level, target.posX + 0.5, target.posY + 5, target.posZ + 0.5));

        }
    }

    @Override
    public void updateEntity(EntityItem item, float level) {

        if(RadiationConfig.disableHydro)
            return;

        if(item.isWet()) {
            item.setDead();
            item.worldObj.spawnEntityInWorld(EntityNukeExplosionMK5.statFac(item.worldObj, (int)level, item.posX + 0.5, item.posY + 5, item.posZ + 0.5));
        }
    }

    @Override
    public void addHazardInformation(EntityPlayer player, List list, float level, ItemStack stack, List<HazardModifier> modifiers) {
        list.add(EnumChatFormatting.RED + "[" + I18nUtil.resolveKey("trait.hydrosuper") + "]");
    }
}
