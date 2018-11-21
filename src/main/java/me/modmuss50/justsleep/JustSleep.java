package me.modmuss50.justsleep;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.networking.CustomPayloadHandlerRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerServer;
import net.minecraft.network.packet.client.CPacketCustomPayload;
import net.minecraft.network.packet.server.SPacketCustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.Validate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class JustSleep implements ModInitializer {

	public static final Identifier SYNC_BED_STATUS = new Identifier("justsleep", "sync_bed_status");
	public static final Identifier SET_SPAWN = new Identifier("justsleep", "set_spawn");

	//This is a list of players that should skip setting the spawn next time the event is fired.
	public static Set<String> playerSpawnSetSkip = new HashSet<>();

	//Client side aware map of the players bed locations
	private static HashMap<String, BlockPos> validBedMap = new HashMap<>();

	public static boolean hasValidBedLocation(EntityPlayer player) {
		return getBedLocation(player) != null;
	}

	public static BlockPos getBedLocation(EntityPlayer player) {
		String uuid = player.getUuid().toString();
		if (player.world.isRemote) {
			return validBedMap.getOrDefault(uuid, null);
		}
		BlockPos bedLocation = player.getSpawnPosition();
		if (bedLocation == null) {
			return null;
		}
		//method_7288 = getBedSpawn
		BlockPos bedSpawnLocation = EntityPlayer.method_7288(player.world, bedLocation, false);
		return bedSpawnLocation;
	}

	public static void updateClientBedLocation(EntityPlayer player, BlockPos pos) {
		Validate.isTrue(player.world.isRemote);
		String uuid = player.getUuid().toString();
		validBedMap.remove(uuid);
		validBedMap.put(uuid, pos);

	}

	public static void updateBedMap(EntityPlayerServer player) {
		BlockPos pos = player.getSpawnPosition();
		if (!hasValidBedLocation(player)) {
			pos = null;
		}
		player.networkHandler.sendPacket(createBedStatusPacket(pos));
	}

	@Override
	public void onInitialize() {
		CustomPayloadHandlerRegistry.CLIENT.register(SYNC_BED_STATUS, (packetContext, packetByteBuf) -> {
			BlockPos pos = null;
			if (packetByteBuf.readBoolean()) {
				pos = packetByteBuf.readBlockPos();
			}
			updateClientBedLocation(packetContext.getPlayer(), pos);
		});
		CustomPayloadHandlerRegistry.SERVER.register(SET_SPAWN, (packetContext, packetByteBuf) -> {
			EntityPlayer player = packetContext.getPlayer();
			if (player.isSleeping()) {
				player.setPlayerSpawn(player.sleepingPos, false);
			}
		});
	}

	public static CPacketCustomPayload createBedStatusPacket(BlockPos pos) {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeBoolean(pos != null);
		if (pos != null) {
			buf.writeBlockPos(pos);
		}
		CPacketCustomPayload packet = new CPacketCustomPayload(SYNC_BED_STATUS, buf);
		return packet;
	}

	public static SPacketCustomPayload createSetSpawnPacket() {
		return new SPacketCustomPayload(SET_SPAWN, new PacketByteBuf(Unpooled.buffer()));
	}

}
