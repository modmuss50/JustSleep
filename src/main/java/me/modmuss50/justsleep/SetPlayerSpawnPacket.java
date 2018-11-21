package me.modmuss50.justsleep;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import reborncore.common.network.ExtendedPacketBuffer;
import reborncore.common.network.INetworkPacket;

import java.io.IOException;

public class SetPlayerSpawnPacket implements INetworkPacket<SetPlayerSpawnPacket> {

	public SetPlayerSpawnPacket() {
	}

	@Override
	public void writeData(ExtendedPacketBuffer buffer) throws IOException {
	}

	@Override
	public void readData(ExtendedPacketBuffer buffer) throws IOException {
	}

	@Override
	public void processData(SetPlayerSpawnPacket message, MessageContext context) {
		EntityPlayer player = context.getServerHandler().player;
		if (player.isPlayerSleeping()) {
			player.setSpawnPoint(player.bedLocation, false);
		}
	}
}