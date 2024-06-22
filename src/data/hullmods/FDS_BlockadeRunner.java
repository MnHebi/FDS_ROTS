package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class FDS_BlockadeRunner extends BaseHullMod {
   private static final float HANDLING_BONUS = 1.1F;
   private static final float ZEROFLUX_MULT = 0.2F;

   public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
      stats.getMaxSpeed().modifyMult(id, 1.1F);
      stats.getAcceleration().modifyMult(id, 1.1F);
      stats.getMaxTurnRate().modifyMult(id, 1.1F);
      stats.getTurnAcceleration().modifyMult(id, 1.1F);
      stats.getZeroFluxMinimumFluxLevel().modifyFlat(id, 0.2F);
   }

   public String getDescriptionParam(int index, HullSize hullSize) {
      if (index == 0) {
         return "10";
      } else {
         return index == 1 ? "20" : null;
      }
   }
}
