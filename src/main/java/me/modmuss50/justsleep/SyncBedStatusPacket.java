package me.modmuss50.justsleep;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import reborncore.common.network.ExtendedPacketBuffer;
import reborncore.common.network.INetworkPacket;

import java.io.IOException;

public class SyncBedStatusPacket implements INetworkPacket<SyncBedStatusPacket> {

	BlockPos pos;

	public SyncBedStatusPacket(BlockPos pos) {
		this.pos = pos;
	}

	public SyncBedStatusPacket() {
	}

	@Override
	public void writeData(ExtendedPacketBuffer buffer) throws IOException {
		buffer.writeBoolean(pos != null);
		if (pos != null) {
			buffer.writeBlockPos(pos);
		}
	}

	@Override
	public void readData(ExtendedPacketBuffer buffer) throws IOException {
		if (buffer.readBoolean()) {
			pos = buffer.readBlockPos();
		} else {
			pos = null;
		}
	}

	@Override
	public void processData(SyncBedStatusPacket message, MessageContext context) {
		handle(message, context);
	}

	@SideOnly(Side.CLIENT)
	public void handle(SyncBedStatusPacket message, MessageContext context) {
		JustSleep.updateClientBedLocation(Minecraft.getMinecraft().player, pos);
	}
}
