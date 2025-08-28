package com.hbm.inventory.recipes;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import com.hbm.inventory.FluidStack;
import com.hbm.inventory.RecipesCommon.*;
import com.hbm.inventory.recipes.loader.SerializableRecipe;
import com.hbm.items.ModItems;
import com.hbm.items.machine.ItemBreedingRod.*;

import com.hbm.items.machine.ItemFluidIcon;
import com.hbm.util.Tuple;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import static com.hbm.inventory.OreDictManager.*;

public class BreederRecipes extends SerializableRecipe {

	public static HashMap<AStack, Tuple.Pair<ItemStack, Integer>> recipes = new HashMap();

	@Override
	public void registerDefaults() {
		setRecipe(BreedingRodType.LITHIUM, BreedingRodType.TRITIUM, 200);
		setRecipe(BreedingRodType.CO, BreedingRodType.CO60, 100);
		setRecipe(BreedingRodType.RA226, BreedingRodType.AC227, 300);
		setRecipe(BreedingRodType.TH232, BreedingRodType.THF, 500);
		setRecipe(BreedingRodType.U235, BreedingRodType.NP237, 300);
		setRecipe(BreedingRodType.NP237, BreedingRodType.PU238, 200);
		setRecipe(BreedingRodType.PU238, BreedingRodType.PU239, 1000);
		setRecipe(BreedingRodType.U238, BreedingRodType.RGP, 300);
		setRecipe(BreedingRodType.URANIUM, BreedingRodType.RGP, 200);
		setRecipe(BreedingRodType.RGP, BreedingRodType.WASTE, 200);

		/* thorium to thorium fuel */
		recipes.put((new OreDictStack(TH232.ingot())),	new Tuple.Pair<>(new ItemStack(ModItems.ingot_thorium_fuel), 750));
		recipes.put((new OreDictStack(TH232.nugget())),	new Tuple.Pair<>(new ItemStack(ModItems.nugget_thorium_fuel), 125));
		recipes.put((new OreDictStack(TH232.billet())),	new Tuple.Pair<>(new ItemStack(ModItems.billet_thorium_fuel), 500));

		/* cobalt to cobalt-60 */
		recipes.put((new OreDictStack(CO.ingot())),		new Tuple.Pair<>(new ItemStack(ModItems.ingot_co60), 100));
		recipes.put((new OreDictStack(CO.nugget())),	new Tuple.Pair<>(new ItemStack(ModItems.nugget_co60), 10));
		recipes.put((new OreDictStack(CO.dust())),		new Tuple.Pair<>(new ItemStack(ModItems.powder_co60), 100));

		/* gold to gold-198 */
		recipes.put((new OreDictStack(GOLD.ingot())),     new Tuple.Pair<>(new ItemStack(ModItems.ingot_au198), 4000));
		recipes.put((new OreDictStack(GOLD.nugget())),	  new Tuple.Pair<>(new ItemStack(ModItems.nugget_au198), 2000));
		recipes.put((new OreDictStack(GOLD.dust())),	  new Tuple.Pair<>(new ItemStack(ModItems.powder_au198), 4000));

		/* lead to lead-209 */
		recipes.put((new OreDictStack(PB.ingot())),     new Tuple.Pair<>(new ItemStack(ModItems.ingot_pb209), 9000));
		recipes.put((new OreDictStack(PB.nugget())),	new Tuple.Pair<>(new ItemStack(ModItems.nugget_pb209), 2500));
		recipes.put((new OreDictStack(PB.billet())),	new Tuple.Pair<>(new ItemStack(ModItems.billet_pb209), 3750));

		/* bismuth to polonium */
		recipes.put((new OreDictStack(BI.ingot())),		    new Tuple.Pair<>(new ItemStack(ModItems.ingot_polonium), 1200));
		recipes.put((new OreDictStack(BI.nugget())),	    new Tuple.Pair<>(new ItemStack(ModItems.nugget_polonium), 120));
		recipes.put((new OreDictStack(BI.dust())),		    new Tuple.Pair<>(new ItemStack(ModItems.powder_polonium), 1200));

		recipes.put(new ComparableStack(ModItems.meteorite_sword_etched), new Tuple.Pair<>(new ItemStack(ModItems.meteorite_sword_bred), 1000));
	}

	/** Sets recipes for single, dual, and quad rods **/
	public static void setRecipe(BreedingRodType inputType, BreedingRodType outputType, int flux) {
		recipes.put(new ComparableStack(new ItemStack(ModItems.rod, 1, inputType.ordinal())),      new Tuple.Pair<>(new ItemStack(ModItems.rod, 1, outputType.ordinal()), flux));
		recipes.put(new ComparableStack(new ItemStack(ModItems.rod_dual, 1, inputType.ordinal())), new Tuple.Pair<>(new ItemStack(ModItems.rod_dual, 1, outputType.ordinal()), flux * 2));
		recipes.put(new ComparableStack(new ItemStack(ModItems.rod_quad, 1, inputType.ordinal())), new Tuple.Pair<>(new ItemStack(ModItems.rod_quad, 1, outputType.ordinal()), flux * 3));
	}

	public static HashMap getRecipes() {

		HashMap<Object, Object[]> recipes = new HashMap<Object, Object[]>();

		for(Entry<AStack, Tuple.Pair<ItemStack, Integer>> entry : BreederRecipes.recipes.entrySet()) {

			AStack input = entry.getKey();
			ItemStack output = entry.getValue().getKey();
			Integer flux = entry.getValue().getValue();

			if(output != null && flux != null) recipes.put(input, new Object[] {output, flux});

		}

		return recipes;
	}

	public static Tuple.Pair<ItemStack, Integer> getOutput(ItemStack input) {

		ComparableStack comp = new ComparableStack(input).makeSingular();

		if(recipes.containsKey(comp)) {
			return recipes.get(comp);
		}

		String[] dictKeys = comp.getDictKeys();

		for(String key : dictKeys) {
			OreDictStack dict = new OreDictStack(key);
			if(recipes.containsKey(dict)) {
				return recipes.get(dict);
			}
		}

		return null;
	}


	@Override
	public String getFileName() {
		return "hbmBreeder.json";
	}

	@Override
	public Object getRecipeObject() {
		return recipes;
	}

	@Override
	public void readRecipe(JsonElement recipe) {
		JsonObject obj = (JsonObject) recipe;

		AStack input = readAStack(obj.get("input").getAsJsonArray());
		ItemStack output = readItemStack(obj.get("output").getAsJsonArray());
		Integer flux = obj.get("flux").getAsInt();

		if(output != null || flux != null) {
			recipes.put(input, new Tuple.Pair<>(output, flux));
		}
	}

	@Override
	public void writeRecipe(Object recipe, JsonWriter writer) throws IOException {
		Entry<AStack, Tuple.Pair<ItemStack, Integer>> rec = (Entry<AStack, Tuple.Pair<ItemStack, Integer>>) recipe;



		if(rec.getValue() != null) {
			writer.name("input");
			writeAStack(rec.getKey(), writer);
		}

		if(rec.getValue().getKey() != null) {
			writer.name("output");
			writeItemStack(rec.getValue().getKey(), writer);
		}

		if(rec.getValue().getValue() != null) {
			writer.name("flux");
			writeInt((rec.getValue().getValue()), writer);
		}

	}

	@Override
	public void deleteRecipes() {
		recipes.clear();
	}
}
