package data.scripts.plugins;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseEveryFrameCombatPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ViewportAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.lazywizard.lazylib.CollisionUtils;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.AIUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

public class FDS_ProjectilePlugin extends BaseEveryFrameCombatPlugin {
   private static final String DATA_KEY = "FDS_ProjectileData";
   private static final float KANE_DEFAULT_RANGE = 1000.0F;
   private static final float KANE_DEFAULT_SPEED = 800.0F;
   private static final Color KANE_DETONATION_COLOR = new Color(255, 50, 50, 225);
   private static final float KANE_DETONATION_DURATION = 0.2F;
   private static final float KANE_DETONATION_SIZE = 20.0F;
   private static final String KANE_DETONATION_SOUND_ID = "fds_heavy_assault_gun";
   private static final float KANE_FUSE_DISTANCE = 100.0F;
   private static final Color KANE_PARTICLE_COLOR = new Color(255, 100, 100, 200);
   private static final int KANE_PARTICLE_COUNT = 20;
   private static final String KANE_PROJECTILE_ID = "fds_kane_shot";
   private static final float KANE_SPLIT_DISTANCE = 500.0F;
   private static final float KANE_SPREAD_FORCE_MAX = 50.0F;
   private static final float KANE_SPREAD_FORCE_MIN = 25.0F;
   private static final int KANE_SUBMUNITIONS = 10;
   private static final String KANE_SUBMUNITION_WEAPON_ID = "fds_kane_sub";
   private static final float ORIGINAL_PROJECTILE_DAMAGE_MULTIPLIER = 0.5F;
   private static final String MINE_PROJECTILE_ID = "fds_stasis_mine";
   private static final Color MINE_DETONATION_COLOR = new Color(53, 81, 255, 225);
   private static final float MINE_DETONATION_DURATION = 0.2F;
   private static final float MINE_DETONATION_SIZE = 20.0F;
   private static final String MINE_DETONATION_SOUND = "mine_explosion";
   private Map<DamagingProjectileAPI, Float> projectileTrailIDs = new HashMap();
   private Map<DamagingProjectileAPI, Float> projectileSecondaryTrailIDs = new HashMap();
   private Set<DamagingProjectileAPI> kaneProjectileSet;
   private Set<DamagingProjectileAPI> mineProjectileSet;
   private Set<DamagingProjectileAPI> trailingProjectileSet;
   private CombatEngineAPI engine;
   private static final Map<String, String> TRAIL_SPRITES = new HashMap();
   private static final Map<String, Float> DURATION_IN;
   private static final Map<String, Float> DURATION_MAIN;
   private static final Map<String, Float> DURATION_FADE;
   private static final Map<String, Float> START_SIZE;
   private static final Map<String, Float> END_SIZE;
   private static final Map<String, Color> TRAIL_COLOR;
   private static final Map<String, Color> FADE_COLOR;
   private static final Map<String, Float> TRAIL_OPACITY;
   private static final Map<String, Float> LOOP_LENGTH;
   private static final Map<String, Float> SCROLL_SPEED;

   public void advance(float amount, List<InputEventAPI> events) {
      if (this.engine != null && !this.engine.isPaused()) {
         FDS_ProjectilePlugin.LocalData localData = (FDS_ProjectilePlugin.LocalData)this.engine.getCustomData().get("FDS_ProjectileData");
         this.kaneProjectileSet = localData.kaneProjectileSet;
         this.mineProjectileSet = localData.mineProjectileSet;
         this.trailingProjectileSet = localData.trailingProjectileSet;
         List<DamagingProjectileAPI> projectiles = this.engine.getProjectiles();
         int size = projectiles.size();

         for(int i = 0; i < size; ++i) {
            DamagingProjectileAPI proj = (DamagingProjectileAPI)projectiles.get(i);
            String spec = proj.getProjectileSpecId();
            if (spec != null) {
               this.processProjectileTrail(proj);
               this.processProjectileFlicker(proj);
               byte var10 = -1;
               switch(spec.hashCode()) {
               case -267803957:
                  if (spec.equals("fds_stasis_mine")) {
                     var10 = 1;
                  }
                  break;
               case 1429888546:
                  if (spec.equals("fds_kane_shot")) {
                     var10 = 0;
                  }
               }

               switch(var10) {
               case 0:
                  this.processKane(proj);
                  break;
               case 1:
                  this.processMine(proj);
               }
            }
         }

      }
   }

