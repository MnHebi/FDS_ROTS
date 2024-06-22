package data.shipsystems.scripts.ai;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipCommand;
import com.fs.starfarer.api.combat.ShipSystemAIScript;
import com.fs.starfarer.api.combat.ShipSystemAPI;
import com.fs.starfarer.api.combat.ShipwideAIFlags;
import com.fs.starfarer.api.combat.CombatFleetManagerAPI.AssignmentInfo;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShipwideAIFlags.AIFlags;
import com.fs.starfarer.api.util.IntervalUtil;
import java.awt.Color;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;

public class FDS_QuantumGeneratorAIScript implements ShipSystemAIScript {
   private ShipAPI ship;
   private ShipwideAIFlags flags;
   private CombatEngineAPI engine;
   private ShipSystemAPI system;
   private final IntervalUtil tracker = new IntervalUtil(1.0F, 2.0F);
   private final IntervalUtil evaluate = new IntervalUtil(2.0F, 4.0F);
   private AssignmentInfo assignments;
   private static Map safeDistance = new HashMap();
   private FDS_QuantumGeneratorAIScript.ShipOrder order;
   private ShipAPI target;

   public void init(ShipAPI ship, ShipSystemAPI system, ShipwideAIFlags flags, CombatEngineAPI engine) {
      this.ship = ship;
      this.flags = flags;
      this.engine = engine;
      this.system = system;
   }

   public void advance(float amount, Vector2f missileDangerDir, Vector2f collisionDangerDir, ShipAPI target) {
      if (this.engine != null) {
         if (this.engine.isPaused()) {
            return;
         }

         this.tracker.advance(amount);
         this.evaluate.advance(amount);
         if (this.evaluate.intervalElapsed()) {
            AssignmentInfo assignments = this.engine.getFleetManager(this.ship.getOwner()).getTaskManager(false).getAssignmentFor(this.ship);
            if (assignments != null) {
               if (this.assignments == null || assignments.getType() != this.assignments.getType()) {
                  this.assignments = assignments;
               }
            } else {
               if (!this.flags.hasFlag(AIFlags.PURSUING) && (!this.flags.hasFlag(AIFlags.HARASS_MOVE_IN) || this.flags.hasFlag(AIFlags.HARASS_MOVE_IN_COOLDOWN))) {
                  if (this.flags.hasFlag(AIFlags.BACK_OFF) && this.flags.hasFlag(AIFlags.DO_NOT_PURSUE)) {
                     this.order = FDS_QuantumGeneratorAIScript.ShipOrder.RETREAT;
                  }
               } else {
                  this.order = FDS_QuantumGeneratorAIScript.ShipOrder.ENGAGE;
               }

               this.order = null;
            }
         }

         if (this.tracker.intervalElapsed()) {
            this.engine.addSmoothParticle(this.ship.getMouseTarget(), new Vector2f(0.0F, 0.0F), 50.0F, 1.0F, 1.0F, Color.PINK);
            if (this.ship.getShipTarget() != null) {
               this.engine.addSmoothParticle(this.ship.getShipTarget().getLocation(), new Vector2f(0.0F, 0.0F), 500.0F, 1.0F, 1.0F, Color.RED);
            }

            if (this.system.isOutOfAmmo() || this.system.getCooldownRemaining() > 0.0F && !this.system.isOn()) {
               return;
            }

            if (this.ship.getMutableStats().getMaxSpeed().getMultStatMod("fds_signature_dampener") != null) {
               if (this.system.isActive()) {
                  this.ship.useSystem();
               }

               return;
            }

            if (this.ship.getFluxTracker().getFluxLevel() > 0.8F) {
               return;
            }

            if (this.system.isActive() && this.flags.hasFlag(AIFlags.AVOIDING_BORDER)) {
               this.ship.useSystem();
            }

            if (this.order == null) {
               return;
            }

            switch(this.order) {
            case MOVE:
               this.executeMove();
               break;
            case ENGAGE:
               this.executeEngage();
               break;
            case RETREAT:
               this.executeRetreat();
               break;
            case FLANK:
               this.executeFlank();
               break;
            case CAPTURE:
               this.executeCapture();
               break;
            case ESCORT:
               this.executeEscort(this.target);
            }
         }
      }

   }

   private void executeMove() {
   }

   private void executeEngage() {
   }

   private void executeRetreat() {
   }

   private void executeFlank() {
   }

   private void executeCapture() {
   }

   private void executeEscort(ShipAPI escortee) {
      if (escortee != null) {
         if (MathUtils.getDistance(this.ship.getLocation(), escortee.getLocation()) > 2000.0F) {
            Vector2f point = null;
            int tries = 0;

            while(true) {
               Vector2f p;
               List ships;
               do {
                  if (point != null || tries >= 10) {
                     if (point != null) {
                        this.ship.giveCommand(ShipCommand.USE_SYSTEM, point, 0);
                     }

                     return;
                  }

                  ++tries;
                  float distanceMult = (Float)safeDistance.get(escortee.getHullSpec().getHullSize());
                  p = MathUtils.getRandomPointOnCircumference(escortee.getLocation(), 200.0F * distanceMult);
                  ships = CombatUtils.getShipsWithinRange(p, 200.0F);
               } while(ships.isEmpty());

               Iterator i$ = ships.iterator();

               while(i$.hasNext()) {
                  ShipAPI ship = (ShipAPI)i$.next();
                  if (ship.getHullSpec().getHullSize().equals(HullSize.FIGHTER)) {
                     point = p;
                  }
               }
            }
         }
      }
   }

   private void closeIn(Vector2f location, boolean allied) {
      this.ship.giveCommand(ShipCommand.USE_SYSTEM, location, 0);
   }

   static {
      safeDistance.put(HullSize.FIGHTER, 1.0F);
      safeDistance.put(HullSize.FRIGATE, 1.0F);
      safeDistance.put(HullSize.DESTROYER, 1.0F);
      safeDistance.put(HullSize.CRUISER, 2.0F);
      safeDistance.put(HullSize.CAPITAL_SHIP, 3.0F);
   }

   private static enum ShipOrder {
      MOVE,
      ENGAGE,
      RETREAT,
      FLANK,
      CAPTURE,
      ESCORT;
   }
}
