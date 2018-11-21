package me.modmuss50.justsleep;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.player.PlayerSetSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.commons.lang3.Validate;
import reborncore.common.network.NetworkManager;
import reborncore.common.network.RegisterPacketEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@Mod.EventBusSubscriber
@Mod(modid = "justsleep", name = "Just Sleep", dependencies = "required-after:reborncore", version = "@MODVERSION@")
public class JustSleep {

	//TODO EntityPlayer.wakeUpPlayer

	//This is a list of players that should skip setting the spawn next time the event is fired.
	static Set<String> playerSpawnSetSkip = new HashSet<>();

	//Client side aware map of the players bed locations
	private static HashMap<String, BlockPos> validBedMap = new HashMap<>();

	@SubscribeEvent
	public static void spawnSet(PlayerSetSpawnEvent event) {
		String uuid = event.getEntityPlayer().getUniqueID().toString();
		if (playerSpawnSetSkip.contains(uuid)) {
			playerSpawnSetSkip.remove(uuid);
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void wakeUp(PlayerWakeUpEvent event) {
		if (event.shouldSetSpawn()) {
			String uuid = event.getEntityPlayer().getUniqueID().toString();
			if (hasValidBedLocation(event.getEntityPlayer())) {
				playerSpawnSetSkip.add(uuid);
			}
		}
	}

	@SubscribeEvent
	public static void sleep(PlayerSleepInBedEvent event) {
		if (!event.getEntityPlayer().world.isRemote && event.getEntityPlayer() instanceof EntityPlayerMP) {
			updateBedMap((EntityPlayerMP) event.getEntityPlayer());
		}
	}

	public static boolean hasValidBedLocation(EntityPlayer player) {
		return getBedLocation(player) != null;
	}

	public static BlockPos getBedLocation(EntityPlayer player) {
		String uuid = player.getUniqueID().toString();
		if (player.world.isRemote) {
			return validBedMap.getOrDefault(uuid, null);
		}
		BlockPos bedLocation = player.getBedLocation(player.dimension);
		if (bedLocation == null) {
			return null;
		}
		BlockPos bedSpawnLocation = EntityPlayer.getBedSpawnLocation(player.world, bedLocation, false);
		return bedSpawnLocation;
	}

	@SubscribeEvent
	public static void registerPacket(RegisterPacketEvent event) {
		event.registerPacket(SetPlayerSpawnPacket.class, Side.SERVER);
		event.registerPacket(SyncBedStatusPacket.class, Side.CLIENT);
	}

	public static void updateClientBedLocation(EntityPlayer player, BlockPos pos) {
		Validate.isTrue(player.world.isRemote);
		String uuid = player.getUniqueID().toString();
		validBedMap.remove(uuid);
		validBedMap.put(uuid, pos);

	}

	public static void updateBedMap(EntityPlayerMP player) {
		BlockPos pos = player.getBedLocation(player.dimension);
		if (!hasValidBedLocation(player)) {
			pos = null;
		}
		NetworkManager.sendToPlayer(new SyncBedStatusPacket(pos), player);
	}

}
