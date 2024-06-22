package com.hbm.tileentity.machine;

import java.util.ArrayList;
import java.util.List;

import com.hbm.dim.CelestialBody;
import com.hbm.dim.trait.CBT_Atmosphere;
import com.hbm.handler.atmosphere.ChunkAtmosphereManager;
import com.hbm.interfaces.IFluidAcceptor;
import com.hbm.interfaces.IFluidSource;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.tank.FluidTank;
import com.hbm.inventory.fluid.trait.FT_Gaseous;
import com.hbm.lib.Library;
import com.hbm.tileentity.TileEntityMachineBase;

import api.hbm.energymk2.IEnergyReceiverMK2;
import api.hbm.fluid.IFluidStandardSender;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityAtmoExtractor extends TileEntityMachineBase implements IFluidSource, IEnergyReceiverMK2, IFluidStandardSender {

	int consumption = 200;
	public float rot;
	public float prevRot;
	public long power = 0;
	public FluidTank tank;
	public List<IFluidAcceptor> list = new ArrayList();

	public TileEntityAtmoExtractor() {
		super(0);
		tank = new FluidTank(Fluids.AIR, 50000, 0);
	}

	@Override
	public String getName() {
		return "container.air";
	}

	@Override
	public void updateEntity() {

		if(!worldObj.isRemote) {

			this.updateConnections();

			// Extractors will not work indoors
			CBT_Atmosphere atmosphere = !ChunkAtmosphereManager.proxy.hasAtmosphere(worldObj, xCoord, yCoord, zCoord)
					? CelestialBody.getTrait(worldObj, CBT_Atmosphere.class)
					: null;

			if(atmosphere != null) {
				tank.setTankType(atmosphere.getMainFluid());
			} else {
				tank.setTankType(Fluids.NONE);
			}

			if(hasPower() && tank.getFill() + 100 <= tank.getMaxFill()) {
				tank.setFill(tank.getFill() + 100);
				power -= this.getMaxPower() / 100;

				FT_Gaseous.capture(worldObj, tank.getTankType(), 100);
			}

			markDirty();

			this.sendFluidToAll(tank, this);
			fillFluidInit(tank.getTankType());

			NBTTagCompound data = new NBTTagCompound();
			data.setLong("power", power);
			tank.writeToNBT(data, "water");

			this.networkPack(data, 50);
		}
	}





	protected void updateConnections() {

		for(ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
			this.trySubscribe(worldObj, xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ, dir);
	}

	public void networkUnpack(NBTTagCompound data) {
		this.power = data.getLong("power");
		tank.readFromNBT(data, "water");
	}

	public boolean hasPower() {
		return power >= this.getMaxPower() / 100;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		this.power = nbt.getLong("power");
		tank.readFromNBT(nbt, "water");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setLong("power", power);
		tank.writeToNBT(nbt, "water");
	}


	@Override
	public void fillFluidInit(FluidType type) {

		for(ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
			fillFluid(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ, getTact(), type);
	}

	@Override
	public void fillFluid(int x, int y, int z, boolean newTact, FluidType type) {
		Library.transmitFluid(x, y, z, newTact, this, worldObj, type);
	}

	@Override
	public boolean getTact() {
		return worldObj.getTotalWorldTime() % 20 < 10;
	}

	@Override
	public void setFluidFill(int i, FluidType type) {
		if(type == tank.getTankType())
			tank.setFill(i);
	}

	@Override
	public int getFluidFill(FluidType type) {
		if(type == tank.getTankType())
			return tank.getFill();

		return 0;
	}

	//@Override
	//public int getMaxFluidFill(FluidType type) {
	//	if(type == tanks.getTankType())
	//		return tanks.getMaxFill();

	//	return 0;
	//}

	@Override
	public void setFillForSync(int fill, int index) { }

	@Override
	public void setTypeForSync(FluidType type, int index) { }

	@Override
	public List<IFluidAcceptor> getFluidList(FluidType type) {
		return list;
	}

	@Override
	public void clearFluidList(FluidType type) {
		list.clear();
	}

	@Override
	public void setPower(long i) {
		power = i;
	}

	@Override
	public long getPower() {
		return power;
	}

	@Override
	public long getMaxPower() {
		return 10000;
	}

	@Override
	public FluidTank[] getSendingTanks() {
		return new FluidTank[] { tank };
	}

	//@Override
	//public FluidTank[] getReceivingTanks() {
	//	return new FluidTank[] { tanks };
	//}

	@Override
	public FluidTank[] getAllTanks() {
		return new FluidTank[] { tank };
	}
}