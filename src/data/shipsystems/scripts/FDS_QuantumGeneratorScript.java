package data.shipsystems.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CollisionClass;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipCommand;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShipEngineControllerAPI.ShipEngineAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript.State;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript.StatusData;
import java.awt.Color;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.dark.shaders.util.ShaderLib;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.entities.SimpleEntity;
import org.lwjgl.util.vector.Vector2f;

public class FDS_QuantumGeneratorScript extends BaseShipSystemScript {
   private static final String SYSTEM_EXIT = "system_quantum_generator_exit";
   private static final String SYSTEM_LIGHTNING = "system_emp_emitter_impact";
   private float timer = 0.0F;
   private float timeCounter = 0.0F;
   private boolean isActive = false;
   private boolean destSet = false;
   private boolean soundOn = false;
   private Color bluish = new Color(131, 205, 255);
   private Vector2f destination = new Vector2f(0.0F, 0.0F);
   private Vector2f originalDest = new Vector2f(0.0F, 0.0F);
   private Vector2f velocityVector = new Vector2f(0.0F, 0.0F);
   private Vector2f shiftedPosition = new Vector2f(0.0F, 0.0F);
   private Vector2f entryPoint = new Vector2f(0.0F, 0.0F);
   private float maxSpeed = 0.0F;
   private float startSpeed = 0.0F;
   private static Map arcRadius = new HashMap();

