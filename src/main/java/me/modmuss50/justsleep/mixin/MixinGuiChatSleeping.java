package me.modmuss50.justsleep.mixin;

import me.modmuss50.justsleep.JustSleep;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.SleepingChatScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ClientPlayNetworkHandler;
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
	private boolean didSetSpawn = false;

	public MixinGuiChatSleeping(String string_1) {
		super(string_1);
	}

	@Inject(method = "init", at = @At("RETURN"))
	protected void init(CallbackInfo info) {
		button = new ButtonWidget(width / 2 - 100, height - 62, 200, 20, "Set Spawn", (widget) -> {
			didSetSpawn = true;
			ClientPlayNetworkHandler networkHandler = MinecraftClient.getInstance().getNetworkHandler();
			if (networkHandler != null) {
				networkHandler.getConnection().send(JustSleep.createSetSpawnPacket());
			}
		});
		addButton(button);
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		super.render(mouseX, mouseY, delta);
		PlayerEntity player = MinecraftClient.getInstance().player;
		boolean isCurrentSpawnBed = player.getSleepingPosition().orElse(BlockPos.ORIGIN).equals(JustSleep.getBedLocation(player));
		button.visible = JustSleep.hasValidBedLocation(player) && !isCurrentSpawnBed && !didSetSpawn;
		if (!JustSleep.hasValidBedLocation(player)) {
			font.draw("Setting spawn point (No previous bed found)", width / 2 - 120, height - 55, Color.RED.getRGB());
		}
		if (isCurrentSpawnBed) {
			font.draw("This is the current spawn bed", width / 2 - 80, height - 55, Color.CYAN.getRGB());
		}
		if (didSetSpawn) {
			font.draw("Spawn point updated to this bed", width / 2 - 80, height - 55, Color.GREEN.getRGB());
		}
	}

}