   private void processKane(DamagingProjectileAPI proj) {
      String newSpec = "fds_kane_sub";
      int submunitions = 10;
      float fuseDistance = 100.0F;
      float splitDistance = 500.0F;
      float splitForceMin = 50.0F;
      float splitForceMax = 25.0F;
      float defaultRange = 1000.0F;
      float defaultSpeed = 800.0F;
      Color detonateColor = KANE_DETONATION_COLOR;
      float detonateSize = 20.0F;
      float detonateDuration = 0.2F;
      String detonateSound = "fds_heavy_assault_gun";
      Color particleColor = KANE_PARTICLE_COLOR;
      int particleCount = 20;
      if (!proj.isFading() && !proj.didDamage()) {
         if (!this.kaneProjectileSet.contains(proj)) {
            this.kaneProjectileSet.add(proj);
            proj.getDamage().setDamage(proj.getDamage().getDamage() * 0.5F);
         }

         boolean shouldSplit = false;
         Vector2f loc = proj.getLocation();
         Vector2f vel = proj.getVelocity();
         float speedScalar;
         float rangeScalar;
         if (proj.getSource() != null) {
            speedScalar = proj.getSource().getMutableStats().getProjectileSpeedMult().getModifiedValue();
            rangeScalar = proj.getSource().getMutableStats().getBallisticWeaponRangeBonus().computeEffective(defaultRange) / defaultRange;
         } else {
            rangeScalar = 1.0F;
            speedScalar = 1.0F;
         }

         float speed = defaultSpeed * speedScalar;
         float fuseTime = fuseDistance / speed;
         if (!(proj.getElapsed() < fuseTime)) {
            splitDistance *= rangeScalar;
            float detonateTime;
            if (proj.getWeapon() != null) {
               detonateTime = (proj.getWeapon().getRange() - splitDistance) / speed;
            } else {
               detonateTime = (defaultRange - splitDistance) / speed;
            }

            if (proj.getElapsed() >= detonateTime) {
               shouldSplit = true;
            }

            Vector2f projection;
            int j;
            Vector2f point;
            if (!shouldSplit) {
               projection = new Vector2f(splitDistance, 0.0F);
               VectorUtils.rotate(projection, proj.getFacing(), projection);
               Vector2f.add(loc, projection, projection);
               List<ShipAPI> checkList = this.engine.getShips();
               List<ShipAPI> finalList = new LinkedList();
               int listSize = checkList.size();

               for(j = 0; j < listSize; ++j) {
                  ShipAPI ship = (ShipAPI)checkList.get(j);
                  boolean isInShields = false;
                  if (ship.getShield() != null && ship.getShield().isOn() && MathUtils.isWithinRange(loc, ship.getLocation(), ship.getShield().getRadius() + splitDistance)) {
                     isInShields = ship.getShield().isWithinArc(loc);
                  }

                  if (isInShields || MathUtils.isWithinRange(loc, ship.getLocation(), ship.getCollisionRadius() + splitDistance)) {
                     if (isInShields) {
                        if (CollisionUtils.getCollides(loc, projection, ship.getLocation(), ship.getShield().getRadius())) {
                           finalList.add(ship);
                        }
                     } else if (CollisionUtils.getCollides(loc, projection, ship.getLocation(), ship.getCollisionRadius())) {
                        point = CollisionUtils.getCollisionPoint(loc, projection, ship);
                        if (point != null && MathUtils.getDistance(loc, point) <= splitDistance) {
                           finalList.add(ship);
                        }
                     }
                  }
               }

               ShipAPI closest = null;
               float closestSquareDistance = Float.MAX_VALUE;
               listSize = finalList.size();

               for(j = 0; j < listSize; ++j) {
                  ShipAPI ship = (ShipAPI)finalList.get(j);
                  float squareDistance = MathUtils.getDistanceSquared(loc, ship.getLocation());
                  if (squareDistance < closestSquareDistance) {
                     closestSquareDistance = squareDistance;
                     closest = ship;
                  }
               }

               if (closest != null && (closest.getOwner() == 1 || closest.getOwner() == 0) && closest.getOwner() != proj.getOwner()) {
                  shouldSplit = true;
               }
            }

            if (shouldSplit) {
               projection = new Vector2f(vel);
               projection.scale(0.5F);
               this.engine.spawnExplosion(loc, projection, detonateColor, detonateSize, detonateDuration);
               Global.getSoundPlayer().playSound(detonateSound, 0.25F, 1.0F, loc, projection);
               float forceMultiplier = vel.length() / speed;

               Vector2f actualVel;
               for(j = 0; j < particleCount; ++j) {
                  actualVel = MathUtils.getRandomPointOnCircumference((Vector2f)null, speedScalar / rangeScalar * forceMultiplier * MathUtils.getRandomNumberInRange(splitForceMin, splitForceMax));
                  actualVel.scale((float)Math.random() + 0.75F);
                  Vector2f.add(vel, actualVel, actualVel);
                  actualVel.scale((float)Math.random() + 0.25F);
                  this.engine.addHitParticle(loc, actualVel, (float)Math.random() * 2.0F + 6.0F, 1.0F, ((float)Math.random() * 0.75F + 1.25F) * detonateDuration, particleColor);
               }

               Vector2f defaultVel = new Vector2f(defaultSpeed * speedScalar, 0.0F);
               VectorUtils.rotate(defaultVel, proj.getFacing(), defaultVel);
               actualVel = new Vector2f();

               for(j = 0; j < submunitions; ++j) {
                  Vector2f randomVel = MathUtils.getRandomPointOnCircumference((Vector2f)null, speedScalar / rangeScalar * forceMultiplier * MathUtils.getRandomNumberInRange(splitForceMin, splitForceMax));
                  Vector2f.add(defaultVel, randomVel, actualVel);
                  Vector2f.add(vel, randomVel, randomVel);
                  DamagingProjectileAPI subProj = (DamagingProjectileAPI)this.engine.spawnProjectile(proj.getSource(), proj.getWeapon(), newSpec, loc, VectorUtils.getFacing(actualVel), randomVel);
                  point = subProj.getVelocity();
                  Vector2f.sub(point, defaultVel, point);
               }

               this.kaneProjectileSet.remove(proj);
               this.engine.removeEntity(proj);
            }

         }
      } else {
         this.kaneProjectileSet.remove(proj);
      }
   }

