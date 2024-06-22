package data.scripts.campaign.econ;

import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.util.Pair;

public class FDS_FuelConverter extends BaseIndustry {
   public void apply() {
      super.apply(true);
      int size = this.market.getSize();
      this.demand("energy_crystals", size - 1);
      this.supply("fuel", size - 1);
      Pair<String, Integer> deficit = this.getMaxDeficit(new String[]{"energy_crystals"});
      this.applyDeficitToProduction(1, deficit, new String[]{"fuel"});
      if (!this.isFunctional()) {
         this.supply.clear();
      }

   }

   public void unapply() {
      super.unapply();
   }

   public boolean isAvailableToBuild() {
      if (!super.isAvailableToBuild()) {
         return false;
      } else {
         return !this.market.hasIndustry("fuelprod");
      }
   }

   public String getUnavailableReason() {
      return !super.isAvailableToBuild() ? super.getUnavailableReason() : "Mutually exclusive with the Fuel Production industry";
   }
}
