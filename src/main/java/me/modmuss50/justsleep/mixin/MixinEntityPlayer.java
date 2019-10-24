package me.modmuss50.justsleep.mixin;

import me.modmuss50.justsleep.JustSleep;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class MixinEntityPlayer extends Entity {

	public MixinEntityPlayer(EntityType<?> entityFactory, World world) {
		super(entityFactory, world);
	}

	// method_7358 awakePlayer
	@Inject(method = "wakeUp", at = @At("HEAD"))
	public void method_7358(boolean var1, boolean var2, boolean updateSpawn, CallbackInfo info) {
		if (updateSpawn) {
			String uuid = getUuid().toString();
			if (JustSleep.hasValidBedLocation((PlayerEntity) (Object) this)) {
				JustSleep.playerSpawnSetSkip.add(uuid);
			}
		}
	}

	@Inject(method = "setPlayerSpawn", at = @At("HEAD"), cancellable = true)
	public void setPlayerSpawn(BlockPos var1, boolean var2, CallbackInfo info) {
		String uuid = getUuid().toString();
		if (JustSleep.playerSpawnSetSkip.contains(uuid)) {
			JustSleep.playerSpawnSetSkip.remove(uuid);
			info.cancel();
		}
	}

	@Inject(method = "trySleep", at = @At("RETURN"), cancellable = true)
	public void trySleep(BlockPos var1, CallbackInfoReturnable<PlayerEntity.SleepFailureReason> info) {
		if (!world.isClient && (PlayerEntity) (Object) this instanceof ServerPlayerEntity) {
			JustSleep.updateBedMap((ServerPlayerEntity) (Object) this);
		}
	}

}
