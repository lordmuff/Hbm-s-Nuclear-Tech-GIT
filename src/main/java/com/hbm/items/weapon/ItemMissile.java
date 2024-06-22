package com.hbm.items.weapon;

import java.util.List;

import com.hbm.items.ItemCustomLore;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

public class ItemMissile extends ItemCustomLore {
	
	public final MissileFormFactor formFactor;
	public final MissileTier tier;
	public final MissileFuel fuel;
	public int fuelCap;
	public boolean launchable = true;
	
	public ItemMissile(MissileFormFactor form, MissileTier tier) {
		this(form, tier, form.defaultFuel);
	}
	
	public ItemMissile(MissileFormFactor form, MissileTier tier, MissileFuel fuel) {
		this.formFactor = form;
		this.tier = tier;
		this.fuel = fuel;
		this.setFuelCap(this.fuel.defaultCap);
	}

	public enum PartSize {
		
		//for chips
		ANY,
		//for missile tips and thrusters
		NONE,
		//regular sizes, 1.0m, 1.5m and 2.0m
		SIZE_10,
		SIZE_15,
		SIZE_20
	}
	
	public enum WarheadType {
		
		HE,
		INC,
		BUSTER,
		CLUSTER,
		NUCLEAR,
		TX,
		N2,
		BALEFIRE,
		SCHRAB,
		TAINT,
		CLOUD,
		TURBINE
	}
	
	public enum FuelType {
		
		KEROSENE,
		SOLID,
		HYDROGEN,
		XENON,
		BALEFIRE, 
		HYDRAZINE
	}
	
	public enum Rarity {
		
		COMMON(EnumChatFormatting.GRAY + "Common"),
		UNCOMMON(EnumChatFormatting.YELLOW + "Uncommon"),
		RARE(EnumChatFormatting.AQUA + "Rare"),
		EPIC(EnumChatFormatting.LIGHT_PURPLE + "Epic"),
		LEGENDARY(EnumChatFormatting.DARK_GREEN + "Legendary"),
		SEWS_CLOTHES_AND_SUCKS_HORSE_COCK(EnumChatFormatting.DARK_AQUA + "Strange");
		
		String name;
		
		Rarity(String name) {
			this.name = name;
		}
	}
	
	public ItemMissile makeChip(float inaccuracy) {
		
		this.type = PartType.CHIP;
		this.top = PartSize.ANY;
		this.bottom = PartSize.ANY;
		this.attributes = new Object[] { inaccuracy };
		
		parts.put(this.hashCode(), this);
		

	public ItemMissile notLaunchable() {
		this.launchable = false;
		return this;
	}
	
	public ItemMissile setFuelCap(int fuelCap) {
		this.fuelCap = fuelCap;
		return this;
	}
	
	@Override
	public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean bool) {
		list.add(EnumChatFormatting.ITALIC + this.tier.display);

		try {
			switch(type) {
			case CHIP:
				list.add(EnumChatFormatting.BOLD + "Inaccuracy: " + EnumChatFormatting.GRAY + (Float)attributes[0] * 100 + "%");
				break;
			case WARHEAD:
				list.add(EnumChatFormatting.BOLD + "Size: " + EnumChatFormatting.GRAY + getSize(bottom));
				list.add(EnumChatFormatting.BOLD + "Type: " + EnumChatFormatting.GRAY + getWarhead((WarheadType)attributes[0]));
				list.add(EnumChatFormatting.BOLD + "Strength: " + EnumChatFormatting.GRAY + (Float)attributes[1]);
				list.add(EnumChatFormatting.BOLD + "Weight: " + EnumChatFormatting.GRAY + (Float)attributes[2] + "t");
				break;
			case FUSELAGE:
				list.add(EnumChatFormatting.BOLD + "Top size: " + EnumChatFormatting.GRAY + getSize(top));
				list.add(EnumChatFormatting.BOLD + "Bottom size: " + EnumChatFormatting.GRAY + getSize(bottom));
				list.add(EnumChatFormatting.BOLD + "Fuel type: " + EnumChatFormatting.GRAY + getFuel((FuelType)attributes[0]));
				list.add(EnumChatFormatting.BOLD + "Fuel amount: " + EnumChatFormatting.GRAY + (Float)attributes[1] + "l");
				break;
			case FINS:
				list.add(EnumChatFormatting.BOLD + "Size: " + EnumChatFormatting.GRAY + getSize(top));
				list.add(EnumChatFormatting.BOLD + "Inaccuracy: " + EnumChatFormatting.GRAY + (Float)attributes[0] * 100 + "%");
				break;
			case THRUSTER:
				list.add(EnumChatFormatting.BOLD + "Size: " + EnumChatFormatting.GRAY + getSize(top));
				list.add(EnumChatFormatting.BOLD + "Fuel type: " + EnumChatFormatting.GRAY + getFuel((FuelType)attributes[0]));
				list.add(EnumChatFormatting.BOLD + "Fuel consumption: " + EnumChatFormatting.GRAY + (Float)attributes[1] + "l/tick");
				list.add(EnumChatFormatting.BOLD + "Max. payload: " + EnumChatFormatting.GRAY + (Float)attributes[2] + "t");
				break;
			}
		} catch(Exception ex) {
			list.add("### I AM ERROR ###");
		}
		
		if(type != PartType.CHIP)
			list.add(EnumChatFormatting.BOLD + "Health: " + EnumChatFormatting.GRAY + health + "HP");
		
		if(this.rarity != null)
			list.add(EnumChatFormatting.BOLD + "Rarity: " + EnumChatFormatting.GRAY + this.rarity.name);
		if(author != null)
			list.add(EnumChatFormatting.WHITE + "   by " + author);
		if(witty != null)
			list.add(EnumChatFormatting.GOLD + "   " + EnumChatFormatting.ITALIC + "\"" + witty + "\"");
	}
	
