package com.hbm.dim.dres.biome;
import java.util.Random;

import com.hbm.blocks.ModBlocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class BiomeGenDresCanyon extends BiomeGenBase {
	
    public static final BiomeGenBase.Height height = new BiomeGenBase.Height(-1F, 0.02F);

    //TODO: avoid doing an extra planets and make each planet unique and cool.
	public BiomeGenDresCanyon(int id) {
		super(id);
		this.setBiomeName("Dres Large Basins");
		this.setDisableRain();
		
        this.spawnableCreatureList.clear();
        this.spawnableMonsterList.clear();
        this.spawnableWaterCreatureList.clear();
        this.spawnableCaveCreatureList.clear();
        
        this.theBiomeDecorator.generateLakes = false;
        
        this.setHeight(height);
        
        this.topBlock = ModBlocks.sellafield_slaked;
        this.fillerBlock = ModBlocks.sellafield_slaked;
	}

	@Override
    public void genTerrainBlocks(World world, Random rand, Block[] blocks, byte[] meta, int x, int z, double noise)
    {
        boolean flag = true;
        Block block = this.topBlock;
        byte b0 = (byte)(this.field_150604_aj & 255);
        Block block1 = this.fillerBlock;
        int k = -1;
        int l = (int)(noise / 6.0D + 6.0D + rand.nextDouble() * 0.85D);
        int i1 = x & 15;
        int j1 = z & 15;
        int k1 = blocks.length / 256;

        for (int l1 = 255; l1 >= 0; --l1)
        {
            int i2 = (j1 * 16 + i1) * k1 + l1;

            if (l1 <= 0 + rand.nextInt(5))
            {
            	blocks[i2] = Blocks.bedrock;
            }
            else
            {
                Block block2 = blocks[i2];

                if (block2 != null && block2.getMaterial() != Material.air)
                {
                    if (block2 == ModBlocks.dres_rock)
                    {
                        if (k == -1)
                        {
                            if (l <= 0)
                            {
                                block = null;
                                b0 = 0;
                                block1 = ModBlocks.sellafield_slaked;
                            }
                            else if (l1 >= 59 && l1 <= 64)
                            {
                                block = this.topBlock;
                                b0 = (byte)(this.field_150604_aj & 255);
                                block1 = this.fillerBlock;
                            }

                            if (l1 < 63 && (block == null || block.getMaterial() == Material.air))
                            {
                                if (this.getFloatTemperature(x, l1, z) < 0.15F)
                                {
                                    block = this.topBlock;
                                    b0 = 0;
                                }
                                else
                                {
                                    block = this.topBlock;
                                    b0 = 0;
                                }
                            }

                            k = l;

                            if (l1 >= 62)
                            {
                            	blocks[i2] = block;
                            	meta[i2] = b0;
                            }
                            else if (l1 < 68)
                            {
                                block = null;
                                block1 = ModBlocks.sellafield_slaked;
                                if (Math.random() > 0.4) {
                                	blocks[i2] = ModBlocks.sellafield_slaked;
                                	meta[i2] = 8;
                                }
                                else
                                {
                                    blocks[i2] = ModBlocks.sellafield_slaked;   	
                                    meta[i2] = 7;
                                }
                            }
                            else
                            {
                            	blocks[i2] = block1;
                            }
                        }
                        else if (k > 0)
                        {
                            --k;
                            blocks[i2] = block1;

                            if (k == 0 && block1 == Blocks.sand)
                            {
                                k = rand.nextInt(4) + Math.max(0, l1 - 63);
                                block1 = Blocks.sandstone;
                            }
                        }
                    }
                }
                else
                {
                    k = -1;
                }
            }
        }
    }

}