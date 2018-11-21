package me.modmuss50.justsleep;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiSleepMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import reborncore.common.network.NetworkManager;

import java.awt.*;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(modid = "justsleep", value = Side.CLIENT)
public class ClientEventHandler {

	static GuiButton button;
	static boolean setSpawn = false;

	@SubscribeEvent
	public static void initGui(GuiScreenEvent.InitGuiEvent event) {
		if (event.getGui() instanceof GuiSleepMP) {
			setSpawn = false;
			GuiSleepMP gui = (GuiSleepMP) event.getGui();
			event.getButtonList().add(button = new GuiButton(202, gui.width / 2 - 100, gui.height - 62, "Set spawn"));
		}
	}

	@SubscribeEvent
	public static void drawScreen(GuiScreenEvent.DrawScreenEvent event) {
		if (event.getGui() instanceof GuiSleepMP) {
			GuiSleepMP gui = (GuiSleepMP) event.getGui();
			EntityPlayer player = Minecraft.getMinecraft().player;
			boolean isCurrentSpawnBed = player.bedLocation.equals(JustSleep.getBedLocation(player));
			button.visible = JustSleep.hasValidBedLocation(player) && !isCurrentSpawnBed && !setSpawn;
			if (!JustSleep.hasValidBedLocation(player)) {
				event.getGui().mc.fontRenderer.drawString("Setting spawn point (No previous bed found)", gui.width / 2 - 120, gui.height - 52, Color.RED.getRGB());
			}
			if (isCurrentSpawnBed) {
				event.getGui().mc.fontRenderer.drawString("This is the current spawn bed", gui.width / 2 - 80, gui.height - 52, Color.BLUE.getRGB());
			}
			if (setSpawn) {
				event.getGui().mc.fontRenderer.drawString("Spawn point updated to this bed", gui.width / 2 - 80, gui.height - 52, Color.GREEN.getRGB());
			}
		}
	}

	@SubscribeEvent
	public static void actionPeformed(GuiScreenEvent.ActionPerformedEvent event) {
		if (event.getGui() instanceof GuiSleepMP && event.getButton().id == 202) {
			setSpawn = true;
			NetworkManager.sendToServer(new SetPlayerSpawnPacket());
		}
	}

}
