package data.scripts.weapons.ai;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.GuidedMissileAI;
import com.fs.starfarer.api.combat.MissileAIPlugin;
import com.fs.starfarer.api.combat.MissileAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipCommand;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.CollectionUtils.SortEntitiesByDistance;
import org.lazywizard.lazylib.combat.AIUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;

public class FDS_CustomMissileAI implements MissileAIPlugin, GuidedMissileAI {
   private final float OVERSHOT_ANGLE = 90.0F;
   private final float WAVE_TIME = 2.0F;
   private final float WAVE_AMPLITUDE = 120.0F;
   private final float DAMPING = 0.1F;
   private final boolean OVERSTEER = false;
   private final boolean TARGET_SWITCH = true;
   private final Integer RANDOM_TARGET = 0;
   private final boolean ANTI_FIGHTER = false;
   private final boolean ASSAULT = true;
   private final float MAX_SEARCH_RANGE = 2000.0F;
   private float PRECISION_RANGE = 500.0F;
   private final boolean LEADING = true;
   private float ECCM = 3.0F;
   private final float MAX_SPEED;
   private final float MAX_RANGE;
   private final float OFFSET;
   private CombatEngineAPI engine;
   private final MissileAPI MISSILE;
   private CombatEntityAPI target;
   private Vector2f lead = new Vector2f();
   private boolean launch = true;
   private float timer = 0.0F;
   private float check = 0.0F;

   public FDS_CustomMissileAI(MissileAPI missile, ShipAPI launchingShip) {
      this.MISSILE = missile;
      this.MAX_SPEED = missile.getMaxSpeed();
      this.MAX_RANGE = missile.getWeapon().getRange();
      if (missile.getSource().getVariant().getHullMods().contains("eccm")) {
         this.ECCM = 1.0F;
      }

      this.PRECISION_RANGE = (float)Math.pow((double)(2.0F * this.PRECISION_RANGE), 2.0D);
      this.OFFSET = (float)(Math.random() * 3.141592653589793D * 2.0D);
   }

   public void advance(float amount) {
      if (this.engine != Global.getCombatEngine()) {
         this.engine = Global.getCombatEngine();
      }

      if (!Global.getCombatEngine().isPaused() && !this.MISSILE.isFading() && !this.MISSILE.isFizzling()) {
         if (this.target != null && this.target.getOwner() != this.MISSILE.getOwner() && (!(this.target instanceof ShipAPI) || !((ShipAPI)this.target).isHulk()) && this.engine.isEntityInPlay(this.target)) {
            this.timer += amount;
            if (this.launch || this.timer >= this.check) {
               this.launch = false;
               this.timer -= this.check;
               this.check = Math.min(0.25F, Math.max(0.03F, MathUtils.getDistanceSquared(this.MISSILE, this.target) / this.PRECISION_RANGE));
               this.lead = AIUtils.getBestInterceptPoint(this.MISSILE.getLocation(), this.MAX_SPEED * this.ECCM, this.target.getLocation(), this.target.getVelocity());
               if (this.lead == null) {
                  this.lead = this.target.getLocation();
               }
            }

            float correctAngle = VectorUtils.getAngle(this.MISSILE.getLocation(), this.lead);
            float aimAngle = 1.0F;
            if (this.ECCM <= 1.0F) {
               aimAngle = 0.3F;
            }

            correctAngle = (float)((double)correctAngle + (double)(aimAngle * 120.0F * this.check) * Math.cos((double)this.OFFSET + (double)this.MISSILE.getElapsed() * 3.141592653589793D));
            aimAngle = MathUtils.getShortestRotation(this.MISSILE.getFacing(), correctAngle);
            if (Math.abs(aimAngle) < 90.0F) {
               this.MISSILE.giveCommand(ShipCommand.ACCELERATE);
            }

            if (aimAngle < 0.0F) {
               this.MISSILE.giveCommand(ShipCommand.TURN_RIGHT);
            } else {
               this.MISSILE.giveCommand(ShipCommand.TURN_LEFT);
            }

            if (Math.abs(aimAngle) < Math.abs(this.MISSILE.getAngularVelocity()) * 0.1F) {
               this.MISSILE.setAngularVelocity(aimAngle / 0.1F);
            }

         } else {
            this.setTarget(this.assignTarget(this.MISSILE));
            this.MISSILE.giveCommand(ShipCommand.ACCELERATE);
         }
      }
   }

