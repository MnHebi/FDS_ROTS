package data.shipsystems.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CollisionClass;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipEngineControllerAPI.ShipEngineAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript.State;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript.StatusData;
import java.awt.Color;
import java.util.Iterator;
import java.util.List;
import org.lwjgl.util.vector.Vector2f;

public class FDS_YeeetDriveScript extends BaseShipSystemScript {
   private static final String EXIT_BOOM = "system_quantum_generator_exit";
   private static final Color RED = new Color(255, 50, 50, 255);
   private boolean isActive = false;

   public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
      if (stats.getEntity() instanceof ShipAPI) {
         ShipAPI ship = (ShipAPI)stats.getEntity();
         CombatEngineAPI engine = Global.getCombatEngine();
         if (state == State.OUT) {
            if (this.isActive) {
               List<ShipEngineAPI> engineList = ship.getEngineController().getShipEngines();
               Iterator i$ = engineList.iterator();

               while(i$.hasNext()) {
                  ShipEngineAPI shipEngine = (ShipEngineAPI)i$.next();
                  engine.addSmokeParticle(shipEngine.getLocation(), new Vector2f(), 50.0F, 0.5F, 0.5F, Color.RED);
               }

               Global.getSoundPlayer().playSound("system_quantum_generator_exit", 1.0F, 1.0F, ship.getLocation(), ship.getVelocity());
               stats.getMaxSpeed().unmodify("fds_quantum_generator");
               this.isActive = false;
            }
         } else if (state == State.IN) {
            ship.getEngineController().fadeToOtherColor(this, RED, (Color)null, 1.0F, 0.8F);
            if (!this.isActive) {
               this.isActive = true;
            } else {
               this.forceShutdown(engine, ship, state);
            }
         } else {
            boolean shutdown = this.forceShutdown(engine, ship, state);
            if (!shutdown) {
               if (ship.getMutableStats().getMaxSpeed().getMultStatMod("fds_signature_dampener") != null) {
                  this.unapply(ship.getMutableStats(), "fds_quantum_generator");
               } else {
                  ship.getEngineController().fadeToOtherColor(this, Color.RED, (Color)null, 1.0F, 0.8F);
                  ship.getEngineController().extendFlame(this, 3.0F, 1.0F, 1.0F);
                  stats.getMaxSpeed().modifyFlat("fds_quantum_generator", 4000.0F * effectLevel);
                  stats.getAcceleration().modifyFlat("fds_quantum_generator", 20000.0F * effectLevel);
               }
            }
         }

      }
   }

   public StatusData getStatusData(int index, State state, float effectLevel) {
      if (state == State.IN) {
         if (index == 0) {
            return new StatusData("Get ready to YEEEEET", true);
         }
      } else if (state == State.ACTIVE && index == 0) {
         return new StatusData("YEEEEEEEEEEEEETTT!!!!", true);
      }

      return null;
   }

   public void unapply(MutableShipStatsAPI stats, String id) {
      if (stats.getEntity() instanceof ShipAPI) {
         stats.getMaxSpeed().unmodify("fds_quantum_generator");
         stats.getMaxTurnRate().unmodify("fds_quantum_generator");
         stats.getTurnAcceleration().unmodify("fds_quantum_generator");
         stats.getAcceleration().unmodify("fds_quantum_generator");
         stats.getDeceleration().unmodify("fds_quantum_generator");
         ShipAPI ship = (ShipAPI)stats.getEntity();
         ship.setCollisionClass(CollisionClass.SHIP);
      }
   }

   private boolean forceShutdown(CombatEngineAPI engine, ShipAPI ship, State state) {
      float height = engine.getMapHeight();
      float width = engine.getMapWidth();
      float x = ship.getLocation().x;
      float y = ship.getLocation().y;
      if (this.isActive && (state == State.IN || state == State.ACTIVE)) {
         if (x <= -width / 2.0F || x >= width / 2.0F || y <= -height / 2.0F || y >= height / 2.0F) {
            ship.useSystem();
            return true;
         }

         if (ship.getMutableStats().getMaxSpeed().getMultStatMod("fds_signature_dampener") != null) {
            ship.useSystem();
            return true;
         }
      }

      return false;
   }
}
