package me.modmuss50.justsleep;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.PacketRegistry;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.client.network.packet.CustomPayloadS2CPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.packet.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.lang3.Validate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class JustSleep implements ModInitializer {

	public static final Identifier SYNC_BED_STATUS = new Identifier("justsleep", "sync_bed_status");
	public static final Identifier SET_SPAWN = new Identifier("justsleep", "set_spawn");

	//This is a list of players that should skip setting the spawn next time the event is fired.
	public static Set<String> playerSpawnSetSkip = new HashSet<>();

	//Client side aware map of the players bed locations
	private static HashMap<String, BlockPos> validBedMap = new HashMap<>();

	public static boolean hasValidBedLocation(PlayerEntity player) {
		return getBedLocation(player) != null;
	}

	public static BlockPos getBedLocation(PlayerEntity player) {
		String uuid = player.getUuid().toString();
		if (player.world.isClient) {
			return validBedMap.getOrDefault(uuid, null);
		}
		BlockPos bedLocation = player.getSpawnPosition();
		if (bedLocation == null) {
			return null;
		}
		//method_7288 = getBedSpawn
		Optional<Vec3d> bedSpawnLocation = PlayerEntity.method_7288(player.world, bedLocation, false);
		return bedSpawnLocation.map(BlockPos::new).orElse(null);
	}

	public static void updateClientBedLocation(PlayerEntity player, BlockPos pos) {
		Validate.isTrue(player.world.isClient);
		String uuid = player.getUuid().toString();
		validBedMap.remove(uuid);
		validBedMap.put(uuid, pos);

	}

	public static void updateBedMap(ServerPlayerEntity player) {
		BlockPos pos = player.getSpawnPosition();
		if (!hasValidBedLocation(player)) {
			pos = null;
		}
		player.networkHandler.sendPacket(createBedStatusPacket(pos));
	}

	@Override
	public void onInitialize() {
		ServerSidePacketRegistry.INSTANCE.register(SET_SPAWN, (packetContext, packetByteBuf) -> {
			PlayerEntity player = packetContext.getPlayer();
			if (player.isSleeping()) {
				player.getSleepingPosition().ifPresent((blockPos) -> player.setPlayerSpawn(blockPos, false, false));
			}
		});
	}

	public static CustomPayloadS2CPacket createBedStatusPacket(BlockPos pos) {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeBoolean(pos != null);
		if (pos != null) {
			buf.writeBlockPos(pos);
		}
		return new CustomPayloadS2CPacket(SYNC_BED_STATUS, buf);
	}

	public static CustomPayloadC2SPacket createSetSpawnPacket() {
		return new CustomPayloadC2SPacket(SET_SPAWN, new PacketByteBuf(Unpooled.buffer()));
	}

}
