package net.frostbyte.mobility.mixin;

import net.frostbyte.mobility.BoatChanger;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BoatEntity.class, priority = 450)
public abstract class BoatEntityMixin extends Entity {

    @Shadow protected abstract boolean checkBoatInWater();

    public BoatEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci){
        this.setStepHeight(BoatChanger.boatStepUp);
        if (BoatChanger.boatStepUp > 0 && this.horizontalCollision && this.checkBoatInWater()) {
            this.addVelocity(new Vec3d(0, 0.1, 0));
        }
    }
}
