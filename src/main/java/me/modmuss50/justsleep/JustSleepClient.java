package me.modmuss50.justsleep;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.audio.SoundLoader;
import net.minecraft.client.gui.ingame.ChatSleepingGui;
import net.minecraft.client.gui.widget.ButtonWidget;

public class JustSleepClient {

	public static boolean setSpawn = false;

	//this is in a seprate class becuase mixin doesnt handle anonymous classes
	public static ButtonWidget createButton(ChatSleepingGui gui) {
		return new ButtonWidget(202, gui.width / 2 - 100, gui.height - 62, "Set Spawn") {
			@Override
			public void onPressed(double d1, double d2) {
				setSpawn = true;
				MinecraftClient.getInstance().getNetworkHandler().getClientConnection().sendPacket(JustSleep.createSetSpawnPacket());
				super.onPressed(d1, d2);
			}
		};
	}
}
