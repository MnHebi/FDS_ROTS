package data.shipsystems.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatFleetManagerAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript.State;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript.StatusData;
import data.scripts.plugins.FDS_SpriteRenderManager;
import java.awt.Color;
import java.util.Iterator;
import java.util.List;
import org.lazywizard.lazylib.combat.AIUtils;
import org.lwjgl.util.vector.Vector2f;

public class FDS_OverclockedSensorArrayScript extends BaseShipSystemScript {
   public static final float MAX_RANGE = 7500.0F;
   private static final float RECOIL_BONUS = 0.9F;
   private static final float RANGE_BONUS = 1.1F;
   private static final String SYSTEM_LOOP = "system_dampener_loop";
   private static final Color EFFECT_COLOR = new Color(255, 0, 0, 255);
   private boolean pointGiven = false;
   public static final float TIMER = 1.5F;
   private boolean add = true;
   private float level = 0.0F;

   public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
      CombatEngineAPI engine = Global.getCombatEngine();
      ShipAPI ship = (ShipAPI)stats.getEntity();
      if (ship != null) {
         CombatFleetManagerAPI fleetManager = engine.getFleetManager(ship.getOwner());
         if (fleetManager != null && !this.pointGiven) {
            fleetManager.getTaskManager(true).getCommandPointsStat().modifyFlat(id, 1.0F);
            fleetManager.getTaskManager(false).getCommandPointsStat().modifyFlat(id, 1.0F);
            this.pointGiven = true;
         }

         Global.getSoundPlayer().playLoop("system_dampener_loop", ship, 1.0F, 0.2F, ship.getLocation(), ship.getVelocity());
         if (this.add) {
            this.level += engine.getElapsedInLastFrame();
         } else {
            this.level -= engine.getElapsedInLastFrame();
         }

         if (this.level < 0.0F) {
            this.level = 0.0F;
            this.add = true;
         } else if (this.level > 1.5F) {
            this.level = 1.5F;
            this.add = false;
         }

         float opacity = 255.0F * (this.level / 1.5F);
         FDS_SpriteRenderManager.singleFrameRender(Global.getSettings().getSprite("misc", "fds_overclocked_sensor"), ship.getLocation(), new Vector2f(1000.0F, 1000.0F), ship.getFacing(), new Color(255, 0, 0, (int)opacity), true);
         this.applyBuff(ship);
         List<ShipAPI> ships = AIUtils.getAlliesOnMap(ship);
         Iterator i$ = ships.iterator();

         while(i$.hasNext()) {
            ShipAPI target = (ShipAPI)i$.next();
            this.applyBuff(target);
         }
      }

   }

   public void unapply(MutableShipStatsAPI stats, String id) {
      ShipAPI ship = (ShipAPI)stats.getEntity();
      this.unapplyBuff(ship);
      List<ShipAPI> ships = AIUtils.getAlliesOnMap(ship);
      Iterator i$ = ships.iterator();

      while(i$.hasNext()) {
         ShipAPI target = (ShipAPI)i$.next();
         this.unapplyBuff(target);
      }

      this.pointGiven = false;
   }

   private void applyBuff(ShipAPI target) {
      MutableShipStatsAPI stats = target.getMutableStats();
      stats.getSensorStrength().modifyPercent("fds_overclocked_sensors", 1.0F);
      stats.getMaxRecoilMult().modifyMult("fds_overclocked_sensors", 0.9F);
      stats.getBallisticWeaponRangeBonus().modifyMult("fds_overclocked_sensors", 1.1F);
      stats.getEnergyWeaponRangeBonus().modifyMult("fds_overclocked_sensors", 1.1F);
      stats.getBeamWeaponRangeBonus().modifyMult("fds_overclocked_sensors", 1.1F);
   }

   private void unapplyBuff(ShipAPI target) {
      MutableShipStatsAPI stats = target.getMutableStats();
      stats.getSensorStrength().unmodify("fds_overclocked_sensors");
      stats.getMaxRecoilMult().unmodify("fds_overclocked_sensors");
      stats.getBallisticWeaponRangeBonus().unmodify("fds_overclocked_sensors");
      stats.getEnergyWeaponRangeBonus().unmodify("fds_overclocked_sensors");
      stats.getBeamWeaponRangeBonus().unmodify("fds_overclocked_sensors");
   }

   public StatusData getStatusData(int index, State state, float effectLevel) {
      return index == 0 ? new StatusData("sensor range increased", false) : null;
   }
}
