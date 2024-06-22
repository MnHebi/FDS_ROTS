package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class FDSIncompatibleHullmodWarning extends BaseHullMod {
   public String getDescriptionParam(int index, HullSize hullSize) {
      return index == 0 ? "WARNING" : null;
   }
}
