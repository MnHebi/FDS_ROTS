package data.scripts.campaign.econ;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.CommoditySpecAPI;
import com.fs.starfarer.api.campaign.econ.Industry.AICoreDescriptionMode;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import java.awt.Color;

public class FDS_RepairYards extends BaseIndustry {
   public static float QUALITY_BONUS = 0.25F;

   public void apply() {
      super.apply(true);
      int size = this.market.getSize();

      //increase production if production 0
      int OUTPUT = 3;
      int ZERO_PROTECTION = size - OUTPUT;
      if (ZERO_PROTECTION <= 0)
      {
         OUTPUT = 1;
      }
      else
      {
         OUTPUT = ZERO_PROTECTION;
      }

      this.demand("maintenance_bots", size - 1);
      this.demand("supplies", size - 2);
      this.demand("heavy_machinery", size - 2);
      this.supply("ships", OUTPUT);
      Pair<String, Integer> deficit = this.getMaxDeficit(new String[]{"maintenance_bots", "heavy_machinery", "supplies"});
      float bonus = QUALITY_BONUS;
      if ((Integer)deficit.two > 0) {
         float total = (float)(3 * size - 5);
         bonus *= 1.0F - (float)(Integer)deficit.two / total;
      }

      this.market.getStats().getDynamic().getMod(Stats.PRODUCTION_QUALITY_MOD).modifyMult(this.getModId(), 1.0F + bonus, this.getNameForModifier());
      this.market.getStats().getDynamic().getMod(Stats.FLEET_QUALITY_MOD).modifyMult(this.getModId(), 1.0F + bonus, this.getNameForModifier());
      if (!this.isFunctional()) {
         this.supply.clear();
      }

   }

   public void unapply() {
      super.unapply();
      this.market.getStats().getDynamic().getMod("production_quality_mod").unmodifyMult(this.getModId());
      this.market.getStats().getDynamic().getMod("fleet_quality_mod").unmodifyMult(this.getModId());
   }

   @Override
   protected void addPostDemandSection(TooltipMakerAPI tooltip, boolean hasDemand, IndustryTooltipMode mode) {
      if (mode != IndustryTooltipMode.NORMAL || isFunctional()) {
         float SHIP_QUALITY_BONUS = 0.25f;

         float total = SHIP_QUALITY_BONUS;
         String totalStr = "+" + (int)Math.round(total * 100f) + "%";
         Color h = Misc.getHighlightColor();
         if (total < 0) {
            h = Misc.getNegativeHighlightColor();
            totalStr = "" + (int)Math.round(total * 100f) + "%";
         }
         float opad = 10f;
         if (total >= 0) {
            tooltip.addPara("Ship quality: %s", opad, h, totalStr);
            tooltip.addPara("*Quality bonus only applies for the largest ship producer in the faction.",
                    Misc.getGrayColor(), opad);
         }
      }
   }

   protected void applyAlphaCoreModifiers() {
   }

   protected void applyNoAICoreModifiers() {
   }

   protected void applyAlphaCoreSupplyAndDemandModifiers() {
      this.demandReduction.modifyFlat(this.getModId(0), (float)DEMAND_REDUCTION, "Alpha core");
   }

   protected void addAlphaCoreDescription(TooltipMakerAPI tooltip, AICoreDescriptionMode mode) {
      float opad = 10.0F;
      Color highlight = Misc.getHighlightColor();
      String pre = "Alpha-level AI core currently assigned. ";
      if (mode == AICoreDescriptionMode.MANAGE_CORE_DIALOG_LIST || mode == AICoreDescriptionMode.INDUSTRY_TOOLTIP) {
         pre = "Alpha-level AI core. ";
      }

      if (mode == AICoreDescriptionMode.INDUSTRY_TOOLTIP) {
         CommoditySpecAPI coreSpec = Global.getSettings().getCommoditySpec(this.aiCoreId);
         TooltipMakerAPI text = tooltip.beginImageWithText(coreSpec.getIconName(), 48.0F);
         text.addPara(pre + "Reduces upkeep cost by %s. Reduces demand by %s unit.", opad, highlight, new String[]{"" + (int)((1.0F - UPKEEP_MULT) * 100.0F) + "%", "" + DEMAND_REDUCTION});
         tooltip.addImageWithText(opad);
      } else {
         tooltip.addPara(pre + "Reduces upkeep cost by %s. Reduces demand by %s unit.", opad, highlight, new String[]{"" + (int)((1.0F - UPKEEP_MULT) * 100.0F) + "%", "" + DEMAND_REDUCTION});
      }
   }

   public boolean isAvailableToBuild() {
      return this.market.hasIndustry("heavyindustry") || this.market.hasIndustry("orbitalworks");
   }

   public String getUnavailableReason() {
      return "Requires Heavy Industry or Orbital Works";
   }
}
