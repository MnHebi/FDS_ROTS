package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class FDS_SyndicateCombatSystems extends BaseHullMod {
   private static final float TURN_BONUS = 20.0F;
   private static final float RECOIL_BONUS = 25.0F;
   private static final Set<String> BLOCKED_HULLMODS = new HashSet(1);
   private String ERROR = "FDSIncompatibleHullmodWarning";
   private static Map hulls = new HashMap();

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
         return ship.getVariant().getHullMods().contains("turretgyros") ? "Incompatible with Advanced Turret Gyros" : null;
      }
   }

   public boolean isApplicableToShip(ShipAPI ship) {
      return ship.getHullSpec().getHullId().startsWith("fds_") && !ship.getVariant().getHullMods().contains("turretgyros");
   }

   public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
      stats.getEnergyWeaponRangeBonus().modifyPercent(id, (Float)hulls.get(hullSize));
      stats.getWeaponTurnRateBonus().modifyPercent(id, 20.0F);
      stats.getBeamWeaponTurnRateBonus().modifyPercent(id, 20.0F);
      stats.getRecoilDecayMult().modifyPercent(id, 25.0F);
   }

   public String getDescriptionParam(int index, HullSize hullSize) {
      if (index == 0) {
         return "20";
      } else if (index == 1) {
         return "25";
      } else if (index == 2) {
         return "5/10/15/20";
      } else if (index == 3) {
         return "2.5";
      } else {
         return index == 4 ? "50" : null;
      }
   }

   static {
      hulls.put(HullSize.FIGHTER, 0.0F);
      hulls.put(HullSize.FRIGATE, 5.0F);
      hulls.put(HullSize.DESTROYER, 10.0F);
      hulls.put(HullSize.CRUISER, 15.0F);
      hulls.put(HullSize.CAPITAL_SHIP, 20.0F);
      BLOCKED_HULLMODS.add("turretgyros");
   }
}