   public CombatEntityAPI assignTarget(MissileAPI missile) {
      ShipAPI theTarget = null;
      ShipAPI source = missile.getSource();
      ShipAPI currentTarget;
      if (source != null && source.getShipTarget() != null && source.getShipTarget() instanceof ShipAPI && source.getShipTarget().getOwner() != missile.getOwner()) {
         currentTarget = source.getShipTarget();
      } else {
         currentTarget = null;
      }

      if (this.RANDOM_TARGET > 0) {
         Vector2f location = missile.getLocation();
         if (this.RANDOM_TARGET < 2) {
            if (currentTarget != null && currentTarget.isAlive() && MathUtils.isWithinRange(missile, currentTarget, this.MAX_RANGE)) {
               location = currentTarget.getLocation();
            } else if (source != null && source.getMouseTarget() != null) {
               location = source.getMouseTarget();
            }
         }

         theTarget = this.getRandomLargeTarget(location);
      } else {
         Iterator i$;
         ShipAPI tmp;
         List closeTargets;
         if (source != null) {
            if (currentTarget != null && currentTarget.isAlive() && currentTarget.getOwner() != missile.getOwner() && !currentTarget.isDrone() && !currentTarget.isFighter()) {
               theTarget = currentTarget;
            } else {
               closeTargets = CombatUtils.getShipsWithinRange(source.getMouseTarget(), 100.0F);
               if (!closeTargets.isEmpty()) {
                  Collections.sort(closeTargets, new SortEntitiesByDistance(source.getMouseTarget()));
                  i$ = closeTargets.iterator();

                  while(i$.hasNext()) {
                     tmp = (ShipAPI)i$.next();
                     if (tmp.isAlive() && tmp.getOwner() != missile.getOwner() && !tmp.isDrone() && !tmp.isFighter() && !tmp.isFrigate()) {
                        theTarget = tmp;
                        break;
                     }
                  }
               }
            }
         }

         if (theTarget == null) {
            closeTargets = AIUtils.getNearbyEnemies(missile, 2000.0F);
            if (!closeTargets.isEmpty()) {
               Collections.sort(closeTargets, new SortEntitiesByDistance(missile.getLocation()));
               i$ = closeTargets.iterator();

               while(true) {
                  do {
                     do {
                        if (!i$.hasNext()) {
                           return theTarget;
                        }

                        tmp = (ShipAPI)i$.next();
                     } while(!tmp.isAlive());
                  } while(tmp.getOwner() == missile.getOwner());

                  if (tmp.isCapital() || tmp.isCruiser()) {
                     theTarget = tmp;
                     break;
                  }

                  if (tmp.isDestroyer() && Math.random() > 0.5D) {
                     theTarget = tmp;
                     break;
                  }

                  if (tmp.isDestroyer() && Math.random() > 0.75D) {
                     theTarget = tmp;
                     break;
                  }

                  if (!tmp.isDrone() && !tmp.isFighter() && Math.random() > 0.95D) {
                     theTarget = tmp;
                     break;
                  }
               }
            }
         }
      }

      return theTarget;
   }

   public ShipAPI getRandomFighterTarget(Vector2f location) {
      ShipAPI select = null;
      Map<Integer, ShipAPI> PRIORITYLIST = new HashMap();
      Map<Integer, ShipAPI> OTHERSLIST = new HashMap();
      int i = 1;
      int u = 1;
      List<ShipAPI> potentialTargets = CombatUtils.getShipsWithinRange(location, this.MAX_RANGE);
      if (!potentialTargets.isEmpty()) {
         Iterator i$ = potentialTargets.iterator();

         while(true) {
            while(true) {
               ShipAPI tmp;
               do {
                  do {
                     if (!i$.hasNext()) {
                        int chooser;
                        if (!PRIORITYLIST.isEmpty()) {
                           chooser = Math.round((float)Math.random() * (float)(i - 1) + 0.5F);
                           select = (ShipAPI)PRIORITYLIST.get(chooser);
                        } else if (!OTHERSLIST.isEmpty()) {
                           chooser = Math.round((float)Math.random() * (float)(u - 1) + 0.5F);
                           select = (ShipAPI)OTHERSLIST.get(chooser);
                           return select;
                        }

                        return select;
                     }

                     tmp = (ShipAPI)i$.next();
                  } while(!tmp.isAlive());
               } while(tmp.getOwner() == this.MISSILE.getOwner());

               if (!tmp.isFighter() && !tmp.isDrone()) {
                  OTHERSLIST.put(u, tmp);
                  ++u;
               } else {
                  PRIORITYLIST.put(i, tmp);
                  ++i;
               }
            }
         }
      } else {
         return select;
      }
   }

