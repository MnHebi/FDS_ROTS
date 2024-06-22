package data.scripts.plugins;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseEveryFrameCombatPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.util.IntervalUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.AIUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;

public class FDS_QuantumSignatureCalculator extends BaseEveryFrameCombatPlugin {
   private CombatEngineAPI engine;
   private final IntervalUtil tracker = new IntervalUtil(2.0F, 4.0F);
   public static List<FDS_QuantumSignatureCalculator.ThreatLevels> shipDangerLevel = new ArrayList();
   public static List<FDS_QuantumSignatureCalculator.JumpIntentions> jumpIntentions = new ArrayList();
   private static List<ShipAPI> jumpers = new ArrayList();
   private static Map weight = new HashMap();
   private static Map personality;

   public void init(CombatEngineAPI engine) {
      this.engine = engine;
   }

   public void advance(float amount, List events) {
      if (this.engine != Global.getCombatEngine()) {
         this.engine = Global.getCombatEngine();
      }

      if (this.engine != null) {
         if (this.engine.isPaused()) {
            return;
         }

         this.tracker.advance(amount);
         if (this.tracker.intervalElapsed()) {
            List<ShipAPI> ships = CombatUtils.getShipsWithinRange(new Vector2f(this.engine.getMapWidth() / 2.0F, this.engine.getMapHeight() / 2.0F), 100000.0F);
            Iterator i$ = ships.iterator();

            while(i$.hasNext()) {
               ShipAPI ship = (ShipAPI)i$.next();
               this.bringOutYourDead();
               if (!ship.getHullSpec().getHullSize().equals(HullSize.FIGHTER) && !ship.isHulk()) {
                  this.calculateShipDangerLevels(ship);
               }
            }

            this.generateJumpPoints();
         }
      }

   }

   public static void addJumper(ShipAPI ship) {
      jumpers.add(ship);
   }

   private void bringOutYourDead() {
      Iterator j$;
      if (shipDangerLevel != null && shipDangerLevel.size() > 0) {
         j$ = shipDangerLevel.iterator();

         while(j$.hasNext()) {
            FDS_QuantumSignatureCalculator.ThreatLevels t = (FDS_QuantumSignatureCalculator.ThreatLevels)j$.next();
            if (t.isDead()) {
               j$.remove();
            }
         }
      }

      if (jumpers != null && jumpers.size() > 0) {
         j$ = jumpers.iterator();

         label43:
         while(true) {
            ShipAPI s;
            do {
               if (!j$.hasNext()) {
                  break label43;
               }

               s = (ShipAPI)j$.next();
            } while(s != null && !s.isHulk());

            j$.remove();
         }
      }

      if (jumpIntentions != null && jumpIntentions.size() > 0) {
         j$ = jumpIntentions.iterator();

         while(j$.hasNext()) {
            FDS_QuantumSignatureCalculator.JumpIntentions j = (FDS_QuantumSignatureCalculator.JumpIntentions)j$.next();
            if (j.isShipDead()) {
               j$.remove();
            }
         }
      }

   }

   private void calculateShipDangerLevels(ShipAPI ship) {
      FDS_QuantumSignatureCalculator.ThreatLevels threatLevels = new FDS_QuantumSignatureCalculator.ThreatLevels();
      threatLevels.setShip(ship);
      float threat = (Float)weight.get(ship.getHullSpec().getHullSize());
      if (ship.getVariant().hasTag("STATION")) {
         threat *= 10.0F;
      }

      List<ShipAPI> enemyShips = AIUtils.getEnemiesOnMap(ship);
      threatLevels.setEnemy(this.calculateThreat(ship, enemyShips));
      List<ShipAPI> alliedShips = AIUtils.getAlliesOnMap(ship);
      threatLevels.setAllied(threat + this.calculateThreat(ship, alliedShips));
      boolean check = false;
      if (shipDangerLevel != null) {
         Iterator i$ = shipDangerLevel.iterator();

         while(i$.hasNext()) {
            FDS_QuantumSignatureCalculator.ThreatLevels t = (FDS_QuantumSignatureCalculator.ThreatLevels)i$.next();
            if (t.getShip() == ship) {
               int idx = shipDangerLevel.indexOf(t);
               if (idx >= 0) {
                  check = true;
                  shipDangerLevel.set(idx, threatLevels);
               }
            }
         }

         if (!check) {
            shipDangerLevel.add(threatLevels);
         }
      } else {
         shipDangerLevel.add(threatLevels);
      }

   }

