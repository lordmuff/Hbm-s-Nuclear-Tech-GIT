package com.hbm.tileentity.machine.rbmk;

import com.hbm.handler.neutron.NeutronStream;
import com.hbm.handler.neutron.RBMKNeutronHandler.RBMKType;
import com.hbm.handler.radiation.ChunkRadiationManager;
import com.hbm.tileentity.machine.rbmk.TileEntityRBMKConsole.ColumnType;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;

public class TileEntityRBMKElevatorColumn extends TileEntityRBMKBase implements IRBMKFluxReceiver {

	public double flux;
	public double fluxRatio;

	@Override
	public void receiveFlux(NeutronStream stream) {
		this.addFlux(stream.fluxQuantity, stream.fluxRatio);
	}

	public void addFlux(double quantity, double ratio) {
		double fast = this.flux * this.fluxRatio;
		double fastIn = quantity * ratio;

		this.flux += quantity;
		this.fluxRatio = this.flux > 0 ? (fast + fastIn) / this.flux : 0;
	}

	@Override
	public void updateEntity() {
		super.updateEntity();

		if(worldObj.isRemote || this.flux <= 0) return;

		double quantity = this.flux;
		double ratio = this.fluxRatio;
		this.flux = 0;
		this.fluxRatio = 0;

		this.routeFlux(quantity, ratio);
	}

	protected void routeFlux(double quantity, double ratio) {

		int start = yCoord + RBMKDials.getColumnHeight(worldObj) + 1;

		for(int y = start; y < worldObj.getHeight(); y++) {

			Block block = worldObj.getBlock(xCoord, y, zCoord);
			if(block.isAir(worldObj, xCoord, y, zCoord)) continue;

			TileEntity te = worldObj.getTileEntity(xCoord, y, zCoord);
			if(te instanceof TileEntityRBMKElevatorColumn) {
				((TileEntityRBMKElevatorColumn) te).addFlux(quantity, ratio);
				return;
			}

			this.irradiateArea(y, quantity);
			return;
		}
	}

	protected void irradiateArea(int y, double quantity) {

		float rad = (float) (quantity * 0.05F);
		int r = 1;

		for(int dx = -r; dx <= r; dx++) {
			for(int dy = -r; dy <= r; dy++) {
				for(int dz = -r; dz <= r; dz++) {
					ChunkRadiationManager.proxy.incrementRad(worldObj, xCoord + dx, y + dy, zCoord + dz, rad);
				}
			}
		}
	}

	@Override
	public RBMKType getRBMKType() {
		return RBMKType.OTHER;
	}

	@Override
	public ColumnType getConsoleType() {
		return ColumnType.BLANK;
	}
}