   public ShipAPI getRandomLargeTarget(Vector2f location) {
      ShipAPI select = null;
      Map<Integer, ShipAPI> PRIORITY1 = new HashMap();
      Map<Integer, ShipAPI> PRIORITY2 = new HashMap();
      Map<Integer, ShipAPI> PRIORITY3 = new HashMap();
      Map<Integer, ShipAPI> PRIORITY4 = new HashMap();
      Map<Integer, ShipAPI> OTHERSLIST = new HashMap();
      int i = 1;
      int u = 1;
      int v = 1;
      int x = 1;
      int y = 1;
      List<ShipAPI> potentialTargets = CombatUtils.getShipsWithinRange(location, this.MAX_RANGE);
      if (!potentialTargets.isEmpty()) {
         Iterator i$ = potentialTargets.iterator();

         while(i$.hasNext()) {
            ShipAPI tmp = (ShipAPI)i$.next();
            if (tmp.isAlive() && tmp.getOwner() != this.MISSILE.getOwner() && !tmp.isDrone()) {
               if (tmp.isCapital()) {
                  PRIORITY1.put(i, tmp);
                  ++i;
                  PRIORITY2.put(u, tmp);
                  ++u;
                  PRIORITY3.put(x, tmp);
                  ++x;
                  PRIORITY4.put(v, tmp);
                  ++v;
                  OTHERSLIST.put(y, tmp);
                  ++y;
               } else if (tmp.isCruiser()) {
                  PRIORITY2.put(u, tmp);
                  ++u;
                  PRIORITY3.put(x, tmp);
                  ++x;
                  PRIORITY4.put(v, tmp);
                  ++v;
                  OTHERSLIST.put(y, tmp);
                  ++y;
               } else if (tmp.isDestroyer()) {
                  PRIORITY3.put(x, tmp);
                  ++x;
                  PRIORITY4.put(v, tmp);
                  ++v;
                  OTHERSLIST.put(y, tmp);
                  ++y;
               } else if (tmp.isFrigate()) {
                  PRIORITY4.put(v, tmp);
                  ++v;
                  OTHERSLIST.put(y, tmp);
                  ++y;
               } else {
                  OTHERSLIST.put(y, tmp);
                  ++y;
               }
            }
         }

         int chooser;
         if (!PRIORITY1.isEmpty() && Math.random() > 0.800000011920929D) {
            chooser = Math.round((float)Math.random() * (float)(i - 1) + 0.5F);
            select = (ShipAPI)PRIORITY1.get(chooser);
         } else if (!PRIORITY2.isEmpty() && Math.random() > 0.800000011920929D) {
            chooser = Math.round((float)Math.random() * (float)(u - 1) + 0.5F);
            select = (ShipAPI)PRIORITY2.get(chooser);
         } else if (!PRIORITY3.isEmpty() && Math.random() > 0.800000011920929D) {
            chooser = Math.round((float)Math.random() * (float)(x - 1) + 0.5F);
            select = (ShipAPI)PRIORITY3.get(chooser);
         } else if (!PRIORITY4.isEmpty() && Math.random() > 0.800000011920929D) {
            chooser = Math.round((float)Math.random() * (float)(v - 1) + 0.5F);
            select = (ShipAPI)PRIORITY4.get(chooser);
         } else if (!OTHERSLIST.isEmpty()) {
            chooser = Math.round((float)Math.random() * (float)(y - 1) + 0.5F);
            select = (ShipAPI)OTHERSLIST.get(chooser);
         }
      }

      return select;
   }

   public ShipAPI getAnyTarget(Vector2f location) {
      ShipAPI select = null;
      Map<Integer, ShipAPI> TARGETLIST = new HashMap();
      int i = 1;
      List<ShipAPI> potentialTargets = CombatUtils.getShipsWithinRange(location, this.MAX_RANGE);
      if (!potentialTargets.isEmpty()) {
         Iterator i$ = potentialTargets.iterator();

         while(i$.hasNext()) {
            ShipAPI tmp = (ShipAPI)i$.next();
            if (tmp.isAlive() && tmp.getOwner() != this.MISSILE.getOwner() && !tmp.isDrone()) {
               TARGETLIST.put(i, tmp);
               ++i;
            }
         }

         if (!TARGETLIST.isEmpty()) {
            int chooser = Math.round((float)Math.random() * (float)(i - 1) + 0.5F);
            select = (ShipAPI)TARGETLIST.get(chooser);
         }
      }

      return select;
   }

   public CombatEntityAPI getTarget() {
      return this.target;
   }

   public void setTarget(CombatEntityAPI target) {
      this.target = target;
   }

   public void init(CombatEngineAPI engine) {
   }
}
