package net.frostbyte.mobility.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frostbyte.mobility.config.MaximumMobilityConfig;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.VehicleEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(value = BoatEntity.class, priority = 450)
public abstract class BoatEntityMixin extends VehicleEntity {
    @Shadow abstract boolean checkBoatInWater();
    public BoatEntityMixin(EntityType<?> entityType, World world) {
        super(entityType, world);
    }

    public float getStepHeight() {
        if (this.getControllingPassenger() instanceof PlayerEntity && MaximumMobilityConfig.boatStepUp > 0) {
            if (this.horizontalCollision && this.checkBoatInWater()) {
                return (float) MaximumMobilityConfig.boatStepUp + 0.5F;
            }
            return (float) MaximumMobilityConfig.boatStepUp;
        }
        return 0;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        if (this.horizontalCollision && this.checkBoatInWater() && this.getControllingPassenger() instanceof PlayerEntity && MaximumMobilityConfig.boatStepUp > 0) {
            this.setOnGround(true);
        }
    }
}
