package data.scripts.plugins;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseEveryFrameCombatPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.MissileAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShipHullSpecAPI.ShipTypeHints;
import com.fs.starfarer.api.util.IntervalUtil;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.lazywizard.lazylib.combat.AIUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;

public class FDS_FactionCombatBuff extends BaseEveryFrameCombatPlugin {
   public static final Object KEY_STATUS = new Object();
   private CombatEngineAPI engine;
   private ShipAPI playerShip;
   private final String BUFF_ID = "fds_syndicates_might";
   public static final float MAX_RANGE = 3000.0F;
   public static final float SENSOR_MINE_RANGE = 1000.0F;
   public static final float MAX_BOOST = 0.5F;
   public static final float ALLIED_BOOST = 0.5F;
   private final IntervalUtil tracker = new IntervalUtil(1.0F, 1.0F);
   private final IntervalUtil fasterTracker = new IntervalUtil(0.1F, 0.1F);
   private List<ShipAPI> sensors = new ArrayList();

   public void init(CombatEngineAPI engine) {
      this.engine = engine;
   }

   public void advance(float amount, List events) {
      if (this.engine != Global.getCombatEngine()) {
         this.engine = Global.getCombatEngine();
      }

      if (this.playerShip == null || this.playerShip != this.engine.getPlayerShip()) {
         this.playerShip = this.engine.getPlayerShip();
      }

      if (this.engine != null) {
         this.processSensorMod();
         this.processSensorMines();
         if (this.engine.isPaused()) {
            return;
         }

         this.tracker.advance(amount);
         this.fasterTracker.advance(amount);
         if (this.tracker.intervalElapsed()) {
            List<ShipAPI> ships = CombatUtils.getShipsWithinRange(new Vector2f(this.engine.getMapWidth() / 2.0F, this.engine.getMapHeight() / 2.0F), 100000.0F);
            Iterator i$ = ships.iterator();

            while(i$.hasNext()) {
               ShipAPI ship = (ShipAPI)i$.next();
               this.processShip(ship);
            }
         }

         if (this.playerShip != null && this.playerShip.getMutableStats().getCRLossPerSecondPercent().getMultBonus("fds_syndicates_might") != null && this.playerShip.getVariant().hasHullMod("fds_syndicate_combat_systems")) {
            String icon = Global.getSettings().getSpriteName("ui", "fds_combat_buff");
            String title = "Nearby allied ships: " + this.countPlayerNearbyShips();
            String data = "-" + (int)((1.0F - this.playerShip.getMutableStats().getCRLossPerSecondPercent().getMultBonus("fds_syndicates_might").value) * 100.0F) + "% CR loss";
            this.engine.maintainStatusForPlayerShip(KEY_STATUS, icon, title, data, false);
         }
      }

   }

   private void processShip(ShipAPI s) {
      List<ShipAPI> ships = AIUtils.getAlliesOnMap(s);
      Iterator i$ = ships.iterator();

      while(i$.hasNext()) {
         ShipAPI ship = (ShipAPI)i$.next();
         if (ship.getVariant().hasHullMod("fds_syndicate_combat_systems")) {
            ship.getMutableStats().getCRLossPerSecondPercent().modifyMult("fds_syndicates_might", this.calculateBonus(ship));
         }
      }

   }

   private void processSensorMines() {
      List<MissileAPI> mines = this.engine.getMissiles();
      Iterator i$ = mines.iterator();

      while(i$.hasNext()) {
         MissileAPI sensor = (MissileAPI)i$.next();
         if (sensor.getProjectileSpecId() == "fds_sensor_mine") {
            this.engine.getFogOfWar(sensor.getOwner()).revealAroundPoint(sensor, sensor.getLocation().x, sensor.getLocation().y, 1000.0F);
         }
      }

   }

   private void processSensorMod() {
      List<ShipAPI> ships = this.engine.getShips();
      Iterator i$ = ships.iterator();

      while(true) {
         ShipAPI ship;
         boolean hasBuff;
         do {
            do {
               if (!i$.hasNext()) {
                  return;
               }

               ship = (ShipAPI)i$.next();
            } while(!ship.getVariant().hasHullMod("fds_augmented_sensor_arrays"));

            hasBuff = ship.getMutableStats().getSensorStrength().getPercentStatMod("fds_overclocked_sensors") != null;
         } while(!hasBuff);

         Vector2f loc = ship.getLocation();
         boolean ally = ship.isAlly() || ship.getOwner() == 0;
         this.engine.getFogOfWar(ally ? 0 : 1).revealAroundPoint(ship, loc.x, loc.y, 3600.0F);
      }
   }

   private float calculateBonus(ShipAPI ship) {
      List<ShipAPI> ships = AIUtils.getNearbyAllies(ship, 3000.0F);
      Iterator i$ = ships.iterator();
      float count = 0.0F;

      while(true) {
         while(i$.hasNext()) {
            ShipAPI s = (ShipAPI)i$.next();
            if (s.getHullSpec().getHullId().startsWith("fds_") && !s.getHullSpec().getHullSize().equals(HullSize.FIGHTER) && !s.getHullSpec().getHints().contains(ShipTypeHints.CIVILIAN)) {
               ++count;
            } else if (!s.getHullSpec().getHullId().startsWith("fds_") && !s.getHullSpec().getHullSize().equals(HullSize.FIGHTER) && !s.getHullSpec().getHints().contains(ShipTypeHints.CIVILIAN)) {
               count += 0.5F;
            }
         }

         return count / 20.0F > 0.5F ? 0.5F : 1.0F - count / 20.0F;
      }
   }

   private int countPlayerNearbyShips() {
      List<ShipAPI> ships = AIUtils.getNearbyAllies(this.playerShip, 3000.0F);
      Iterator i$ = ships.iterator();
      int count = 0;

      while(i$.hasNext()) {
         ShipAPI s = (ShipAPI)i$.next();
         if (!s.getHullSpec().getHullSize().equals(HullSize.FIGHTER) && !s.getHullSpec().getHints().contains(ShipTypeHints.CIVILIAN)) {
            ++count;
         }
      }

      return count;
   }
}