   private void processMine(DamagingProjectileAPI proj) {
      if (!proj.isFading() && !proj.didDamage()) {
         if (!this.mineProjectileSet.contains(proj)) {
            this.mineProjectileSet.add(proj);
         }

         boolean fuseTriggered = false;
         List<ShipAPI> closeEnemies = AIUtils.getNearbyEnemies(proj, 200.0F);
         if (!closeEnemies.isEmpty()) {
            fuseTriggered = true;
         }

         if (fuseTriggered) {
            this.engine.spawnExplosion(proj.getLocation(), proj.getVelocity(), MINE_DETONATION_COLOR, 20.0F, 0.2F);
            Global.getSoundPlayer().playSound("mine_explosion", 1.0F, 1.0F, proj.getLocation(), proj.getVelocity());
            this.mineProjectileSet.remove(proj);
         }
      } else {
         this.mineProjectileSet.remove(proj);
      }
   }

   private void processProjectileFlicker(DamagingProjectileAPI proj) {
      if (!proj.isFading() && !proj.didDamage()) {
         if (!this.trailingProjectileSet.contains(proj)) {
            this.trailingProjectileSet.add(proj);
         }

      } else {
         this.trailingProjectileSet.remove(proj);
      }
   }

   private void processProjectileTrail(DamagingProjectileAPI proj) {
      if (TRAIL_SPRITES.keySet().contains(proj.getProjectileSpecId())) {
         if (this.projectileTrailIDs.get(proj) == null) {
            this.projectileTrailIDs.put(proj, MagicTrailPlugin.getUniqueID());
         }

         String specID = proj.getProjectileSpecId();
         SpriteAPI spriteToUse = Global.getSettings().getSprite("trails", (String)TRAIL_SPRITES.get(specID));
         Vector2f offsetPoint = new Vector2f((float)Math.cos(Math.toRadians((double)proj.getFacing())) * 1.0F, (float)Math.sin(Math.toRadians((double)proj.getFacing())) * 1.0F);
         Vector2f spawnPosition = new Vector2f(offsetPoint.x + proj.getLocation().x, offsetPoint.y + proj.getLocation().y);
         MagicTrailPlugin.AddTrailMemberAdvanced(proj, (Float)this.projectileTrailIDs.get(proj), spriteToUse, spawnPosition, 0.0F, 0.0F, proj.getFacing() - 180.0F, 0.0F, 0.0F, (Float)START_SIZE.get(specID), (Float)END_SIZE.get(specID), (Color)TRAIL_COLOR.get(specID), (Color)FADE_COLOR.get(specID), (Float)TRAIL_OPACITY.get(specID), (Float)DURATION_IN.get(specID), (Float)DURATION_MAIN.get(specID), (Float)DURATION_FADE.get(specID), 770, 1, (Float)LOOP_LENGTH.get(specID), (Float)SCROLL_SPEED.get(specID), new Vector2f(0.0F, 0.0F), (Map)null);
         if (this.projectileSecondaryTrailIDs.get(proj) == null) {
            this.projectileSecondaryTrailIDs.put(proj, MagicTrailPlugin.getUniqueID());
         }

         float opacityMult = 1.0F;
         if (proj.isFading()) {
            opacityMult = proj.getDamageAmount() / proj.getBaseDamageAmount();
         }

         Vector2f velocity = new Vector2f(proj.getVelocity());
         if (velocity.length() < 0.1F && proj.getSource() != null) {
            velocity = new Vector2f(proj.getSource().getVelocity());
         }

         Vector2f projectileVelocity = VectorUtils.rotate(velocity, -proj.getFacing());
         Vector2f baseVelocity = new Vector2f(0.0F, projectileVelocity.getY());
         Vector2f lateralVelocity = VectorUtils.rotate(baseVelocity, proj.getFacing());
         spriteToUse = Global.getSettings().getSprite("trails", "fds_jagged_trail");
         MagicTrailPlugin.AddTrailMemberAdvanced(proj, (Float)this.projectileSecondaryTrailIDs.get(proj), spriteToUse, spawnPosition, 0.0F, MathUtils.getRandomNumberInRange(0.0F, 200.0F), proj.getFacing() - 180.0F, 0.0F, MathUtils.getRandomNumberInRange(-400.0F, 400.0F), (Float)START_SIZE.get(specID), (Float)END_SIZE.get(specID), (Color)FADE_COLOR.get(specID), (Color)FADE_COLOR.get(specID), 0.3F * opacityMult, (Float)DURATION_IN.get(specID), 0.2F, (Float)DURATION_FADE.get(specID) + 0.2F, 770, 1, (Float)LOOP_LENGTH.get(specID), (Float)SCROLL_SPEED.get(specID), lateralVelocity, (Map)null);
      }
   }