   public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
      if (stats.getEntity() instanceof ShipAPI) {
         ShipAPI ship = (ShipAPI)stats.getEntity();
         CombatEngineAPI engine = Global.getCombatEngine();
         Vector2f vel;
         if (state == State.OUT) {
            ship.getEngineController().fadeToOtherColor(this, Color.WHITE, (Color)null, 1.0F, 0.8F);
            ship.getEngineController().extendFlame(this, 5.0F, 0.25F, 1.0F);
            ship.giveCommand(ShipCommand.ACCELERATE, (Object)null, 0);
            ship.setCollisionClass(CollisionClass.NONE);
            ship.setExtraAlphaMult(1.0F);
            ship.setApplyExtraAlphaToEngines(true);
            if (this.isActive) {
               this.velocityVector = this.directionNormalized(ship);
               this.maxSpeed = ship.getMaxSpeed() + 2000.0F;
               this.startSpeed = ship.getMaxSpeed();
               this.isActive = false;
               this.soundOn = true;
               stats.getAcceleration().modifyMult("fds_quantum_generator", 0.0F);
               stats.getMaxSpeed().modifyFlat("fds_quantum_generator", 2000.0F * (effectLevel / 2.0F));
            }

            vel = new Vector2f(0.0F, 0.0F);
            VectorUtils.resize(this.velocityVector, 2000.0F, vel);
            ship.getVelocity().set(vel);
         } else {
            float radius;
            Vector2f t;
            float duration;
            if (state == State.IN) {
               if (!engine.isPaused()) {
                  this.timer += engine.getElapsedInLastFrame();
               }

               ship.getEngineController().fadeToOtherColor(this, Color.WHITE, (Color)null, 1.0F, 0.8F);
               ship.getEngineController().extendFlame(this, 5.0F, 0.25F, 1.0F);
               ship.giveCommand(ShipCommand.ACCELERATE, (Object)null, 0);
               if (this.timer >= 0.5F) {
                  if (!this.isActive) {
                     this.velocityVector = this.directionNormalized(ship);
                     this.isActive = true;
                     stats.getAcceleration().modifyMult("fds_quantum_generator", 0.0F);
                     stats.getMaxSpeed().modifyFlat("fds_quantum_generator", 2000.0F);
                  }

                  vel = new Vector2f(0.0F, 0.0F);
                  VectorUtils.resize(this.velocityVector, 2000.0F, vel);
                  ship.getVelocity().set(vel);
                  ship.setCollisionClass(CollisionClass.NONE);
                  ship.setExtraAlphaMult(1.0F - (effectLevel - 0.5F) / 0.5F);
                  ship.setApplyExtraAlphaToEngines(true);
               }

               this.timeCounter += engine.getElapsedInLastFrame();
               Vector2f arcShift;
               if (this.timeCounter > 0.2F && this.timer < 0.5F) {
                  this.timeCounter = 0.0F;
                  radius = 1000.0F;
                  arcShift = new Vector2f(0.0F, 0.0F);
                  VectorUtils.resize(this.directionNormalized(ship), radius, arcShift);
                  if (ShaderLib.isOnScreen(ship.getLocation(), radius)) {
                     t = new Vector2f(ship.getLocation().x + arcShift.x, ship.getLocation().y + arcShift.y);
                     Global.getSoundPlayer().playSound("system_emp_emitter_impact", 0.2F, 0.25F, ship.getLocation(), t);
                     engine.spawnEmpArc(ship, ship.getLocation(), (CombatEntityAPI)null, new SimpleEntity(t), DamageType.OTHER, 0.0F, 0.0F, 10000.0F, (String)null, (float)Math.random() * (radius / 25.0F) + 5.0F, this.bluish, Color.WHITE);
                  }
               }

               if (this.timer >= 0.1F) {
                  arcShift = new Vector2f(0.0F, 0.0F);
                  VectorUtils.resize(this.directionNormalized(ship), 1000.0F, arcShift);
                  if (this.timer < 0.5F) {
                     this.entryPoint = new Vector2f(ship.getLocation().x + arcShift.x, ship.getLocation().y + arcShift.y);
                  }

                  for(int i = 0; (float)i < 1.0F * this.timer; ++i) {
                     radius = (Float)arcRadius.get(ship.getHullSpec().getHullSize());
                     vel = MathUtils.getRandomPointInCircle((Vector2f)null, radius);
                     if (ShaderLib.isOnScreen(this.entryPoint, radius)) {
                        if ((float)Math.random() <= 0.5F) {
                           duration = MathUtils.getRandomNumberInRange(0.25F, 1.0F);
                           float size = MathUtils.getRandomNumberInRange(5.0F, 20.0F);
                           engine.addSmoothParticle(this.entryPoint, vel, size, 1.0F, duration, this.bluish);
                        } else {
                           Vector2f t = new Vector2f(this.entryPoint.x + vel.x, this.entryPoint.y + vel.y);
                           engine.spawnEmpArc(ship, this.entryPoint, (CombatEntityAPI)null, new SimpleEntity(t), DamageType.OTHER, 0.0F, 0.0F, 10000.0F, (String)null, (float)Math.random() * (radius / 25.0F) + 5.0F, this.bluish, Color.WHITE);
                        }
                     }
                  }
               }
            } else if (state == State.ACTIVE) {
               if (!engine.isPaused()) {
                  this.timer += engine.getElapsedInLastFrame();
               }

               if (!this.destSet) {
                  this.originalDest = new Vector2f(ship.getLocation());
                  VectorUtils.resize(VectorUtils.rotate(new Vector2f(1.0F, 0.0F), ship.getFacing() + 180.0F), 500.0F, this.shiftedPosition);
                  this.destination = new Vector2f(ship.getLocation().x + this.shiftedPosition.x, ship.getLocation().y + this.shiftedPosition.y);
                  ship.getLocation().set(this.destination);
                  this.destSet = true;
               }

               ship.getEngineController().fadeToOtherColor(this, Color.WHITE, (Color)null, 1.0F, 0.8F);
               ship.getEngineController().extendFlame(this, 5.0F, 0.25F, 1.0F);
               ship.getVelocity().set(new Vector2f(0.0F, 0.0F));
               stats.getMaxSpeed().modifyFlat("fds_quantum_generator", 1.0F);
               ship.setCollisionClass(CollisionClass.NONE);
               ship.setPhased(true);
               ship.setExtraAlphaMult(0.0F);
               ship.setApplyExtraAlphaToEngines(true);

               for(int i = 0; (float)i < 1.0F * this.timer; ++i) {
                  radius = (Float)arcRadius.get(ship.getHullSpec().getHullSize());
                  vel = MathUtils.getRandomPointInCircle((Vector2f)null, radius);
                  if (ShaderLib.isOnScreen(ship.getLocation(), radius)) {
                     if ((float)Math.random() <= 0.5F) {
                        float duration = MathUtils.getRandomNumberInRange(0.25F, 1.0F);
                        duration = MathUtils.getRandomNumberInRange(5.0F, 20.0F);
                        engine.addSmoothParticle(ship.getLocation(), vel, duration, 1.0F, duration, this.bluish);
                     } else {
                        t = new Vector2f(ship.getLocation().x + vel.x, ship.getLocation().y + vel.y);
                        if (Math.abs(MathUtils.getShortestRotation(ship.getFacing(), VectorUtils.getAngle(ship.getLocation(), t))) > 20.0F) {
                           engine.spawnEmpArc(ship, ship.getLocation(), (CombatEntityAPI)null, new SimpleEntity(t), DamageType.OTHER, 0.0F, 0.0F, 10000.0F, (String)null, (float)Math.random() * (radius / 25.0F) + 5.0F, this.bluish, Color.WHITE);
                        }
                     }
                  }
               }

               this.timeCounter += engine.getElapsedInLastFrame();
               if (this.timeCounter > 0.2F) {
                  this.timeCounter = 0.0F;
                  Global.getSoundPlayer().playSound("system_emp_emitter_impact", 0.2F, 0.25F, ship.getLocation(), this.originalDest);
                  radius = (Float)arcRadius.get(ship.getHullSpec().getHullSize());
                  if (ShaderLib.isOnScreen(ship.getLocation(), radius)) {
                     engine.spawnEmpArc(ship, ship.getLocation(), (CombatEntityAPI)null, new SimpleEntity(this.originalDest), DamageType.OTHER, 0.0F, 0.0F, 10000.0F, (String)null, (float)Math.random() * (radius / 25.0F) + 5.0F, this.bluish, Color.WHITE);
                  }
               }
            }
         }

      }
   }

   public StatusData getStatusData(int index, State state, float effectLevel) {
      if (state == State.IN) {
         if (index == 0) {
            return new StatusData("Charging engines...", false);
         }
      } else if (state == State.ACTIVE && index == 0) {
         return new StatusData("Generator Active", false);
      }

      return null;
   }

   public void unapply(MutableShipStatsAPI stats, String id) {
      if (stats.getEntity() instanceof ShipAPI) {
         ShipAPI ship = (ShipAPI)stats.getEntity();
         CombatEngineAPI engine = Global.getCombatEngine();
         List<ShipEngineAPI> engineList = ship.getEngineController().getShipEngines();
         Iterator i$ = engineList.iterator();

         while(i$.hasNext()) {
            ShipEngineAPI shipEngine = (ShipEngineAPI)i$.next();
            if (ship.getMutableStats().getMaxSpeed().getMultStatMod("fds_signature_dampener") != null) {
               shipEngine.disable();
            }
         }

         if (this.soundOn) {
            Global.getSoundPlayer().playSound("system_quantum_generator_exit", 1.0F, 1.0F, ship.getLocation(), new Vector2f(0.0F, 0.0F));
         }

         stats.getMaxSpeed().unmodify("fds_quantum_generator");
         stats.getMaxTurnRate().unmodify("fds_quantum_generator");
         stats.getTurnAcceleration().unmodify("fds_quantum_generator");
         stats.getAcceleration().unmodify("fds_quantum_generator");
         stats.getDeceleration().unmodify("fds_quantum_generator");
         ship.setCollisionClass(CollisionClass.SHIP);
         ship.setExtraAlphaMult(1.0F);
         ship.setPhased(false);
         Vector2f scaledVelocity = new Vector2f(0.0F, 0.0F);
         VectorUtils.resize(this.velocityVector, this.startSpeed, scaledVelocity);
         ship.getVelocity().set(scaledVelocity);
         this.timer = 0.0F;
         this.timeCounter = 0.0F;
         this.isActive = false;
         this.soundOn = false;
         this.destSet = false;
      }
   }

   private Vector2f directionNormalized(ShipAPI ship) {
      return VectorUtils.rotate(new Vector2f(1.0F, 0.0F), ship.getFacing());
   }

   private float weightedAverage(float max, float start, float effectLevel) {
      return max * effectLevel + start * (1.0F - effectLevel);
   }

   static {
      arcRadius.put(HullSize.FIGHTER, 25.0F);
      arcRadius.put(HullSize.FRIGATE, 50.0F);
      arcRadius.put(HullSize.DESTROYER, 100.0F);
      arcRadius.put(HullSize.CRUISER, 150.0F);
      arcRadius.put(HullSize.CAPITAL_SHIP, 250.0F);
   }
}
