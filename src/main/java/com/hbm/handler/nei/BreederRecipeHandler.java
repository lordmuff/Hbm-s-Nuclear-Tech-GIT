package com.hbm.handler.nei;

import java.awt.Rectangle;
import java.util.Arrays;
import java.util.List;
import java.util.HashMap;

import com.hbm.blocks.ModBlocks;
import com.hbm.handler.imc.ICompatNHNEI;
import com.hbm.inventory.RecipesCommon;
import com.hbm.inventory.gui.GUIMachineReactorBreeding;
import com.hbm.inventory.gui.GUIRBMKOutgasser;
import com.hbm.inventory.recipes.BreederRecipes;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import com.hbm.inventory.recipes.OutgasserRecipes;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;

public class BreederRecipeHandler extends NEIUniversalHandler implements ICompatNHNEI {
	public BreederRecipeHandler() {
		super("Breeding", ModBlocks.machine_reactor_breeding, BreederRecipes.getRecipes());
	}

	@Override
	public String getKey() {
		return "ntmOutgasser";
	}

	@Override
	public void loadTransferRects() {
		super.loadTransferRects();
		transferRectsGui.add(new RecipeTransferRect(new Rectangle(75, 26, 16, 32), "ntmOutgasser"));
		guiGui.add(GUIRBMKOutgasser.class);
		RecipeTransferRectHandler.registerRectsToGuis(guiGui, transferRectsGui);
	}
}