   public void init(CombatEngineAPI engine) {
      this.engine = engine;
      this.projectileTrailIDs.clear();
      this.projectileSecondaryTrailIDs.clear();
      Global.getCombatEngine().getCustomData().put("FDS_ProjectileData", new FDS_ProjectilePlugin.LocalData());
   }

   public void renderInWorldCoords(ViewportAPI view) {
      if (this.engine != null && !this.engine.isPaused()) {
         if (this.trailingProjectileSet != null && !this.trailingProjectileSet.isEmpty()) {
            if (this.engine.isPaused()) {
               float var10000 = 0.0F;
            } else {
               this.engine.getElapsedInLastFrame();
            }

            GL11.glEnable(3553);
            Iterator iter = this.trailingProjectileSet.iterator();

            while(true) {
               DamagingProjectileAPI proj;
               do {
                  if (!iter.hasNext()) {
                     return;
                  }

                  proj = (DamagingProjectileAPI)iter.next();
               } while(!proj.didDamage() && this.engine.isEntityInPlay(proj));

               iter.remove();
            }
         }
      }
   }

   static {
      TRAIL_SPRITES.put("fds_dual_autoblaster_cannon_shot", "fds_jagged_trail");
      TRAIL_SPRITES.put("fds_decimator_shot", "fds_trail");
      DURATION_IN = new HashMap();
      DURATION_IN.put("fds_dual_autoblaster_cannon_shot", 0.0F);
      DURATION_IN.put("fds_decimator_shot", 0.0F);
      DURATION_MAIN = new HashMap();
      DURATION_MAIN.put("fds_dual_autoblaster_cannon_shot", 0.1F);
      DURATION_MAIN.put("fds_decimator_shot", 0.0F);
      DURATION_FADE = new HashMap();
      DURATION_FADE.put("fds_dual_autoblaster_cannon_shot", 0.1F);
      DURATION_FADE.put("fds_decimator_shot", 0.75F);
      START_SIZE = new HashMap();
      START_SIZE.put("fds_dual_autoblaster_cannon_shot", 25.0F);
      START_SIZE.put("fds_decimator_shot", 15.0F);
      END_SIZE = new HashMap();
      END_SIZE.put("fds_dual_autoblaster_cannon_shot", 20.0F);
      END_SIZE.put("fds_decimator_shot", 30.0F);
      TRAIL_COLOR = new HashMap();
      TRAIL_COLOR.put("fds_dual_autoblaster_cannon_shot", new Color(0, 200, 0));
      TRAIL_COLOR.put("fds_decimator_shot", new Color(250, 50, 50));
      FADE_COLOR = new HashMap();
      FADE_COLOR.put("fds_dual_autoblaster_cannon_shot", new Color(120, 120, 120));
      FADE_COLOR.put("fds_decimator_shot", new Color(75, 75, 75));
      TRAIL_OPACITY = new HashMap();
      TRAIL_OPACITY.put("fds_dual_autoblaster_cannon_shot", 0.75F);
      TRAIL_OPACITY.put("fds_decimator_shot", 0.8F);
      LOOP_LENGTH = new HashMap();
      LOOP_LENGTH.put("fds_dual_autoblaster_cannon_shot", -1.0F);
      LOOP_LENGTH.put("fds_decimator_shot", -1.0F);
      SCROLL_SPEED = new HashMap();
      SCROLL_SPEED.put("fds_dual_autoblaster_cannon_shot", 0.0F);
      SCROLL_SPEED.put("fds_decimator_shot", 0.0F);
   }

   private static final class LocalData {
      final Set<DamagingProjectileAPI> kaneProjectileSet;
      final Set<DamagingProjectileAPI> mineProjectileSet;
      final Set<DamagingProjectileAPI> trailingProjectileSet;

      private LocalData() {
         this.kaneProjectileSet = new HashSet(100);
         this.mineProjectileSet = new HashSet(100);
         this.trailingProjectileSet = new HashSet(100);
      }

      // $FF: synthetic method
      LocalData(Object x0) {
         this();
      }
   }
}
