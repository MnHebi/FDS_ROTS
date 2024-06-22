package data.shipsystems.scripts.ai;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipSystemAIScript;
import com.fs.starfarer.api.combat.ShipSystemAPI;
import com.fs.starfarer.api.combat.ShipwideAIFlags;
import com.fs.starfarer.api.util.IntervalUtil;
import java.util.Iterator;
import java.util.List;
import org.lazywizard.lazylib.combat.AIUtils;
import org.lwjgl.util.vector.Vector2f;

public class FDS_OverclockedSensorArrayAIScript implements ShipSystemAIScript {
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
   }

   public void advance(float amount, Vector2f missileDangerDir, Vector2f collisionDangerDir, ShipAPI target) {
      if (this.engine != null && !this.engine.isPaused()) {
         this.tracker.advance(amount);
         if (this.tracker.intervalElapsed()) {
            List<ShipAPI> allies = AIUtils.getAlliesOnMap(this.ship);
            List<ShipAPI> enemies = AIUtils.getEnemiesOnMap(this.ship);
            if (!this.system.isActive()) {
               boolean activeFleet = false;
               Iterator i$ = allies.iterator();

               while(i$.hasNext()) {
                  ShipAPI s = (ShipAPI)i$.next();
                  if (s.getHullSpec().getHullId() == this.ship.getHullSpec().getHullId() && s.getSystem().isOn()) {
                     activeFleet = true;
                  }
               }

               if (enemies.size() > 0 && !activeFleet) {
                  this.ship.useSystem();
               }
            }
         }
      }

   }
}
