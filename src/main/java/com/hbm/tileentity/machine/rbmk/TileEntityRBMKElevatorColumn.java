package com.hbm.tileentity.machine.rbmk;

import com.hbm.handler.neutron.NeutronNodeWorld;
import com.hbm.handler.neutron.NeutronNodeWorld.StreamWorld;
import com.hbm.handler.neutron.NeutronStream;
import com.hbm.handler.neutron.RBMKNeutronHandler;
import com.hbm.handler.neutron.RBMKNeutronHandler.RBMKNeutronNode;
import com.hbm.handler.neutron.RBMKNeutronHandler.RBMKNeutronStream;
import com.hbm.handler.neutron.RBMKNeutronHandler.RBMKType;
import com.hbm.handler.radiation.ChunkRadiationManager;
import com.hbm.tileentity.machine.rbmk.TileEntityRBMKConsole.ColumnType;
import com.hbm.util.fauxpointtwelve.BlockPos;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3;

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

		if(worldObj.isRemote) return;

		this.transferHeat();

		if(this.flux <= 0) return;

		double quantity = this.flux;
		double ratio = this.fluxRatio;
		this.flux = 0;
		this.fluxRatio = 0;

		this.routeFlux(quantity, ratio);
	}

	protected void routeFlux(double quantity, double ratio) {

		int aboveY = yCoord + RBMKDials.getColumnHeight(worldObj) + 1;
		TileEntity above = worldObj.getTileEntity(xCoord, aboveY, zCoord);

		if(above instanceof TileEntityRBMKElevatorColumn) {
			((TileEntityRBMKElevatorColumn) above).addFlux(quantity, ratio);
			return;
		}

		if(above instanceof IRBMKFluxReceiver) {
			this.deliverFlux((IRBMKFluxReceiver) above, quantity, ratio);
			return;
		}

		Block block = worldObj.getBlock(xCoord, aboveY, zCoord);
		if(!block.isAir(worldObj, xCoord, aboveY, zCoord)) {
			this.irradiateArea(aboveY, quantity);
		}
	}

	protected void transferHeat() {

		double step = RBMKDials.getColumnHeatFlow(worldObj);
		int column = RBMKDials.getColumnHeight(worldObj) + 1;

		TileEntity above = worldObj.getTileEntity(xCoord, yCoord + column, zCoord);
		if(above instanceof TileEntityRBMKElevatorColumn) {
			this.exchangeHeat((TileEntityRBMKBase) above, step);
		}

		TileEntity below = worldObj.getTileEntity(xCoord, yCoord - column, zCoord);
		if(below instanceof TileEntityRBMKBase) {
			this.exchangeHeat((TileEntityRBMKBase) below, step);
		}
	}

	protected void exchangeHeat(TileEntityRBMKBase other, double step) {

		double average = (this.heat + other.heat) / 2D;
		this.heat += (average - this.heat) * step;
		other.heat += (average - other.heat) * step;
		this.markDirty();
	}

	protected void deliverFlux(IRBMKFluxReceiver receiver, double quantity, double ratio) {

		StreamWorld streamWorld = NeutronNodeWorld.getOrAddWorld(worldObj);
		BlockPos pos = new BlockPos(this);
		RBMKNeutronNode node = (RBMKNeutronNode) streamWorld.getNode(pos);

		if(node == null) {
			node = RBMKNeutronHandler.makeNode(streamWorld, this);
			streamWorld.addNode(node);
		}

		RBMKNeutronStream stream = new RBMKNeutronStream(node, Vec3.createVectorHelper(0, 1, 0));
		stream.fluxQuantity = quantity;
		stream.fluxRatio = ratio;
		receiver.receiveFlux(stream);
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
