package me.modmuss50.justsleep.mixin;

import me.modmuss50.justsleep.JustSleep;
import me.modmuss50.justsleep.JustSleepClient;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.ingame.ChatGui;
import net.minecraft.client.gui.ingame.ChatSleepingGui;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(ChatSleepingGui.class)
public abstract class MixinGuiChatSleeping extends ChatGui {

	private ButtonWidget button;

	@Inject(method = "onInitialized", at = @At("RETURN"))
	protected void onInitialized(CallbackInfo info) {
		this.addButton(button = JustSleepClient.createButton((ChatSleepingGui) (Object) this));
		JustSleepClient.setSpawn = false;
	}

	@Override
	public void draw(int i, int i1, float v) {
		super.draw(i, i1, v);
		PlayerEntity player = MinecraftClient.getInstance().player;
		boolean isCurrentSpawnBed = player.sleepingPos.equals(JustSleep.getBedLocation(player));
		button.visible = JustSleep.hasValidBedLocation(player) && !isCurrentSpawnBed && !JustSleepClient.setSpawn;
		if (!JustSleep.hasValidBedLocation(player)) {
			fontRenderer.draw("Setting spawn point (No previous bed found)", width / 2 - 120, height - 52, Color.RED.getRGB());
		}
		if (isCurrentSpawnBed) {
			fontRenderer.draw("This is the current spawn bed", width / 2 - 80, height - 52, Color.BLUE.getRGB());
		}
		if (JustSleepClient.setSpawn) {
			fontRenderer.draw("Spawn point updated to this bed", width / 2 - 80, height - 52, Color.GREEN.getRGB());
		}
	}

}
