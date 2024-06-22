package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class FDS_MaintenanceBots extends BaseHullMod {
   public static final float REPAIR_BONUS = 0.75F;
   public static final float DISABLED_BONUS = 0.5F;

   public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
      stats.getCombatEngineRepairTimeMult().modifyMult(id, 0.75F);
      stats.getCombatWeaponRepairTimeMult().modifyMult(id, 0.75F);
      stats.getOverloadTimeMod().modifyMult(id, 0.5F);
   }

   public String getDescriptionParam(int index, HullSize hullSize) {
      if (index == 0) {
         return "25";
      } else {
         return index == 1 ? "50" : null;
      }
   }
}
