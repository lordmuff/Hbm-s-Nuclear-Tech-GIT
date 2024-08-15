package com.hbm.tileentity.bomb;

import java.util.List;

import com.hbm.entity.missile.EntityMissileCustom;
import com.hbm.handler.CompatHandler;
import com.hbm.handler.MissileStruct;
import com.hbm.interfaces.IFluidAcceptor;
import com.hbm.interfaces.IFluidContainer;
import com.hbm.inventory.container.ContainerLaunchTable;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.tank.FluidTank;
import com.hbm.inventory.gui.GUIMachineLaunchTable;
import com.hbm.items.ItemVOTVdrive;
import com.hbm.items.ModItems;
import com.hbm.items.weapon.ItemCustomMissile;
import com.hbm.items.weapon.ItemCustomMissilePart;
import com.hbm.items.weapon.ItemCustomMissilePart.FuelType;
import com.hbm.items.weapon.ItemCustomMissilePart.PartSize;
import com.hbm.lib.Library;
import com.hbm.main.MainRegistry;
import com.hbm.packet.BufPacket;
import com.hbm.packet.PacketDispatcher;
import com.hbm.tileentity.IBufPacketReceiver;
import com.hbm.tileentity.IGUIProvider;
import com.hbm.tileentity.IRadarCommandReceiver;
import com.hbm.tileentity.TileEntityLoadedBase;

import api.hbm.energymk2.IEnergyReceiverMK2;
import api.hbm.fluid.IFluidStandardReceiver;
import api.hbm.item.IDesignatorItem;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.SimpleComponent;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

@Optional.InterfaceList({@Optional.Interface(iface = "li.cil.oc.api.network.SimpleComponent", modid = "OpenComputers")})
public class TileEntityLaunchTable extends TileEntityLoadedBase implements ISidedInventory, IEnergyReceiverMK2, IFluidContainer, IFluidAcceptor, IFluidStandardReceiver, IGUIProvider, SimpleComponent, IRadarCommandReceiver, IBufPacketReceiver, CompatHandler.OCComponent {

	public ItemStack slots[];
	public ItemStack syncStack;

	public long power;
	public static final long maxPower = 100000;
	public int solid;
	public static final int maxSolid = 100000;
	public FluidTank[] tanks;
	public PartSize padSize;
	public int height;
	
	public MissileStruct load;

	private static final int[] access = new int[] { 0 };

	private String customName;

	public TileEntityLaunchTable() {
		slots = new ItemStack[8];
		tanks = new FluidTank[2];
		tanks[0] = new FluidTank(Fluids.NONE, 100000, 0);
		tanks[1] = new FluidTank(Fluids.NONE, 100000, 1);
		padSize = PartSize.SIZE_10;
		height = 10;
	}

	@Override
	public int getSizeInventory() {
		return slots.length;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return slots[i];
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		if (slots[i] != null) {
			ItemStack itemStack = slots[i];
			slots[i] = null;
			return itemStack;
		} else {
			return null;
		}
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemStack) {
		slots[i] = itemStack;
		if (itemStack != null && itemStack.stackSize > getInventoryStackLimit()) {
			itemStack.stackSize = getInventoryStackLimit();
		}
	}

	@Override
	public String getInventoryName() {
		return this.hasCustomInventoryName() ? this.customName : "container.launchTable";
	}

	@Override
	public boolean hasCustomInventoryName() {
		return this.customName != null && this.customName.length() > 0;
	}

	public void setCustomName(String name) {
		this.customName = name;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		if (worldObj.getTileEntity(xCoord, yCoord, zCoord) != this) {
			return false;
		} else {
			return player.getDistanceSq(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D) <= 64;
		}
	}

	// You scrubs aren't needed for anything (right now)
	@Override
	public void openInventory() {
	}