	public String getSize(PartSize size) {
		
		switch(size) {
		case ANY:
			return "Any";
		case SIZE_10:
			return "1.0m";
		case SIZE_15:
			return "1.5m";
		case SIZE_20:
			return "2.0m";
		default:
			return "None";
		}
	}
	
	public String getWarhead(WarheadType type) {
		
		switch(type) {
		case HE:
			return EnumChatFormatting.YELLOW + "HE";
		case INC:
			return EnumChatFormatting.GOLD + "Incendiary";
		case CLUSTER:
			return EnumChatFormatting.GRAY + "Cluster";
		case BUSTER:
			return EnumChatFormatting.WHITE + "Bunker Buster";
		case NUCLEAR:
			return EnumChatFormatting.DARK_GREEN + "Nuclear";
		case TX:
			return EnumChatFormatting.DARK_PURPLE + "Thermonuclear (TX)";
		case N2:
			return EnumChatFormatting.RED + "N²";
		case BALEFIRE:
			return EnumChatFormatting.GREEN + "BF";
		case SCHRAB:
			return EnumChatFormatting.AQUA + "Schrabidium";
		case TAINT:
			return EnumChatFormatting.DARK_PURPLE + "Taint";
		case CLOUD:
			return EnumChatFormatting.LIGHT_PURPLE + "Cloud";
		case TURBINE:
			return (System.currentTimeMillis() % 1000 < 500 ? EnumChatFormatting.RED : EnumChatFormatting.LIGHT_PURPLE) + "Turbine";
		default:
			return EnumChatFormatting.BOLD + "N/A";
		}
	}
	
	public String getFuel(FuelType type) {
		
		switch(type) {
		case KEROSENE:
			return EnumChatFormatting.LIGHT_PURPLE + "Kerosene / Peroxide";
		case SOLID:
			return EnumChatFormatting.GOLD + "Solid Fuel";
		case HYDROGEN:
			return EnumChatFormatting.DARK_AQUA + "Hydrogen / Oxygen";
		case XENON:
			return EnumChatFormatting.DARK_PURPLE + "Xenon Gas";
		case BALEFIRE:
			return EnumChatFormatting.GREEN + "BF Rocket Fuel / Peroxide";
		case HYDRAZINE:
			return EnumChatFormatting.BLUE + "Hydrazine";
		default:
			return EnumChatFormatting.BOLD + "N/A";
		}
	}
	
	//am i retarded?
	/* yes */
	public ItemMissile copy() {
		
		ItemMissile part = new ItemMissile();
		part.type = this.type;
		part.top = this.top;
		part.bottom = this.bottom;
		part.health = this.health;
		part.attributes = this.attributes;
		part.health = this.health;
		part.setTextureName(this.iconString);
		
		return part;
	}
	
	public ItemMissile setAuthor(String author) {
		this.author = author;
		return this;
	}
	
	public ItemMissile setTitle(String title) {
		this.title = title;
		return this;
	}
	
	public ItemMissile setWittyText(String witty) {
		this.witty = witty;
		return this;
	}
	
	public ItemMissile setHealth(float health) {
		this.health = health;
		return this;
	}
	
	public ItemMissile setRarity(Rarity rarity) {
		this.rarity = rarity;
		
		if(this.type == PartType.FUSELAGE) {
			if(this.top == PartSize.SIZE_10)
				ItemLootCrate.list10.add(this);
			if(this.top == PartSize.SIZE_15)
				ItemLootCrate.list15.add(this);
		if(!this.launchable) {
			list.add(EnumChatFormatting.RED + "Not launchable!");
		} else {
			list.add("Fuel: " + this.fuel.display);
			if(this.fuelCap > 0) list.add("Fuel capacity: " + this.fuelCap + "mB");
			super.addInformation(itemstack, player, list, bool);
		}
	}
	
	public enum MissileFormFactor {
		ABM(MissileFuel.SOLID),
		MICRO(MissileFuel.SOLID),
		V2(MissileFuel.ETHANOL_PEROXIDE),
		STRONG(MissileFuel.KEROSENE_PEROXIDE),
		HUGE(MissileFuel.KEROSENE_LOXY),
		ATLAS(MissileFuel.JETFUEL_LOXY),
		OTHER(MissileFuel.KEROSENE_PEROXIDE);
		
		protected MissileFuel defaultFuel;
		
		private MissileFormFactor(MissileFuel defaultFuel) {
			this.defaultFuel = defaultFuel;
		}
	}
	
	public enum MissileTier {
		TIER0("Tier 0"),
		TIER1("Tier 1"),
		TIER2("Tier 2"),
		TIER3("Tier 3"),
		TIER4("Tier 4");
		
		public String display;
		
		private MissileTier(String display) {
			this.display = display;
		}
	}
	
	public enum MissileFuel {
		SOLID(EnumChatFormatting.GOLD + "Solid Fuel (pre-fueled)", 0),
		ETHANOL_PEROXIDE(EnumChatFormatting.AQUA + "Ethanol / Hydrogen Peroxide", 4_000),
		KEROSENE_PEROXIDE(EnumChatFormatting.BLUE + "Kerosene / Hydrogen Peroxide", 8_000),
		KEROSENE_LOXY(EnumChatFormatting.LIGHT_PURPLE + "Kerosene / Liquid Oxygen", 12_000),
		JETFUEL_LOXY(EnumChatFormatting.RED + "Jet Fuel / Liquid Oxygen", 16_000);
		
		public String display;
		public int defaultCap;
		
		private MissileFuel(String display, int defaultCap) {
			this.display = display;
			this.defaultCap = defaultCap;
		}
	}
}
