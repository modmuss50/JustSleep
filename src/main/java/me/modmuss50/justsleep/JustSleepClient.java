package me.modmuss50.justsleep;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.util.math.BlockPos;

public class JustSleepClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientSidePacketRegistry.INSTANCE.register(JustSleep.SYNC_BED_STATUS, (packetContext, packetByteBuf) -> {
            BlockPos pos = null;
            if (packetByteBuf.readBoolean()) {
                pos = packetByteBuf.readBlockPos();
            }
            JustSleep.updateClientBedLocation(packetContext.getPlayer(), pos);
        });
    }
}
