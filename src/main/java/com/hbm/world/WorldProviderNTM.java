package com.hbm.world;

import com.hbm.handler.ImpactWorldHandler;
import com.hbm.main.MainRegistry;
import com.hbm.saveddata.RogueWorldSaveData;
import com.hbm.saveddata.TomSaveData;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.Chunk;

public class WorldProviderNTM extends WorldProviderSurface {

	private float[] colorsSunriseSunset = new float[4];

	//    public WorldChunkManagerNTM worldChunkMgr;
	public WorldProviderNTM() {
	}

	/*@Override
    public void registerWorldChunkManager()
    {
		this.worldChunkMgr = new WorldChunkManagerNTM();
    }*/
	@Override
	public float calculateCelestialAngle(long worldTime, float partialTicks) {
		return super.calculateCelestialAngle(worldTime, partialTicks);
	}

	@Override
	public boolean canDoRainSnowIce(Chunk chunk) {
		RogueWorldSaveData data = RogueWorldSaveData.forWorld(worldObj);
		return data.temperature >= 0 ? super.canDoRainSnowIce(chunk) : false;
	}

	@Override
	public void updateWeather() {
		RogueWorldSaveData data = RogueWorldSaveData.forWorld(worldObj);
		if (data.temperature >= 0)
			super.updateWeather();
	}


	@Override
	@SideOnly(Side.CLIENT)
	public float[] calcSunriseSunsetColors(float par1, float par2) {
		float f2 = 0.4F;
		float f3 = MathHelper.cos(par1 * (float) Math.PI * 2.0F) - 0.0F;
		float f4 = -0.0F;
		float dust = MainRegistry.proxy.getImpactDust(worldObj);

		if (f3 >= f4 - f2 && f3 <= f4 + f2) {
			float f5 = (f3 - f4) / f2 * 0.5F + 0.5F;
			float f6 = 1.0F - (1.0F - MathHelper.sin(f5 * (float) Math.PI)) * 0.99F;
			f6 *= f6;
			return this.colorsSunriseSunset;
		} else {
			return null;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public float getStarBrightness(float par1) {
		float starBr = worldObj.getStarBrightnessBody(par1);
		float dust = MainRegistry.proxy.getImpactDust(worldObj);
		//float distance = 1-MainRegistry.proxy.getDistance(worldObj);
		float f1 = worldObj.getCelestialAngle(par1);
		float f2 = 1.0F - (MathHelper.cos(f1 * (float) Math.PI * 2.0F) * 2.0F + 0.25F);


		if (f2 > 1.0F) {
			f2 = 1.0F;
		}
		return f2 * (1 - dust) * 0.5f;
	}

	@Override
	public boolean isDaytime() {
		float dust = MainRegistry.proxy.getImpactDust(worldObj);

		if (dust >= 0.75F) {
			return false;
		} else {
			return super.isDaytime();
		}
	}

	/**
	 * Return Vec3D with biome specific fog color
	 */





	/*@Override
    public boolean canBlockFreeze(int x, int y, int z, boolean byWater)
    {
        BiomeGenBase biomegenbase = this.getBiomeGenForCoords(x, z);
        float f = biomegenbase.getFloatTemperature(x, y, z);
        TomSaveData data = TomSaveData.forWorld(worldObj);
        float t = 0;
        if(data.impact)
        {
        	t=1.15F;
        }
        else
        {
        	t=0.15F;
        }
        if (f > t)
        {
            return false;
        }
        else
        {
            if (y >= 0 && y < 256 && worldObj.getSavedLightValue(EnumSkyBlock.Block, x, y, z) < 10)
            {
                Block block = worldObj.getBlock(x, y, z);

                if ((block == Blocks.water || block == Blocks.flowing_water) && worldObj.getBlockMetadata(x, y, z) == 0)
                {
                    if (!byWater)
                    {
                        return true;
                    }

                    boolean flag1 = true;

                    if (flag1 && worldObj.getBlock(x - 1, y, z).getMaterial() != Material.water)
                    {
                        flag1 = false;
                    }

                    if (flag1 && worldObj.getBlock(x + 1, y, z).getMaterial() != Material.water)
                    {
                        flag1 = false;
                    }

                    if (flag1 && worldObj.getBlock(x, y, z - 1).getMaterial() != Material.water)
                    {
                        flag1 = false;
                    }

                    if (flag1 && worldObj.getBlock(x, y, z + 1).getMaterial() != Material.water)
                    {
                        flag1 = false;
                    }

                    if (!flag1)
                    {
                        return true;
                    }
                }
            }

            return false;
        }
    }*/
	/*@Override
    public boolean canSnowAt(int x, int y, int z, boolean checkLight) {
    {
        BiomeGenBase biomegenbase = this.getBiomeGenForCoords(x, z);
        float f = biomegenbase.getFloatTemperature(x, y, z);

        if (f > 1.15F)
        {
            return false;
        }
        else if (!checkLight)
        {
            return true;
        }
        else
        {
            if (y >= 0 && y < 256 && worldObj.getSavedLightValue(EnumSkyBlock.Block, x, y, z) < 10)
            {
                Block block = worldObj.getBlock(x, y, z);

                if (block.getMaterial() == Material.air && Blocks.snow_layer.canPlaceBlockAt(worldObj, x, y, z))
                {
                    return true;
                }
            }

            return false;
        }
    }*/
}