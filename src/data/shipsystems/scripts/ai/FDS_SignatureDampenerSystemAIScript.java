package data.shipsystems.scripts.ai;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.FluxTrackerAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipSystemAIScript;
import com.fs.starfarer.api.combat.ShipSystemAPI;
import com.fs.starfarer.api.combat.ShipwideAIFlags;
import com.fs.starfarer.api.combat.ShipwideAIFlags.AIFlags;
import com.fs.starfarer.api.util.IntervalUtil;
import java.util.Iterator;
import java.util.List;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.AIUtils;
import org.lwjgl.util.vector.Vector2f;

public class FDS_SignatureDampenerSystemAIScript implements ShipSystemAIScript {
   private float maxRange;
   private ShipAPI ship;
   private ShipwideAIFlags flags;
   private CombatEngineAPI engine;
   private ShipSystemAPI system;
   private final IntervalUtil tracker = new IntervalUtil(0.4F, 0.6F);

   public void init(ShipAPI ship, ShipSystemAPI system, ShipwideAIFlags flags, CombatEngineAPI engine) {
      this.ship = ship;
      this.flags = flags;
      this.engine = engine;
      this.system = system;
      this.maxRange = 2500.0F;
   }

   public void advance(float amount, Vector2f missileDangerDir, Vector2f collisionDangerDir, ShipAPI target) {
      if (this.engine != null && !this.engine.isPaused()) {
         this.tracker.advance(amount);
         if (this.tracker.intervalElapsed()) {
            FluxTrackerAPI flux = this.ship.getFluxTracker();
            List<ShipAPI> enemies = AIUtils.getNearbyEnemies(this.ship, this.maxRange);
            List<ShipAPI> closeEnemies = AIUtils.getNearbyEnemies(this.ship, (float)((double)this.maxRange * 0.9D));
            List<ShipAPI> allies = AIUtils.getAlliesOnMap(this.ship);
            if (this.system.isActive()) {
               if ((double)flux.getCurrFlux() >= (double)flux.getMaxFlux() * 0.9D && this.flags.hasFlag(AIFlags.HAS_INCOMING_DAMAGE) || enemies.size() == 0 || (double)flux.getHardFlux() >= (double)flux.getMaxFlux() * 0.9D) {
                  this.ship.useSystem();
               }
            } else if ((double)flux.getCurrFlux() <= (double)flux.getMaxFlux() * 0.2D) {
               boolean activeFleet = false;
               Iterator i$ = allies.iterator();

               while(i$.hasNext()) {
                  ShipAPI s = (ShipAPI)i$.next();
                  if (s.getHullSpec().getHullId() == this.ship.getHullSpec().getHullId() && s.getSystem().isOn() && (double)MathUtils.getDistance(this.ship.getLocation(), s.getLocation()) <= (double)this.maxRange * 1.75D) {
                     activeFleet = true;
                  }
               }

               if (closeEnemies.size() > 0 && !activeFleet) {
                  this.ship.useSystem();
               }
            }
         }
      }

   }
}
