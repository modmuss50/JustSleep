package me.modmuss50.justsleep.mixin;

import me.modmuss50.justsleep.JustSleep;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFactory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityPlayer.class)
public abstract class MixinEntityPlayer extends Entity {

	public MixinEntityPlayer(EntityFactory<?> entityFactory, World world) {
		super(entityFactory, world);
	}

	// method_7358 awakePlayer
	@Inject(method = "method_7358", at = @At("HEAD"))
	public void method_7358(boolean var1, boolean var2, boolean updateSpawn, CallbackInfo info) {
		if (updateSpawn) {
			String uuid = getUuid().toString();
			if (JustSleep.hasValidBedLocation((EntityPlayer) (Object) this)) {
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
	public void trySleep(BlockPos var1, CallbackInfoReturnable<EntityPlayer.SleepResult> info) {
		if (!world.isRemote && (EntityPlayer) (Object) this instanceof EntityPlayerServer) {
			JustSleep.updateBedMap((EntityPlayerServer) (Object) this);
		}
	}

}
