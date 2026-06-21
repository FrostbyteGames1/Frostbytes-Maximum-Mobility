package net.frostbyte.mobility.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frostbyte.mobility.config.MaximumMobilityConfig;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.VehicleEntity;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(value = AbstractBoat.class, priority = 450)
public abstract class BoatEntityMixin extends VehicleEntity {

    @Shadow
    protected abstract boolean checkInWater();

    public BoatEntityMixin(EntityType<?> entityType, Level world) {
        super(entityType, world);
    }

    public float maxUpStep() {
        if (this.getControllingPassenger() instanceof Player && MaximumMobilityConfig.boatStepUp > 0) {
            if (this.horizontalCollision && this.checkInWater()) {
                return (float) MaximumMobilityConfig.boatStepUp + 0.5F;
            }
            return (float) MaximumMobilityConfig.boatStepUp;
        }
        return 0;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        if (this.horizontalCollision && this.checkInWater() && this.getControllingPassenger() instanceof Player && MaximumMobilityConfig.boatStepUp > 0) {
            this.setOnGround(true);
        }
    }
}
