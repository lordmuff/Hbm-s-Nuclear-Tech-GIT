package com.hbm.dim.minmus.GenLayerMinmus;

import com.hbm.dim.minmus.biome.BiomeGenBaseMinmus;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

public class GenLayerDiversifyMinmus extends GenLayer {

    private static final BiomeGenBase[] biomes = new BiomeGenBase[] { BiomeGenBaseMinmus.minmusCanyon, BiomeGenBaseMinmus.minmusPlains };

    public GenLayerDiversifyMinmus(long l, GenLayer parent) {
        super(l);
        this.parent = parent;
    }

    @Override
    public int[] getInts(int x, int z, int width, int depth) {
        return diversify(x, z, width, depth);
    }

    private int[] diversify(int x, int z, int width, int height) {
        int input[] = this.parent.getInts(x, z, width, height);
        int output[] = IntCache.getIntCache(width * height);
        // EnumBiomeTypeDevonian type;
        for(int zOut = 0; zOut < height; zOut++) {
            for(int xOut = 0; xOut < width; xOut++) {
                int i = xOut + zOut * width;
                int center = input[i];
                initChunkSeed(xOut + x, zOut + z);
                if(nextInt(2) == 0) {
                    output[i] = biomes[nextInt(biomes.length)].biomeID;
                } else
                    output[i] = center;
            }
        }
        return output;
    }
}
