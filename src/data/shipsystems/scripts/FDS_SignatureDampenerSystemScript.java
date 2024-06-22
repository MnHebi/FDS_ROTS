package data.shipsystems.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript.State;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript.StatusData;
import data.scripts.plugins.FDS_SpriteRenderManager;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.dark.shaders.distortion.DistortionShader;
import org.dark.shaders.distortion.RippleDistortion;
import org.dark.shaders.distortion.WaveDistortion;
import org.dark.shaders.light.StandardLight;
import org.dark.shaders.util.ShaderLib;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lazywizard.lazylib.combat.entities.SimpleEntity;
import org.lwjgl.util.vector.Vector2f;

public class FDS_SignatureDampenerSystemScript extends BaseShipSystemScript {
   public static final float MAX_RANGE = 2500.0F;
   private static final float TAG = 0.99999F;
   private static final float CAPITAL_SPEED = 0.99999F;
   private static final float CRUISER_SPEED = 0.95F;
   private static final float DESTROYER_SPEED = 0.9F;
   private static final float FRIGATE_SPEED = 0.8F;
   private static final float FIGHTER_SPEED = 0.75F;
   private static final float PHASE_COST = 2.0F;
   private static final float PHASE_UPKEEP_COST = 1.25F;
   private static final String SYSTEM_LOOP = "system_dampener_loop";
   private static final Vector2f ZERO = new Vector2f();
   private List<ShipAPI> affectedShips = new ArrayList();
   private WaveDistortion wave;
   private StandardLight light;
   private boolean isActive = false;

