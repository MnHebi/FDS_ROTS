package data.shipsystems.scripts.ai;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipSystemAIScript;
import com.fs.starfarer.api.combat.ShipSystemAPI;
import com.fs.starfarer.api.combat.ShipwideAIFlags;
import com.fs.starfarer.api.combat.ShipwideAIFlags.AIFlags;
import com.fs.starfarer.api.util.IntervalUtil;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lwjgl.util.vector.Vector2f;

public class FDS_YeetDriveAIScript implements ShipSystemAIScript {
   private static final float minRange = 1000.0F;
   private ShipAPI ship;
   private ShipwideAIFlags flags;
   private CombatEngineAPI engine;
   private ShipSystemAPI system;
   private final IntervalUtil tracker = new IntervalUtil(0.1F, 0.2F);
   private final IntervalUtil timer = new IntervalUtil(2.0F, 2.0F);
   private boolean trigger = false;

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

         if (this.trigger) {
            this.timer.advance(this.engine.getElapsedInLastFrame());
            if (this.timer.intervalElapsed()) {
               this.ship.useSystem();
               this.trigger = false;
            }
         }

         this.tracker.advance(this.engine.getElapsedInLastFrame());
         if (this.tracker.intervalElapsed()) {
            if (this.system.isOutOfAmmo() || this.system.getCooldownRemaining() > 0.0F && !this.system.isOn()) {
               return;
            }

            if (this.ship.getMutableStats().getMaxSpeed().getMultStatMod("fds_signature_dampener") != null) {
               if (this.system.isActive()) {
                  this.ship.useSystem();
               }

               return;
            }

            if (this.system.isActive() && this.flags.hasFlag(AIFlags.AVOIDING_BORDER)) {
               this.ship.useSystem();
            }

            if (target == null) {
               return;
            }

            float angle = Math.abs(MathUtils.getShortestRotation(this.ship.getFacing(), VectorUtils.getAngle(this.ship.getLocation(), target.getLocation())));
            float distance = MathUtils.getDistance(this.ship, target);
            if (this.system.isActive()) {
               if (!this.flags.hasFlag(AIFlags.PURSUING) && (angle > 60.0F || distance < 600.0F) || this.flags.hasFlag(AIFlags.PURSUING) && (angle > 80.0F || distance < 400.0F)) {
                  this.trigger = true;
               }
            } else if (target.isAlive() && !target.isAlly()) {
               if (distance >= 1000.0F) {
                  if (angle <= 5.0F && distance < 1500.0F) {
                     this.ship.useSystem();
                  } else if (angle <= 2.0F && distance < 2000.0F) {
                     this.ship.useSystem();
                  } else if (angle <= 1.0F && distance >= 2000.0F) {
                     this.ship.useSystem();
                  }
               }
            } else if (this.flags.hasFlag(AIFlags.PURSUING)) {
               this.ship.useSystem();
            }
         }
      }

   }
}
