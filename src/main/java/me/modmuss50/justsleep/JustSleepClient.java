package me.modmuss50.justsleep;

import net.minecraft.client.MinecraftGame;
import net.minecraft.client.audio.SoundLoader;
import net.minecraft.client.gui.ingame.GuiChatSleeping;
import net.minecraft.client.gui.widget.WidgetButton;

public class JustSleepClient {

	public static boolean setSpawn = false;

	//this is in a seprate class becuase mixin doesnt handle anonymous classes
	public static WidgetButton createButton(GuiChatSleeping gui) {
		return new WidgetButton(202, gui.width / 2 - 100, gui.height - 62, "set Spawn") {
			@Override
			public void onPressed(SoundLoader soundLoader) {
				setSpawn = true;
				MinecraftGame.getInstance().getNetworkHandler().getClientConnection().sendPacket(JustSleep.createSetSpawnPacket());
				super.onPressed(soundLoader);
			}
		};
	}
}
