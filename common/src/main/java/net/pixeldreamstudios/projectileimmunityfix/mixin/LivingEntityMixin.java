package net.pixeldreamstudios.projectileimmunityfix.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
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

	@Unique private int projectileHitCount = 0;
	@Unique private int projectileCooldown = 0;
	@Unique private int timeSinceLastProjectileHit = 0;

	@Unique private int meleeHitCount = 0;
	@Unique private int meleeCooldown = 0;
	@Unique private int timeSinceLastMeleeHit = 0;

	@Inject(method = "tick", at = @At("HEAD"))
	private void tickIFrameTimers(CallbackInfo ci) {
		LivingEntity self = (LivingEntity)(Object)this;

		ProjectileImmunityFixConfig.BaseStackingIFrameConfig projCfg =
				ProjectileImmunityFixConfig.INSTANCE.getEffectiveProjectileConfig(self);
		if (projCfg != null && projCfg.enabled) {
			if (projectileCooldown > 0) {
				projectileCooldown--;
				if (projectileCooldown == 0) {
					projectileHitCount = 0;
				}
			} else if (projectileHitCount > 0) {
				timeSinceLastProjectileHit++;
				if (timeSinceLastProjectileHit >= projCfg.timeWithoutHitResetTicks) {
					projectileHitCount = 0;
					timeSinceLastProjectileHit = 0;
				}
			}
		}

		ProjectileImmunityFixConfig.BaseStackingIFrameConfig meleeCfg =
				ProjectileImmunityFixConfig.INSTANCE.getEffectiveMeleeConfig(self);
		if (meleeCfg != null && meleeCfg.enabled) {
			if (meleeCooldown > 0) {
				meleeCooldown--;
				if (meleeCooldown == 0) {
					meleeHitCount = 0;
				}
			} else if (meleeHitCount > 0) {
				timeSinceLastMeleeHit++;
				if (timeSinceLastMeleeHit >= meleeCfg.timeWithoutHitResetTicks) {
					meleeHitCount = 0;
					timeSinceLastMeleeHit = 0;
				}
			}
		}
	}

	@Inject(
			method = "damage",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;applyDamage(Lnet/minecraft/entity/damage/DamageSource;F)V")
	)
	private void modifyProjectileIFrames(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
		LivingEntity self = (LivingEntity) (Object) this;
		if (!(source.getSource() instanceof PersistentProjectileEntity)) return;

		ProjectileImmunityFixConfig.BaseStackingIFrameConfig config =
				ProjectileImmunityFixConfig.INSTANCE.getEffectiveProjectileConfig(self);
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

	@Inject(
			method = "damage",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;applyDamage(Lnet/minecraft/entity/damage/DamageSource;F)V")
	)
	private void modifyMeleeIFrames(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
		LivingEntity self = (LivingEntity) (Object) this;

		boolean isMelee = source.isOf(DamageTypes.PLAYER_ATTACK) || source.isOf(DamageTypes.MOB_ATTACK);
		if (!isMelee) return;

		ProjectileImmunityFixConfig.BaseStackingIFrameConfig config =
				ProjectileImmunityFixConfig.INSTANCE.getEffectiveMeleeConfig(self);
		if (config == null || !config.enabled) return;
		if (meleeCooldown > 0) return;

		meleeHitCount++;
		timeSinceLastMeleeHit = 0;

		if (meleeHitCount <= config.maxHitsBeforeCooldown) {
			self.maxHurtTime = 0;
			self.hurtTime = 0;
			self.timeUntilRegen = 0;
		} else {
			meleeCooldown = config.cooldownTicks;
		}
	}
}
