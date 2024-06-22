package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.WeaponAPI.WeaponSize;
import com.fs.starfarer.api.combat.WeaponAPI.WeaponType;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class FDS_PointDefenceMatrix extends BaseHullMod {
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

      List weapons = ship.getAllWeapons();
      Iterator iter = weapons.iterator();

      while(true) {
         WeaponAPI weapon;
         do {
            if (!iter.hasNext()) {
               return;
            }

            weapon = (WeaponAPI)iter.next();
         } while(weapon.getSize() != WeaponSize.SMALL && weapon.getSize() != WeaponSize.MEDIUM);

         if (weapon.getType() != WeaponType.MISSILE) {
            weapon.setPD(true);
         }
      }
   }

   public String getUnapplicableReason(ShipAPI ship) {
      if (!ship.getHullSpec().getHullId().startsWith("fds_atonement")) {
         return "Can only be installed on the Atonement-class Cruiser";
      } else {
         return ship.getVariant().getHullMods().contains("pointdefenseai") ? "Incompatible with Integrated Point Defense AI" : null;
      }
   }

   public boolean isApplicableToShip(ShipAPI ship) {
      return !ship.getVariant().getHullMods().contains("pointdefenseai") && ship.getHullSpec().getHullId().startsWith("fds_atonement");
   }

   public String getDescriptionParam(int index, HullSize hullSize) {
      return null;
   }

   static {
      BLOCKED_HULLMODS.add("pointdefenseai");
   }
}
