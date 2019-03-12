package me.modmuss50.justsleep.mixin;

import me.modmuss50.justsleep.JustSleep;
import me.modmuss50.justsleep.JustSleepClient;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.ingame.ChatScreen;
import net.minecraft.client.gui.ingame.SleepingChatScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(SleepingChatScreen.class)
public abstract class MixinGuiChatSleeping extends ChatScreen {

	private ButtonWidget button;

	@Inject(method = "onInitialized", at = @At("RETURN"))
	protected void onInitialized(CallbackInfo info) {
		this.addButton(button = JustSleepClient.createButton((SleepingChatScreen) (Object) this));
		JustSleepClient.setSpawn = false;
	}

	@Override
	public void draw(int i, int i1, float v) {
		super.draw(i, i1, v);
		PlayerEntity player = MinecraftClient.getInstance().player;
		boolean isCurrentSpawnBed = player.getSleepingPosition().orElse(BlockPos.ORIGIN).equals(JustSleep.getBedLocation(player));
		button.visible = JustSleep.hasValidBedLocation(player) && !isCurrentSpawnBed && !JustSleepClient.setSpawn;
		if (!JustSleep.hasValidBedLocation(player)) {
			fontRenderer.draw("Setting spawn point (No previous bed found)", screenWidth / 2 - 120, screenHeight - 55, Color.RED.getRGB());
		}
		if (isCurrentSpawnBed) {
			fontRenderer.draw("This is the current spawn bed", screenWidth / 2 - 80, screenHeight - 55, Color.CYAN.getRGB());
		}
		if (JustSleepClient.setSpawn) {
			fontRenderer.draw("Spawn point updated to this bed", screenWidth / 2 - 80, screenHeight - 55, Color.GREEN.getRGB());
		}
	}

}
