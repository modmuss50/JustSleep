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

	@Inject(method = "setPlayerSpawn", at = @At("HEAD"), cancellable = true)
	public void setPlayerSpawn(BlockPos pos, boolean forced, boolean fromBed, CallbackInfo info) {
		// Skip setting spawn if it was from bed, but allow it if they were
		// sneaking and it is day (otherwise no way to set spawn during day)
		if (JustSleep.hasValidBedLocation((PlayerEntity)(Object)this)) {
			boolean isSneaking = this.isSneaking();
			if (fromBed && !(this.world.isDay() && isSneaking)) info.cancel();
		}
	}

	@Inject(method = "trySleep", at = @At("RETURN"))
	public void trySleep(CallbackInfoReturnable<PlayerEntity.SleepFailureReason> info) {
		if (!world.isClient && (PlayerEntity) (Object) this instanceof ServerPlayerEntity) {
			JustSleep.updateBedMap((ServerPlayerEntity) (Object) this);
		}
	}

}
