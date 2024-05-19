package com.hbm.blocks.bomb;

import org.apache.logging.log4j.Level;

import com.hbm.config.GeneralConfig;
import com.hbm.main.MainRegistry;

import com.hbm.entity.item.EntityTNTPrimedBase;

import codechicken.lib.math.MathHelper;
import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class DetCord extends BlockDetonatable implements IDetConnectible {

	public DetCord(Material material) {
		super(material, 0, 0, 0, false, false);
	}
	
	public static int renderID = RenderingRegistry.getNextAvailableRenderId();

	@Override
	public int getRenderType() {
		return renderID;
	}
	
	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block p_149695_5_) {
		if(world.isBlockIndirectlyGettingPowered(x, y, z)) {
			this.explode(world, x, y, z);
		}
	}

	public void explode(World world, int x, int y, int z) {
		if(!world.isRemote) {
			world.setBlock(x, y, z, Blocks.air);
			world.createExplosion(null, x + 0.5, y + 0.5, z + 0.5, 1.5F, true);
		}
	}
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack itemStack) {
	if(!world.isRemote) {
			if(GeneralConfig.enableExtendedLogging) {
			MainRegistry.logger.log(Level.INFO, "[BOMBPL]" + this.getLocalizedName() + " placed at " + x + " / " + y + " / " + z + "! " + "by "+ player.getCommandSenderName());
		}
	}

	}

	@Override
	public void explodeEntity(World world, double x, double y, double z, EntityTNTPrimedBase entity) {
		explode(world, MathHelper.floor_double(x), MathHelper.floor_double(y), MathHelper.floor_double(z));
	}
}
