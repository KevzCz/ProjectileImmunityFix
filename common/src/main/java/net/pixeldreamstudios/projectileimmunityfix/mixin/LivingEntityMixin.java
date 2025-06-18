package net.pixeldreamstudios.projectileimmunityfix.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.world.World;
import net.pixeldreamstudios.projectileimmunityfix.config.ProjectileImmunityFixConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
	public LivingEntityMixin(EntityType<?> type, World world) {
		super(type, world);
	}

	@Unique
	private int projectileHitCount = 0;

	@Unique
	private int projectileCooldown = 0;

	@Unique
	private int timeSinceLastProjectileHit = 0;

	@Inject(method = "tick", at = @At("HEAD"))
	private void tickProjectileTimers(CallbackInfo ci) {
		LivingEntity self = (LivingEntity)(Object)this;

		ProjectileImmunityFixConfig.BaseProjectileConfig config = ProjectileImmunityFixConfig.INSTANCE.getEffectiveConfig(self);
		if (config == null || !config.enabled) return;

		if (projectileCooldown > 0) {
			projectileCooldown--;
			if (projectileCooldown == 0) {
				projectileHitCount = 0;
			}
		} else if (projectileHitCount > 0) {
			timeSinceLastProjectileHit++;
			if (timeSinceLastProjectileHit >= config.timeWithoutHitResetTicks) {
				projectileHitCount = 0;
				timeSinceLastProjectileHit = 0;
			}
		}
	}


	@Inject(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;applyDamage(Lnet/minecraft/entity/damage/DamageSource;F)V"))
	private void modifyArrowIFrames(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
		LivingEntity self = (LivingEntity) (Object) this;

		if (!(source.getSource() instanceof PersistentProjectileEntity)) return;

		ProjectileImmunityFixConfig.BaseProjectileConfig config = ProjectileImmunityFixConfig.INSTANCE.getEffectiveConfig(self);
		if (config == null || !config.enabled) return;

		if (projectileCooldown > 0) return;

		projectileHitCount++;
		timeSinceLastProjectileHit = 0;

		if (projectileHitCount <= config.maxHitsBeforeCooldown) {
			self.maxHurtTime = 0;
			self.hurtTime = 0;
			self.timeUntilRegen = 0;
		} else {
			projectileCooldown = config.cooldownTicks;
		}
	}
}