   private float calculateThreat(ShipAPI ship, List<ShipAPI> ships) {
      float threat = 0.0F;
      Iterator i$ = ships.iterator();

      while(i$.hasNext()) {
         ShipAPI s = (ShipAPI)i$.next();
         if (!s.getHullSpec().getHullSize().equals(HullSize.FIGHTER) && !s.isHulk()) {
            float temp = (Float)weight.get(s.getHullSpec().getHullSize());
            if (s.getVariant().hasTag("STATION")) {
               temp *= 10.0F;
            }

            threat += temp / MathUtils.getDistance(ship, s) * 1000.0F;
         }
      }

      return threat;
   }

   private float calculateThreat(Vector2f point, List<ShipAPI> ships) {
      float threat = 0.0F;
      Iterator i$ = ships.iterator();

      while(i$.hasNext()) {
         ShipAPI s = (ShipAPI)i$.next();
         if (!s.getHullSpec().getHullSize().equals(HullSize.FIGHTER) && !s.isHulk()) {
            float temp = (Float)weight.get(s.getHullSpec().getHullSize());
            if (s.getVariant().hasTag("STATION")) {
               temp *= 10.0F;
            }

            threat += temp / MathUtils.getDistance(point, s.getLocation()) * 1000.0F;
         }
      }

      return threat;
   }

   private void generateJumpPoints() {
      if (shipDangerLevel != null && shipDangerLevel.size() > 0) {
         Iterator i$ = shipDangerLevel.iterator();

         while(i$.hasNext()) {
            FDS_QuantumSignatureCalculator.ThreatLevels t = (FDS_QuantumSignatureCalculator.ThreatLevels)i$.next();
            List<ShipAPI> enemyShips = AIUtils.getEnemiesOnMap(t.getShip());
            List<ShipAPI> alliedShips = AIUtils.getAlliesOnMap(t.getShip());
            List<FDS_QuantumSignatureCalculator.PointThreat> points = t.getPointThreat();
            Iterator p$ = points.iterator();
            float angle = 0.0F;
            float baseDistance = 800.0F;
            float clearCount = 0.0F;

            for(Vector2f loc = t.getShip().getLocation(); p$.hasNext(); angle += 45.0F) {
               FDS_QuantumSignatureCalculator.PointThreat p = (FDS_QuantumSignatureCalculator.PointThreat)p$.next();
               float shipAngle = t.getShip().getFacing();
               Vector2f vector = new Vector2f(baseDistance * (float)Math.cos((double)(shipAngle + angle)), baseDistance * (float)Math.sin((double)(shipAngle + angle)));
               Vector2f point = new Vector2f(loc.getX() + vector.getX(), loc.getY() + vector.getY());
               p.setPoint(point);
               p.setAllied(this.calculateThreat(point, alliedShips));
               p.setEnemy(this.calculateThreat(point, enemyShips));
               if (CombatUtils.getShipsWithinRange(point, 300.0F).size() > 0) {
                  p.setClear(false);
               } else {
                  ++clearCount;
                  p.setClear(true);
               }
            }

            t.setClearPoints(clearCount);
         }
      }

   }

   public static FDS_QuantumSignatureCalculator.JumpIntentions getJumpIntentionsShip(ShipAPI ship) {
      if (ship == null) {
         return null;
      } else {
         Iterator i$ = jumpIntentions.iterator();

         FDS_QuantumSignatureCalculator.JumpIntentions t;
         do {
            if (!i$.hasNext()) {
               return null;
            }

            t = (FDS_QuantumSignatureCalculator.JumpIntentions)i$.next();
         } while(t.getShip() != ship);

         return t;
      }
   }

   public static FDS_QuantumSignatureCalculator.ThreatLevels getShipDangerLevels(ShipAPI ship) {
      Iterator i$ = shipDangerLevel.iterator();

      FDS_QuantumSignatureCalculator.ThreatLevels t;
      do {
         if (!i$.hasNext()) {
            return null;
         }

         t = (FDS_QuantumSignatureCalculator.ThreatLevels)i$.next();
      } while(t.getShip() != ship);

      return t;
   }

