package data.scripts.campaign.econ;

import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.impl.campaign.econ.BaseMarketConditionPlugin;

public class FDS_CrystalCaves extends BaseMarketConditionPlugin {
   public void apply(String id) {
      super.apply(id);
      int size = this.market.getSize();
      Industry industry = this.market.getIndustry("mining");
      if (industry != null) {
         if (industry.isFunctional()) {
            industry.supply(id, "energy_crystals", size - 2, "Base value for colony size");
         } else {
            industry.getSupply("energy_crystals").getQuantity().unmodifyFlat(id);
         }

      }
   }

   public void unapply(String id) {
      super.unapply(id);
   }
}