	@Override
	public void closeInventory() {
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack stack) {
		return false;
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		if (slots[i] != null) {
			if (slots[i].stackSize <= j) {
				ItemStack itemStack = slots[i];
				slots[i] = null;
				return itemStack;
			}
			ItemStack itemStack1 = slots[i].splitStack(j);
			if (slots[i].stackSize == 0) {
				slots[i] = null;
			}

			return itemStack1;
		} else {
			return null;
		}
	}
	
	public long getPowerScaled(long i) {
		return (power * i) / maxPower;
	}
	
	public int getSolidScaled(int i) {
		return (solid * i) / maxSolid;
	}

	@Override
	public void updateEntity() {

		if (!worldObj.isRemote) {
			
			updateTypes();
			if(worldObj.getTotalWorldTime() % 20 == 0)
				this.updateConnections();

			tanks[0].loadTank(2, 6, slots);
			tanks[1].loadTank(3, 7, slots);

			for (int i = 0; i < 2; i++)
				tanks[i].updateTank(xCoord, yCoord, zCoord, worldObj.provider.dimensionId);
			
			power = Library.chargeTEFromItems(slots, 5, power, maxPower);
			
			if(slots[4] != null && slots[4].getItem() == ModItems.rocket_fuel && solid + 250 <= maxSolid) {
				this.decrStackSize(4, 1);
				solid += 250;
			}

			PacketDispatcher.wrapper.sendToAllAround(new BufPacket(xCoord, yCoord, zCoord, this), new TargetPoint(this.worldObj.provider.dimensionId, xCoord, yCoord, zCoord, 250));

			outer:
			for(int x = -4; x <= 4; x++) {
				for(int z = -4; z <= 4; z++) {
					
					if(worldObj.isBlockIndirectlyGettingPowered(xCoord + x, yCoord, zCoord + z) && canLaunch()) {
						launchFromDesignator();
						break outer;
					}
				}
			}
		} else {

			List<EntityMissileCustom> entities = worldObj.getEntitiesWithinAABB(EntityMissileCustom.class, AxisAlignedBB.getBoundingBox(xCoord - 0.5, yCoord, zCoord - 0.5, xCoord + 1.5, yCoord + 10, zCoord + 1.5));
			
			if(!entities.isEmpty()) {
				for(int i = 0; i < 15; i++) {

					boolean dir = worldObj.rand.nextBoolean();
					float moX = (float) (dir ? 0 : worldObj.rand.nextGaussian() * 0.65F);
					float moZ = (float) (!dir ? 0 : worldObj.rand.nextGaussian() * 0.65F);
					MainRegistry.proxy.spawnParticle(xCoord + 0.5, yCoord + 0.25, zCoord + 0.5, "launchsmoke", new float[] {moX, 0, moZ});
				}
			}
		}
	}
	
	private void updateConnections() {

		for(int i = -4; i <= 4; i++) {
			this.trySubscribe(worldObj, xCoord + i, yCoord, zCoord + 5, Library.POS_Z);
			this.trySubscribe(worldObj, xCoord + i, yCoord, zCoord - 5, Library.NEG_Z);
			this.trySubscribe(worldObj, xCoord + 5, yCoord, zCoord + i, Library.POS_X);
			this.trySubscribe(worldObj, xCoord - 5, yCoord, zCoord + i, Library.NEG_X);
			
			for(int j = 0; j < 2; j++) {
				this.trySubscribe(tanks[j].getTankType(), worldObj, xCoord + i, yCoord, zCoord + 5, Library.POS_Z);
				this.trySubscribe(tanks[j].getTankType(), worldObj, xCoord + i, yCoord, zCoord - 5, Library.NEG_Z);
				this.trySubscribe(tanks[j].getTankType(), worldObj, xCoord + 5, yCoord, zCoord + i, Library.POS_X);
				this.trySubscribe(tanks[j].getTankType(), worldObj, xCoord - 5, yCoord, zCoord + i, Library.NEG_X);
			}
		}
	}
	
	public boolean canLaunch() {
		
		if(power >= maxPower * 0.75 && isMissileValid() && hasFuel())
			return true;
		
		return false;
	}

