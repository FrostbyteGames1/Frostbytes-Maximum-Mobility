package net.frostbyte.mobility.mixin;

import com.mojang.brigadier.Message;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frostbyte.mobility.BoatChanger;
import net.minecraft.block.Block;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.VehicleEntity;
import net.minecraft.network.packet.c2s.play.BoatPaddleStateC2SPacket;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@Environment(EnvType.CLIENT)
@Mixin(value = BoatEntity.class, priority = 450)
public abstract class BoatEntityMixin extends VehicleEntity implements VariantHolder<BoatEntity.Type> {
    @Shadow private float ticksUnderwater;
    @Shadow private BoatEntity.Location lastLocation;
    @Shadow private BoatEntity.Location location;
    @Shadow private final float[] paddlePhases;
    @Shadow abstract boolean checkBoatInWater();
    @Shadow abstract BoatEntity.Location checkLocation();
    public void setDamageWobbleTicks(int damageWobbleTicks) {
        this.dataTracker.set(DAMAGE_WOBBLE_TICKS, damageWobbleTicks);
    }
    public int getDamageWobbleTicks() {
        return this.dataTracker.get(DAMAGE_WOBBLE_TICKS);
    }
    public float getDamageWobbleStrength() {
        return this.dataTracker.get(DAMAGE_WOBBLE_STRENGTH);
    }
    public void setDamageWobbleStrength(float damageWobbleStrength) {
        this.dataTracker.set(DAMAGE_WOBBLE_STRENGTH, damageWobbleStrength);
    }
    @Shadow abstract void updatePositionAndRotation();
    @Shadow abstract void setPaddleMovings(boolean leftMoving, boolean rightMoving);
    @Shadow abstract void updateVelocity();
    @Shadow abstract void updatePaddles();
    @Shadow abstract void handleBubbleColumn();
    @Shadow abstract boolean isPaddleMoving(int paddle);
    @Shadow abstract SoundEvent getPaddleSoundEvent();
    @Shadow abstract int getMaxPassengers();
    @Shadow abstract boolean isSmallerThanBoat(Entity entity);

    public BoatEntityMixin(EntityType<?> type, World world) {
        super(type, world);
        this.paddlePhases = new float[2];
        this.intersectionChecked = true;
    }

    @Override
    public void tick() {
        // Boat Step Up Code:

        if (this.horizontalCollision && (this.checkBoatInWater() || this.isOnGround()) && BoatChanger.boatStepUp != 0) {
            Direction dir = this.getHorizontalFacing();
            BlockPos pos = this.getBlockPos().add(dir.getOffsetX(), 0, dir.getOffsetZ());
            for (int i = 0; i <= Math.ceil(BoatChanger.boatStepUp); i++) {
                if (this.getWorld().getBlockState(pos.add(0, i, 0)).isReplaceable() && this.getControllingPassenger() instanceof PlayerEntity) {
                    if (this.checkBoatInWater()) {
                        this.addVelocity(new Vec3d(0, 0.3247967 * Math.pow(i, 0.46426618) * 0.6, 0));
                    } else {
                        this.addVelocity(new Vec3d(0, 0.3247967 * Math.pow(i, 0.46426618), 0));
                    }
                    break;
                }
            }
        }

        // Vanilla Code:
        this.lastLocation = this.location;
        this.location = this.checkLocation();
        if (this.location != BoatEntity.Location.UNDER_WATER && this.location != BoatEntity.Location.UNDER_FLOWING_WATER) {
            this.ticksUnderwater = 0.0F;
        } else {
            ++this.ticksUnderwater;
        }

        if (!this.getWorld().isClient && this.ticksUnderwater >= 60.0F) {
            this.removeAllPassengers();
        }

        if (this.getDamageWobbleTicks() > 0) {
            this.setDamageWobbleTicks(this.getDamageWobbleTicks() - 1);
        }

        if (this.getDamageWobbleStrength() > 0.0F) {
            this.setDamageWobbleStrength(this.getDamageWobbleStrength() - 1.0F);
        }

        super.tick();
        this.updatePositionAndRotation();
        if (this.isLogicalSideForUpdatingMovement()) {
            if (!(this.getFirstPassenger() instanceof PlayerEntity)) {
                this.setPaddleMovings(false, false);
            }

            this.updateVelocity();
            if (this.getWorld().isClient) {
                this.updatePaddles();
                this.getWorld().sendPacket(new BoatPaddleStateC2SPacket(this.isPaddleMoving(0), this.isPaddleMoving(1)));
            }

            this.move(MovementType.SELF, this.getVelocity());
        } else {
            this.setVelocity(Vec3d.ZERO);
        }

        this.handleBubbleColumn();

        for(int i = 0; i <= 1; ++i) {
            if (this.isPaddleMoving(i)) {
                if (!this.isSilent() && (double)(this.paddlePhases[i] % 6.2831855F) <= 0.7853981852531433 && (double)((this.paddlePhases[i] + 0.3926991F) % 6.2831855F) >= 0.7853981852531433) {
                    SoundEvent soundEvent = this.getPaddleSoundEvent();
                    if (soundEvent != null) {
                        Vec3d vec3d = this.getRotationVec(1.0F);
                        double d = i == 1 ? -vec3d.z : vec3d.z;
                        double e = i == 1 ? vec3d.x : -vec3d.x;
                        this.getWorld().playSound((PlayerEntity)null, this.getX() + d, this.getY(), this.getZ() + e, soundEvent, this.getSoundCategory(), 1.0F, 0.8F + 0.4F * this.random.nextFloat());
                    }
                }

                this.paddlePhases[i] += 0.3926991F;
            } else {
                this.paddlePhases[i] = 0.0F;
            }
        }

        this.checkBlockCollision();
        List<Entity> list = this.getWorld().getOtherEntities(this, this.getBoundingBox().expand(0.20000000298023224, -0.009999999776482582, 0.20000000298023224), EntityPredicates.canBePushedBy(this));
        if (!list.isEmpty()) {
            boolean bl = !this.getWorld().isClient && !(this.getControllingPassenger() instanceof PlayerEntity);
            Iterator<Entity> var10 = list.iterator();

            while(true) {
                Entity entity;
                do {
                    if (!var10.hasNext()) {
                        return;
                    }

                    entity = (Entity)var10.next();
                } while(entity.hasPassenger(this));

                if (bl && this.getPassengerList().size() < this.getMaxPassengers() && !entity.hasVehicle() && this.isSmallerThanBoat(entity) && entity instanceof LivingEntity && !(entity instanceof WaterCreatureEntity) && !(entity instanceof PlayerEntity)) {
                    entity.startRiding(this);
                } else {
                    this.pushAwayFrom(entity);
                }
            }

        }
    }
}