   static {
      weight.put(HullSize.FIGHTER, 0.0F);
      weight.put(HullSize.FRIGATE, 1.0F);
      weight.put(HullSize.DESTROYER, 4.0F);
      weight.put(HullSize.CRUISER, 7.0F);
      weight.put(HullSize.CAPITAL_SHIP, 10.0F);
      personality = new HashMap();
      personality.put("timid", 0.5F);
      personality.put("cautious", 0.75F);
      personality.put("steady", 1.0F);
      personality.put("aggressive", 1.2F);
      personality.put("reckless", 1.5F);
   }

   public class ThreatLevels {
      private ShipAPI ship;
      private float allied;
      private float enemy;
      private List<FDS_QuantumSignatureCalculator.PointThreat> pointThreat;
      private float clearPoints;

      public ThreatLevels() {
         this.setAllied(0.0F);
         this.setEnemy(0.0F);
         this.setClearPoints(0.0F);
         this.pointThreat = new ArrayList();

         for(int i = 0; i < 8; ++i) {
            this.pointThreat.add(FDS_QuantumSignatureCalculator.this.new PointThreat());
         }

      }

      public void setShip(ShipAPI ship) {
         this.ship = ship;
      }

      public void setAllied(float allied) {
         this.allied = allied;
      }

      public void setEnemy(float enemy) {
         this.enemy = enemy;
      }

      public void setClearPoints(float clearPoints) {
         this.clearPoints = clearPoints;
      }

      public void setPointThreat(List<FDS_QuantumSignatureCalculator.PointThreat> pointThreat) {
         this.pointThreat = pointThreat;
      }

      public float getAllied() {
         return this.allied;
      }

      public float getEnemy() {
         return this.enemy;
      }

      public List<FDS_QuantumSignatureCalculator.PointThreat> getPointThreat() {
         return this.pointThreat;
      }

      public float numClearPoints() {
         return this.clearPoints;
      }

      public boolean isDead() {
         return this.ship != null ? this.ship.isHulk() : true;
      }

      public ShipAPI getShip() {
         return this.ship;
      }
   }

   private class PointThreat {
      private Vector2f point;
      private float allied;
      private float enemy;
      private boolean clear;

      public PointThreat() {
         this.setAllied(0.0F);
         this.setEnemy(0.0F);
         this.setClear(true);
      }

      public void setPoint(Vector2f point) {
         this.point = point;
      }

      public void setAllied(float allied) {
         this.allied = allied;
      }

      public void setEnemy(float enemy) {
         this.enemy = enemy;
      }

      public void setClear(boolean clear) {
         this.clear = clear;
      }

      public Vector2f getPoint() {
         return this.point;
      }

      public float getAllied() {
         return this.allied;
      }

      public float getEnemy() {
         return this.enemy;
      }

      public boolean isClear() {
         return this.clear;
      }
   }

   public class JumpIntentions {
      private ShipAPI ship;
      private FDS_QuantumSignatureCalculator.PointThreat jumpPoint;
      private boolean maneuvering = false;
      private boolean ready = false;
      private boolean canJump = false;

      public JumpIntentions() {
      }

      public JumpIntentions(FDS_QuantumSignatureCalculator.PointThreat jumpPoint) {
         this.setJumpPoints(jumpPoint);
      }

      public JumpIntentions(ShipAPI ship, FDS_QuantumSignatureCalculator.PointThreat jumpPoint) {
         this.setShip(ship);
         this.setJumpPoints(jumpPoint);
      }

      public ShipAPI getShip() {
         return this.ship;
      }

      private FDS_QuantumSignatureCalculator.PointThreat getJumpPoint() {
         return this.jumpPoint;
      }

      public boolean isManeuvering() {
         return this.maneuvering;
      }

      public boolean isReady() {
         return this.canJump && this.ready;
      }

      public boolean canJump() {
         return this.canJump;
      }

      public void setReady(boolean ready) {
         this.ready = ready;
      }

      public void setCanJump(boolean canJump) {
         this.canJump = canJump;
      }

      private void setJumpPoints(FDS_QuantumSignatureCalculator.PointThreat jumpPoint) {
         this.jumpPoint = jumpPoint;
      }

      public void setManeuvering(boolean maneuvering) {
         this.maneuvering = maneuvering;
      }

      public void setShip(ShipAPI ship) {
         this.ship = ship;
      }

      public boolean isShipDead() {
         return this.ship != null ? this.ship.isHulk() : true;
      }
   }
}