	@Override
	public boolean sendCommandEntity(Entity target) {
		return sendCommandPosition((int) Math.floor(target.posX), yCoord, (int) Math.floor(target.posX));
	}

	@Override
	public boolean sendCommandPosition(int x, int y, int z) {
		if(!canLaunch()) return false;
		this.launchTo(x, z);
		return true;
	}
	
	public void launchFromDesignator() {
		if(slots[1] != null && slots[1].getItem() instanceof IDesignatorItem) {
			IDesignatorItem designator = (IDesignatorItem) slots[1].getItem();
			
			if(designator.isReady(worldObj, slots[1], xCoord, yCoord, zCoord)) {
				Vec3 coords = designator.getCoords(worldObj, slots[1], xCoord, yCoord, zCoord);
				int tX = (int) Math.floor(coords.xCoord);
				int tZ = (int) Math.floor(coords.zCoord);
				
				this.launchTo(tX, tZ);
			}

		}
		if (slots[1] != null && slots[1].getItem() instanceof ItemVOTVdrive) {
			//null because fuck you
			int tX = slots[1].stackTagCompound.getInteger("xCoord");
			int tZ = slots[1].stackTagCompound.getInteger("zCoord");

			this.launchTo(tX, tZ);
		}
	}
	
	public void launchTo(int tX, int tZ) {
		
		ItemCustomMissilePart chip = (ItemCustomMissilePart) Item.getItemById(ItemCustomMissile.readFromNBT(slots[0], "chip"));
		float c = (Float)chip.attributes[0];
		float f = 1.0F;
		
		if(ItemCustomMissile.getStruct(slots[0]).fins != null) {
			ItemCustomMissilePart fins = (ItemCustomMissilePart) Item.getItemById(ItemCustomMissile.readFromNBT(slots[0], "stability"));
			f = (Float) fins.attributes[0];
		}
		
		Vec3 target = Vec3.createVectorHelper(xCoord - tX, 0, zCoord - tZ);
		target.xCoord *= c * f;
		target.zCoord *= c * f;
		
		target.rotateAroundY(worldObj.rand.nextFloat() * 360);

		EntityMissileCustom missile = new EntityMissileCustom(worldObj, xCoord + 0.5F, yCoord + 2.5F, zCoord + 0.5F, tX + (int)target.xCoord, tZ + (int)target.zCoord, ItemCustomMissile.getStruct(slots[0]));
		worldObj.playSoundEffect(xCoord, yCoord, zCoord, "hbm:weapon.missileTakeOff", 10.0F, 1.0F);
		worldObj.spawnEntityInWorld(missile);

		subtractFuel();
		
		slots[0] = null;
	}
	
	private boolean hasFuel() {

		return solidState() != 0 && liquidState() != 0 && oxidizerState() != 0;
	}
	
	private void subtractFuel() {
		
		MissileStruct multipart = ItemCustomMissile.getStruct(slots[0]);
		
		if(multipart == null || multipart.fuselage == null)
			return;
		
		ItemCustomMissilePart fuselage = (ItemCustomMissilePart)multipart.fuselage;
		
		int fuel = fuselage.getTankSize();
		
		switch((FuelType)fuselage.attributes[0]) {
			case KEROSENE:
				tanks[0].setFill(tanks[0].getFill() - fuel);
				tanks[1].setFill(tanks[1].getFill() - fuel);
				break;
			case HYDROGEN:
				tanks[0].setFill(tanks[0].getFill() - fuel);
				tanks[1].setFill(tanks[1].getFill() - fuel);
				break;
			case XENON:
				tanks[0].setFill(tanks[0].getFill() - fuel);
				break;
			case BALEFIRE:
				tanks[0].setFill(tanks[0].getFill() - fuel);
				tanks[1].setFill(tanks[1].getFill() - fuel);
				break;
			case SOLID:
				this.solid -= fuel; break;
			default: break;
		}
		
		this.power -= maxPower * 0.75;
	}

