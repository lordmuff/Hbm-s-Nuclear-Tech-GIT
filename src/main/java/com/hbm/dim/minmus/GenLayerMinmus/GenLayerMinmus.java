package com.hbm.dim.minmus.GenLayerMinmus;

import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.GenLayerFuzzyZoom;
import net.minecraft.world.gen.layer.GenLayerRiver;
import net.minecraft.world.gen.layer.GenLayerRiverInit;
import net.minecraft.world.gen.layer.GenLayerSmooth;
import net.minecraft.world.gen.layer.GenLayerVoronoiZoom;
import net.minecraft.world.gen.layer.GenLayerZoom;

public abstract class GenLayerMinmus extends GenLayer
{
    public GenLayerMinmus(long l)
    {
        super(l);
    }

    public static GenLayer[] makeTheWorld(long l)
    {
    	GenLayer biomes = new GenLayerMinmusBiomes(l);
    	 biomes = new GenLayerFuzzyZoom(2000L, biomes);
         biomes = new GenLayerZoom(2001L, biomes);
         biomes = new GenLayerDiversifyMinmus(1000L, biomes);
         biomes = new GenLayerZoom(1000L, biomes);
         biomes = new GenLayerDiversifyMinmus(1001L, biomes);
         biomes = new GenLayerZoom(1001L, biomes);
         biomes = new GenLayerMinmusBasins(3000L, biomes);
         biomes = new GenLayerZoom(1003L, biomes);
         biomes = new GenLayerSmooth(700L, biomes);
         biomes = new GenLayerMinmusPlains(200L, biomes);
         biomes = new GenLayerZoom(1006L, biomes);
         
         GenLayer genLayerVeronoiZoom = new GenLayerVoronoiZoom(10L, biomes);
 
        biomes.initWorldGenSeed(l);
        genLayerVeronoiZoom.initWorldGenSeed(l);

        return new GenLayer[] { biomes, genLayerVeronoiZoom };
    }
}
