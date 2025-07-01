/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Ownable;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.particle.EntityEffectParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryOps;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class AreaEffectCloudEntity
extends Entity
implements Ownable {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int field_29972 = 5;
    private static final TrackedData<Float> RADIUS = DataTracker.registerData(AreaEffectCloudEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Boolean> WAITING = DataTracker.registerData(AreaEffectCloudEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<ParticleEffect> PARTICLE_ID = DataTracker.registerData(AreaEffectCloudEntity.class, TrackedDataHandlerRegistry.PARTICLE);
    private static final float MAX_RADIUS = 32.0f;
    private static final float field_40730 = 0.5f;
    private static final float field_40731 = 3.0f;
    public static final float field_40732 = 6.0f;
    public static final float field_40733 = 0.5f;
    private PotionContentsComponent potionContentsComponent = PotionContentsComponent.DEFAULT;
    private final Map<Entity, Integer> affectedEntities = Maps.newHashMap();
    private int duration = 600;
    private int waitTime = 20;
    private int reapplicationDelay = 20;
    private int durationOnUse;
    private float radiusOnUse;
    private float radiusGrowth;
    @Nullable
    private LivingEntity owner;
    @Nullable
    private UUID ownerUuid;

    public AreaEffectCloudEntity(EntityType<? extends AreaEffectCloudEntity> arg, World arg2) {
        super(arg, arg2);
        this.noClip = true;
    }

    public AreaEffectCloudEntity(World world, double x, double y, double z) {
        this((EntityType<? extends AreaEffectCloudEntity>)EntityType.AREA_EFFECT_CLOUD, world);
        this.setPosition(x, y, z);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(RADIUS, Float.valueOf(3.0f));
        builder.add(WAITING, false);
        builder.add(PARTICLE_ID, EntityEffectParticleEffect.create(ParticleTypes.ENTITY_EFFECT, -1));
    }

    public void setRadius(float radius) {
        if (!this.getWorld().isClient) {
            this.getDataTracker().set(RADIUS, Float.valueOf(MathHelper.clamp(radius, 0.0f, 32.0f)));
        }
    }

    @Override
    public void calculateDimensions() {
        double d = this.getX();
        double e = this.getY();
        double f = this.getZ();
        super.calculateDimensions();
        this.setPosition(d, e, f);
    }

    public float getRadius() {
        return this.getDataTracker().get(RADIUS).floatValue();
    }

    public void setPotionContents(PotionContentsComponent potionContentsComponent) {
        this.potionContentsComponent = potionContentsComponent;
        this.updateColor();
    }

    private void updateColor() {
        ParticleEffect lv = this.dataTracker.get(PARTICLE_ID);
        if (lv instanceof EntityEffectParticleEffect) {
            EntityEffectParticleEffect lv2 = (EntityEffectParticleEffect)lv;
            int i = this.potionContentsComponent.equals(PotionContentsComponent.DEFAULT) ? 0 : this.potionContentsComponent.getColor();
            this.dataTracker.set(PARTICLE_ID, EntityEffectParticleEffect.create(lv2.getType(), ColorHelper.fullAlpha(i)));
        }
    }

    public void addEffect(StatusEffectInstance effect) {
        this.setPotionContents(this.potionContentsComponent.with(effect));
    }

    public ParticleEffect getParticleType() {
        return this.getDataTracker().get(PARTICLE_ID);
    }

    public void setParticleType(ParticleEffect particle) {
        this.getDataTracker().set(PARTICLE_ID, particle);
    }

    protected void setWaiting(boolean waiting) {
        this.getDataTracker().set(WAITING, waiting);
    }

    public boolean isWaiting() {
        return this.getDataTracker().get(WAITING);
    }

    public int getDuration() {
        return this.duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public void tick() {
        super.tick();
        World world = this.getWorld();
        if (world instanceof ServerWorld) {
            ServerWorld lv = (ServerWorld)world;
            this.serverTick(lv);
        } else {
            this.clientTick();
        }
    }

    private void clientTick() {
        float g;
        int i;
        boolean bl = this.isWaiting();
        float f = this.getRadius();
        if (bl && this.random.nextBoolean()) {
            return;
        }
        ParticleEffect lv = this.getParticleType();
        if (bl) {
            i = 2;
            g = 0.2f;
        } else {
            i = MathHelper.ceil((float)Math.PI * f * f);
            g = f;
        }
        for (int j = 0; j < i; ++j) {
            float h = this.random.nextFloat() * ((float)Math.PI * 2);
            float k = MathHelper.sqrt(this.random.nextFloat()) * g;
            double d = this.getX() + (double)(MathHelper.cos(h) * k);
            double e = this.getY();
            double l = this.getZ() + (double)(MathHelper.sin(h) * k);
            if (lv.getType() == ParticleTypes.ENTITY_EFFECT) {
                if (bl && this.random.nextBoolean()) {
                    this.getWorld().addImportantParticle(EntityEffectParticleEffect.create(ParticleTypes.ENTITY_EFFECT, -1), d, e, l, 0.0, 0.0, 0.0);
                    continue;
                }
                this.getWorld().addImportantParticle(lv, d, e, l, 0.0, 0.0, 0.0);
                continue;
            }
            if (bl) {
                this.getWorld().addImportantParticle(lv, d, e, l, 0.0, 0.0, 0.0);
                continue;
            }
            this.getWorld().addImportantParticle(lv, d, e, l, (0.5 - this.random.nextDouble()) * 0.15, 0.01f, (0.5 - this.random.nextDouble()) * 0.15);
        }
    }

    private void serverTick(ServerWorld world) {
        boolean bl2;
        if (this.age >= this.waitTime + this.duration) {
            this.discard();
            return;
        }
        boolean bl = this.isWaiting();
        boolean bl3 = bl2 = this.age < this.waitTime;
        if (bl != bl2) {
            this.setWaiting(bl2);
        }
        if (bl2) {
            return;
        }
        float f = this.getRadius();
        if (this.radiusGrowth != 0.0f) {
            if ((f += this.radiusGrowth) < 0.5f) {
                this.discard();
                return;
            }
            this.setRadius(f);
        }
        if (this.age % 5 == 0) {
            this.affectedEntities.entrySet().removeIf(entity -> this.age >= (Integer)entity.getValue());
            if (!this.potionContentsComponent.hasEffects()) {
                this.affectedEntities.clear();
            } else {
                ArrayList<StatusEffectInstance> list = Lists.newArrayList();
                if (this.potionContentsComponent.potion().isPresent()) {
                    for (StatusEffectInstance lv : this.potionContentsComponent.potion().get().value().getEffects()) {
                        list.add(new StatusEffectInstance(lv.getEffectType(), lv.mapDuration(duration -> duration / 4), lv.getAmplifier(), lv.isAmbient(), lv.shouldShowParticles()));
                    }
                }
                list.addAll(this.potionContentsComponent.customEffects());
                List<LivingEntity> list2 = this.getWorld().getNonSpectatingEntities(LivingEntity.class, this.getBoundingBox());
                if (!list2.isEmpty()) {
                    for (LivingEntity lv2 : list2) {
                        double e;
                        double d;
                        double g;
                        if (this.affectedEntities.containsKey(lv2) || !lv2.isAffectedBySplashPotions()) continue;
                        if (list.stream().noneMatch(lv2::canHaveStatusEffect) || !((g = (d = lv2.getX() - this.getX()) * d + (e = lv2.getZ() - this.getZ()) * e) <= (double)(f * f))) continue;
                        this.affectedEntities.put(lv2, this.age + this.reapplicationDelay);
                        for (StatusEffectInstance lv3 : list) {
                            if (lv3.getEffectType().value().isInstant()) {
                                lv3.getEffectType().value().applyInstantEffect(world, this, this.getOwner(), lv2, lv3.getAmplifier(), 0.5);
                                continue;
                            }
                            lv2.addStatusEffect(new StatusEffectInstance(lv3), this);
                        }
                        if (this.radiusOnUse != 0.0f) {
                            if ((f += this.radiusOnUse) < 0.5f) {
                                this.discard();
                                return;
                            }
                            this.setRadius(f);
                        }
                        if (this.durationOnUse == 0) continue;
                        this.duration += this.durationOnUse;
                        if (this.duration > 0) continue;
                        this.discard();
                        return;
                    }
                }
            }
        }
    }

    public float getRadiusOnUse() {
        return this.radiusOnUse;
    }

    public void setRadiusOnUse(float radiusOnUse) {
        this.radiusOnUse = radiusOnUse;
    }

    public float getRadiusGrowth() {
        return this.radiusGrowth;
    }

    public void setRadiusGrowth(float radiusGrowth) {
        this.radiusGrowth = radiusGrowth;
    }

    public int getDurationOnUse() {
        return this.durationOnUse;
    }

    public void setDurationOnUse(int durationOnUse) {
        this.durationOnUse = durationOnUse;
    }

    public int getWaitTime() {
        return this.waitTime;
    }

    public void setWaitTime(int waitTime) {
        this.waitTime = waitTime;
    }

    public void setOwner(@Nullable LivingEntity owner) {
        this.owner = owner;
        this.ownerUuid = owner == null ? null : owner.getUuid();
    }

    @Override
    @Nullable
    public LivingEntity getOwner() {
        World world;
        if (this.owner != null && !this.owner.isRemoved()) {
            return this.owner;
        }
        if (this.ownerUuid != null && (world = this.getWorld()) instanceof ServerWorld) {
            LivingEntity lv2;
            ServerWorld lv = (ServerWorld)world;
            Entity entity = lv.getEntity(this.ownerUuid);
            this.owner = entity instanceof LivingEntity ? (lv2 = (LivingEntity)entity) : null;
        }
        return this.owner;
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        this.age = nbt.getInt("Age");
        this.duration = nbt.getInt("Duration");
        this.waitTime = nbt.getInt("WaitTime");
        this.reapplicationDelay = nbt.getInt("ReapplicationDelay");
        this.durationOnUse = nbt.getInt("DurationOnUse");
        this.radiusOnUse = nbt.getFloat("RadiusOnUse");
        this.radiusGrowth = nbt.getFloat("RadiusPerTick");
        this.setRadius(nbt.getFloat("Radius"));
        if (nbt.containsUuid("Owner")) {
            this.ownerUuid = nbt.getUuid("Owner");
        }
        RegistryOps<NbtElement> lv = this.getRegistryManager().getOps(NbtOps.INSTANCE);
        if (nbt.contains("Particle", NbtElement.COMPOUND_TYPE)) {
            ParticleTypes.TYPE_CODEC.parse(lv, nbt.get("Particle")).resultOrPartial(string -> LOGGER.warn("Failed to parse area effect cloud particle options: '{}'", string)).ifPresent(this::setParticleType);
        }
        if (nbt.contains("potion_contents")) {
            PotionContentsComponent.CODEC.parse(lv, nbt.get("potion_contents")).resultOrPartial(string -> LOGGER.warn("Failed to parse area effect cloud potions: '{}'", string)).ifPresent(this::setPotionContents);
        }
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putInt("Age", this.age);
        nbt.putInt("Duration", this.duration);
        nbt.putInt("WaitTime", this.waitTime);
        nbt.putInt("ReapplicationDelay", this.reapplicationDelay);
        nbt.putInt("DurationOnUse", this.durationOnUse);
        nbt.putFloat("RadiusOnUse", this.radiusOnUse);
        nbt.putFloat("RadiusPerTick", this.radiusGrowth);
        nbt.putFloat("Radius", this.getRadius());
        RegistryOps<NbtElement> lv = this.getRegistryManager().getOps(NbtOps.INSTANCE);
        nbt.put("Particle", ParticleTypes.TYPE_CODEC.encodeStart(lv, this.getParticleType()).getOrThrow());
        if (this.ownerUuid != null) {
            nbt.putUuid("Owner", this.ownerUuid);
        }
        if (!this.potionContentsComponent.equals(PotionContentsComponent.DEFAULT)) {
            NbtElement lv2 = PotionContentsComponent.CODEC.encodeStart(lv, this.potionContentsComponent).getOrThrow();
            nbt.put("potion_contents", lv2);
        }
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        if (RADIUS.equals(data)) {
            this.calculateDimensions();
        }
        super.onTrackedDataSet(data);
    }

    @Override
    public PistonBehavior getPistonBehavior() {
        return PistonBehavior.IGNORE;
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        return EntityDimensions.changing(this.getRadius() * 2.0f, 0.5f);
    }

    @Override
    public final boolean damage(ServerWorld world, DamageSource source, float amount) {
        return false;
    }

    @Override
    @Nullable
    public /* synthetic */ Entity getOwner() {
        return this.getOwner();
    }
}