	public boolean isMissileValid() {
		MissileStruct missile = ItemCustomMissile.getStruct(slots[0]);

		if(missile == null || missile.fuselage == null)
			return false;
		
		ItemCustomMissilePart fuselage = (ItemCustomMissilePart)missile.fuselage;
		
		return fuselage.top == padSize;
	}
	
	public boolean hasDesignator() {
		return slots[1] != null && slots[1].getItem() instanceof IDesignatorItem && ((IDesignatorItem)slots[1].getItem()).isReady(worldObj, slots[1], xCoord, yCoord, zCoord);
	}
	
	public int solidState() {
		
		MissileStruct multipart = ItemCustomMissile.getStruct(slots[0]);
		
		if(multipart == null || multipart.fuselage == null)
			return -1;
		
		ItemCustomMissilePart fuselage = (ItemCustomMissilePart)multipart.fuselage;
		
		if((FuelType)fuselage.attributes[0] == FuelType.SOLID) {
			
			if(solid >= fuselage.getTankSize())
				return 1;
			else
				return 0;
		}
		
		return -1;
	}

	public int liquidState() {
		
		MissileStruct multipart = ItemCustomMissile.getStruct(slots[0]);
		
		if(multipart == null || multipart.fuselage == null)
			return -1;
		
		ItemCustomMissilePart fuselage = (ItemCustomMissilePart)multipart.fuselage;
		
		switch((FuelType)fuselage.attributes[0]) {
			case KEROSENE:
			case HYDROGEN:
			case XENON:
			case BALEFIRE:
				
				if(tanks[0].getFill() >= fuselage.getTankSize())
					return 1;
				else
					return 0;
			default: break;
		}
		
		return -1;
	}
	
	public int oxidizerState() {
		
		MissileStruct multipart = ItemCustomMissile.getStruct(slots[0]);
		
		if(multipart == null || multipart.fuselage == null)
			return -1;
		
		ItemCustomMissilePart fuselage = (ItemCustomMissilePart)multipart.fuselage;
		
		switch((FuelType)fuselage.attributes[0]) {
			case KEROSENE:
			case HYDROGEN:
			case BALEFIRE:
				
				if(tanks[1].getFill() >= fuselage.getTankSize())
					return 1;
				else
					return 0;
			default: break;
		}
		
		return -1;
	}
	
	public void updateTypes() {
		
		MissileStruct multipart = ItemCustomMissile.getStruct(slots[0]);
		
		if(multipart == null || multipart.fuselage == null)
			return;
		
		ItemCustomMissilePart fuselage = (ItemCustomMissilePart)multipart.fuselage;
		
		switch((FuelType)fuselage.attributes[0]) {
			case KEROSENE:
				tanks[0].setTankType(Fluids.KEROSENE);
				tanks[1].setTankType(Fluids.PEROXIDE);
				break;
			case HYDROGEN:
				tanks[0].setTankType(Fluids.HYDROGEN);
				tanks[1].setTankType(Fluids.OXYGEN);
				break;
			case XENON:
				tanks[0].setTankType(Fluids.XENON);
				break;
			case BALEFIRE:
				tanks[0].setTankType(Fluids.BALEFIRE);
				tanks[1].setTankType(Fluids.PEROXIDE);
				break;
			default: break;
		}
	}

	@Override
	public void serialize(ByteBuf buf) {
		buf.writeInt(solid);
		buf.writeLong(power);
		buf.writeInt(padSize.ordinal());

		if(slots[0] != null && slots[0].getItem() instanceof ItemCustomMissile) {
			buf.writeBoolean(true);
			MissileStruct missile = ItemCustomMissile.getStruct(slots[0]);
			missile.writeToByteBuffer(buf);
		} else {
			buf.writeBoolean(false);
		}

		for(int i = 0; i < tanks.length; i++) tanks[i].serialize(buf);
	}

