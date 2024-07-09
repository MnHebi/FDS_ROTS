package data.scripts.campaign.econ;

import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.util.Pair;

public class FDS_MaintenanceBotsFactory extends BaseIndustry {
   public void apply() {
      super.apply(true);
      int size = this.market.getSize();

      int OUTPUT = size + 1;

      this.demand("heavy_machinery", size - 1);
      this.demand("volatiles", size - 2);
      this.demand("metals", size + 1);
      this.demand("rare_metals", size - 2);
      this.supply("maintenance_bots", OUTPUT);
      Pair<String, Integer> deficit = this.getMaxDeficit(new String[]{"heavy_machinery", "metals", "rare_metals"});
      this.applyDeficitToProduction(1, deficit, new String[]{"maintenance_bots"});
      if (!this.isFunctional()) {
         this.supply.clear();
      }

   }

   public void unapply() {
      super.unapply();
   }
}
