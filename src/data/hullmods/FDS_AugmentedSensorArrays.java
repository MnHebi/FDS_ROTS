package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class FDS_AugmentedSensorArrays extends BaseHullMod {
   public static final float RANGE_BONUS = 20.0F;
   private static final Set<String> BLOCKED_HULLMODS = new HashSet(1);
   private String ERROR = "FDSIncompatibleHullmodWarning";

   public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
      Iterator i$ = BLOCKED_HULLMODS.iterator();

      while(i$.hasNext()) {
         String tmp = (String)i$.next();
         if (ship.getVariant().getHullMods().contains(tmp)) {
            ship.getVariant().removeMod(tmp);
            ship.getVariant().addMod(this.ERROR);
         }
      }

   }

   public String getUnapplicableReason(ShipAPI ship) {
      if (!ship.getHullSpec().getHullId().startsWith("fds_")) {
         return "Must be installed on a FDS ship";
      } else {
         return ship.getVariant().getHullMods().contains("hiressensors") ? "Incompatible with High Resolution Sensors" : null;
      }
   }

   public boolean isApplicableToShip(ShipAPI ship) {
      return ship.getHullSpec().getHullId().startsWith("fds_") && !ship.getVariant().getHullMods().contains("hiressensors");
   }

   public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
      stats.getSensorStrength().modifyPercent(id, 20.0F);
   }

   public String getDescriptionParam(int index, HullSize hullSize) {
      return index == 0 ? "20" : null;
   }

   static {
      BLOCKED_HULLMODS.add("hiressensors");
   }
}