   public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
      CombatEngineAPI engine = Global.getCombatEngine();
      stats.getShieldAbsorptionMult().modifyMult(id, 0.5F);
      stats.getMaxSpeed().modifyMult(id, 0.75F);
      ShipAPI ship = (ShipAPI)stats.getEntity();
      if (ship != null) {
         Map<ShipAPI, RippleDistortion> maps = (Map)Global.getCombatEngine().getCustomData().get("fds_signature_dampener");
         Vector2f loc = ship.getLocation();
         RippleDistortion halo;
         if (!maps.containsKey(ship)) {
            halo = new RippleDistortion(loc, ZERO);
            halo.setCurrentFrame(50.0F);
            DistortionShader.addDistortion(halo);
            maps.put(ship, halo);
         } else {
            halo = (RippleDistortion)maps.get(ship);
         }

         float amount = engine.getElapsedInLastFrame();
         float range = 2500.0F * (float)(-Math.cos((double)(3.0F * effectLevel)) + 1.0D) / 2.0F;
         halo.setSize(range);
         halo.setIntensity(10.0F * effectLevel);
         halo.setLocation(loc);
         float fluxLevel = ship.getFluxLevel();
         if ((double)fluxLevel < 0.5D) {
            fluxLevel = 0.0F;
         } else {
            fluxLevel = (fluxLevel - 0.5F) / 0.5F;
         }

         Color color = interpolateColor(new Color(0, 0, 255), new Color(255, 0, 0), fluxLevel);
         int count = Math.round(effectLevel * 350.0F * amount);
         Global.getSoundPlayer().playLoop("system_dampener_loop", ship, 1.0F, (float)((double)effectLevel * 0.5D), ship.getLocation(), ship.getVelocity());
         FDS_SpriteRenderManager.singleFrameRender(Global.getSettings().getSprite("misc", "fds_signature_dampener_area"), ship.getLocation(), new Vector2f(range * 2.0F, range * 2.0F), 0.0F, color, true);

         for(int i = 0; i < count; ++i) {
            Vector2f point = MathUtils.getRandomPointInCircle(loc, range);
            if (ShaderLib.isOnScreen(point, 200.0F)) {
               float dist = MathUtils.getDistance(loc, point);
               float radius = MathUtils.getRandomNumberInRange(150.0F, 250.0F) * effectLevel * (float)(((double)dist + (double)(range - dist) * 0.2D) / (double)range);
               float angle = VectorUtils.getAngle(point, loc);
               Vector2f vel = MathUtils.getPointOnCircumference((Vector2f)null, radius, angle);
               float duration = MathUtils.getRandomNumberInRange(2.0F, 5.0F);
               float size = MathUtils.getRandomNumberInRange(10.0F, 20.0F);
               engine.addSmoothParticle(point, vel, size, 1.0F, duration, color);
            }
         }

         float possibility = effectLevel * 30.0F * amount;
         if ((float)Math.random() < possibility) {
            Vector2f pointa = MathUtils.getRandomPointOnCircumference(loc, range);
            float pointaAngle = VectorUtils.getAngle(loc, pointa);
            Vector2f pointb = MathUtils.getPointOnCircumference(loc, range, pointaAngle + 10.0F);
            engine.spawnEmpArc(ship, pointa, (CombatEntityAPI)null, new SimpleEntity(pointb), DamageType.OTHER, 0.0F, 0.0F, 10000.0F, (String)null, (float)Math.random() * 40.0F * effectLevel + 20.0F, color, Color.WHITE);
         }

         List<ShipAPI> ships = CombatUtils.getShipsWithinRange(loc, range);
         Iterator i$ = ships.iterator();

         ShipAPI target;
         while(i$.hasNext()) {
            target = (ShipAPI)i$.next();
            if (!this.affectedShips.contains(target)) {
               this.affectedShips.add(target);
            }

            if (target != ship && target.isAlive() && !target.isAlly() && target.getOwner() != ship.getOwner()) {
               this.applyDebuff(target, "fds_signature_dampener", ship, range);
            }
         }

         this.affectedShips.removeAll(ships);
         i$ = this.affectedShips.iterator();

         while(i$.hasNext()) {
            target = (ShipAPI)i$.next();
            this.unapplyDebuff(target, "fds_signature_dampener");
         }

         this.affectedShips = ships;
      }

   }

   private void applyDebuff(ShipAPI target, String id, ShipAPI emitter, float range) {
      MutableShipStatsAPI stats = target.getMutableStats();
      if (target.isPhased()) {
         float distance = MathUtils.getDistance(target, emitter);
         target.getMutableStats().getTimeMult().modifyPercent("fds_signature_dampener", -((range - distance) / range * 25.0F));
         stats.getMaxSpeed().modifyMult(id, 0.99999F);
         stats.getPhaseCloakActivationCostBonus().modifyMult(id, 2.0F);
         stats.getPhaseCloakUpkeepCostBonus().modifyMult(id, 1.25F);
      } else {
         stats.getZeroFluxMinimumFluxLevel().modifyFlat(id, 1.0F);
         stats.getZeroFluxSpeedBoost().modifyMult(id, 0.0F);
         if (target.isCapital()) {
            stats.getMaxSpeed().modifyMult(id, 0.99999F);
         } else if (target.isCruiser()) {
            stats.getMaxSpeed().modifyMult(id, 0.95F);
         } else if (target.isDestroyer()) {
            stats.getMaxSpeed().modifyMult(id, 0.9F);
         } else if (target.isFrigate()) {
            stats.getMaxSpeed().modifyMult(id, 0.8F);
         } else if (!target.isFighter() && !target.isDrone()) {
            stats.getMaxSpeed().modifyMult(id, 0.95F);
         } else {
            stats.getMaxSpeed().modifyMult(id, 0.75F);
         }
      }

   }

   private void unapplyDebuff(ShipAPI target, String id) {
      MutableShipStatsAPI stats = target.getMutableStats();
      stats.getZeroFluxMinimumFluxLevel().unmodify(id);
      stats.getZeroFluxSpeedBoost().unmodify(id);
      stats.getMaxSpeed().unmodify(id);
      stats.getTimeMult().unmodify(id);
   }

   public void unapply(MutableShipStatsAPI stats, String id) {
      ShipAPI ship = (ShipAPI)stats.getEntity();
      if (ship != null) {
         stats.getShieldAbsorptionMult().unmodify(id);
         stats.getMaxSpeed().unmodify(id);
         Map<ShipAPI, RippleDistortion> maps = (Map)Global.getCombatEngine().getCustomData().get("fds_signature_dampener");
         if (maps != null) {
            RippleDistortion halo = (RippleDistortion)maps.get(ship);
            if (halo != null) {
               DistortionShader.removeDistortion(halo);
               maps.remove(ship);
            }
         }
      }

   }

   private static Color interpolateColor(Color old, Color dest, float progress) {
      float clampedProgress = Math.max(0.0F, Math.min(1.0F, progress));
      float antiProgress = 1.0F - clampedProgress;
      float[] ccOld = old.getComponents((float[])null);
      float[] ccNew = dest.getComponents((float[])null);
      return new Color(ccOld[0] * antiProgress + ccNew[0] * clampedProgress, ccOld[1] * antiProgress + ccNew[1] * clampedProgress, ccOld[2] * antiProgress + ccNew[2] * clampedProgress, ccOld[3] * antiProgress + ccNew[3] * clampedProgress);
   }

   public StatusData getStatusData(int index, State state, float effectLevel) {
      int range = (int)(2500.0F * effectLevel);
      if (index == 0) {
         return new StatusData("dampening enemy ships within " + range + " su", false);
      } else {
         return index == 1 ? new StatusData("shields weakened", true) : null;
      }
   }
}