	@Override
	public void deserialize(ByteBuf buf) {
		solid = buf.readInt();
		power = buf.readLong();
		padSize = PartSize.values()[buf.readInt()];

		if(buf.readBoolean()) {
			load = MissileStruct.readFromByteBuffer(buf);
		} else {
			load = null;
		}

		for(int i = 0; i < tanks.length; i++) tanks[i].deserialize(buf);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		NBTTagList list = nbt.getTagList("items", 10);

		tanks[0].readFromNBT(nbt, "fuel");
		tanks[1].readFromNBT(nbt, "oxidizer");
		solid = nbt.getInteger("solidfuel");
		power = nbt.getLong("power");
		padSize = PartSize.values()[nbt.getInteger("padSize")];

		slots = new ItemStack[getSizeInventory()];

		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound nbt1 = list.getCompoundTagAt(i);
			byte b0 = nbt1.getByte("slot");
			if (b0 >= 0 && b0 < slots.length) {
				slots[b0] = ItemStack.loadItemStackFromNBT(nbt1);
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		
		NBTTagList list = new NBTTagList();

		tanks[0].writeToNBT(nbt, "fuel");
		tanks[1].writeToNBT(nbt, "oxidizer");
		nbt.setInteger("solidfuel", solid);
		nbt.setLong("power", power);
		nbt.setInteger("padSize", padSize.ordinal());

		for (int i = 0; i < slots.length; i++) {
			if (slots[i] != null) {
				NBTTagCompound nbt1 = new NBTTagCompound();
				nbt1.setByte("slot", (byte) i);
				slots[i].writeToNBT(nbt1);
				list.appendTag(nbt1);
			}
		}
		nbt.setTag("items", list);
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int p_94128_1_) {
		return access;
	}

	@Override
	public boolean canInsertItem(int i, ItemStack itemStack, int j) {
		return this.isItemValidForSlot(i, itemStack);
	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemStack, int j) {
		return false;
	}

	@Override
	public int getMaxFluidFill(FluidType type) {
		if (type.name().equals(tanks[0].getTankType().name()))
			return tanks[0].getMaxFill();
		else if (type.name().equals(tanks[1].getTankType().name()))
			return tanks[1].getMaxFill();
		else
			return 0;
	}

	@Override
	public void setFillForSync(int fill, int index) {
		if (index < 2 && tanks[index] != null)
			tanks[index].setFill(fill);
	}

	@Override
	public void setFluidFill(int fill, FluidType type) {
		if (type.name().equals(tanks[0].getTankType().name()))
			tanks[0].setFill(fill);
		else if (type.name().equals(tanks[1].getTankType().name()))
			tanks[1].setFill(fill);
	}

	@Override
	public void setTypeForSync(FluidType type, int index) {
		if (index < 2 && tanks[index] != null)
			tanks[index].setTankType(type);
	}

	@Override
	public int getFluidFill(FluidType type) {
		if (type.name().equals(tanks[0].getTankType().name()))
			return tanks[0].getFill();
		else if (type.name().equals(tanks[1].getTankType().name()))
			return tanks[1].getFill();
		else
			return 0;
	}
	
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return TileEntity.INFINITE_EXTENT_AABB;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public double getMaxRenderDistanceSquared()
	{
		return 65536.0D;
	}

	@Override
	public void setPower(long i) {
		this.power = i;
	}

	@Override
	public long getPower() {
		return this.power;
	}

	@Override
	public long getMaxPower() {
		return this.maxPower;
	}
	
	@Override
	public long transferPower(long power) {
		
		this.power += power;
		
		if(this.power > this.getMaxPower()) {
			
			long overshoot = this.power - this.getMaxPower();
			this.power = this.getMaxPower();
			return overshoot;
		}
		
		return 0;
	}

	@Override
	public boolean canConnect(ForgeDirection dir) {
		return dir != ForgeDirection.UP && dir != ForgeDirection.DOWN && dir != ForgeDirection.UNKNOWN;
	}

	@Override
	public boolean canConnect(FluidType type, ForgeDirection dir) {
		return dir != ForgeDirection.UP && dir != ForgeDirection.DOWN && dir != ForgeDirection.UNKNOWN;
	}

	@Override
	public FluidTank[] getAllTanks() {
		return tanks;
	}

	@Override
	public FluidTank[] getReceivingTanks() {
		return tanks;
	}

	// do some opencomputer stuff
	@Override
	@Optional.Method(modid = "OpenComputers")
	public String getComponentName() {
		return "ntm_custom_launch_pad";
	}

	@Callback
	@Optional.Method(modid = "OpenComputers")
	public Object[] getEnergyInfo(Context context, Arguments args) {
		return new Object[] {getPower(), getMaxPower()};
	}

	@Callback
	@Optional.Method(modid = "OpenComputers")
	public Object[] getContents(Context context, Arguments args) {
		return new Object[] {
				tanks[0].getFill(), tanks[0].getMaxFill(), tanks[0].getTankType().getUnlocalizedName(),
				tanks[1].getFill(), tanks[1].getMaxFill(), tanks[1].getTankType().getUnlocalizedName(),
				solid, maxSolid
		};
	}

	@Callback
	@Optional.Method(modid = "OpenComputers")
	public Object[] getLaunchInfo(Context context, Arguments args) {
		return new Object[] {canLaunch(), isMissileValid(), hasDesignator(), hasFuel()};
	}

	@Callback
	@Optional.Method(modid = "OpenComputers")
	public Object[] getCoords(Context context, Arguments args) {
		if (slots[1] != null && slots[1].getItem() instanceof IDesignatorItem) {
			int xCoord2;
			int zCoord2;
			if (slots[1].stackTagCompound != null) {
				xCoord2 = slots[1].stackTagCompound.getInteger("xCoord");
				zCoord2 = slots[1].stackTagCompound.getInteger("zCoord");
			} else
				return new Object[] {false};

			return new Object[] {xCoord2, zCoord2};
		}
		return new Object[] {false, "Designator not found"};
	}
	@Callback
	@Optional.Method(modid = "OpenComputers")
	public Object[] setCoords(Context context, Arguments args) {
		if (slots[1] != null && slots[1].getItem() instanceof IDesignatorItem) {
			slots[1].stackTagCompound = new NBTTagCompound();
			slots[1].stackTagCompound.setInteger("xCoord", args.checkInteger(0));
			slots[1].stackTagCompound.setInteger("zCoord", args.checkInteger(1));

			return new Object[] {true};
		}
		return new Object[] {false, "Designator not found"};
	}

	@Callback
	@Optional.Method(modid = "OpenComputers")
	public Object[] launch(Context context, Arguments args) {
		if(this.canLaunch()) {
			this.launchFromDesignator();
			return new Object[] {true};
		}
		return new Object[] {false};
	}

	@Override
	@Optional.Method(modid = "OpenComputers")
	public String[] methods() {
		return new String[] {
				"getEnergyInfo",
				"getContents",
				"getLaunchInfo",
				"getCoords",
				"setCoords",
				"launch"
		};
	}

	@Override
	@Optional.Method(modid = "OpenComputers")
	public Object[] invoke(String method, Context context, Arguments args) throws Exception {
		switch(method) {
			case ("getEnergyInfo"):
				return getEnergyInfo(context, args);
			case ("getContents"):
				return getContents(context, args);
			case ("getLaunchInfo"):
				return getLaunchInfo(context, args);
			case ("getCoords"):
				return getCoords(context, args);
			case ("setCoords"):
				return setCoords(context, args);
			case ("launch"):
				return launch(context, args);
	}
	throw new NoSuchMethodException();
}

	@Override
	public Container provideContainer(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return new ContainerLaunchTable(player.inventory, this);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiScreen provideGUI(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return new GUIMachineLaunchTable(player.inventory, this);
	}

}
