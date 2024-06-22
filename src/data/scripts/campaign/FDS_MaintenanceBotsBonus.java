package data.scripts.campaign;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.BuffManagerAPI.Buff;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FDS_MaintenanceBotsBonus implements EveryFrameScript {
   private static final float INTERVAL = 0.5F;
   private static final float CR_PERCENT = 50.0F;
   private static final float SUPPLY_PERCENT = -20.0F;
   private final List<CampaignFleetAPI> fleets = new ArrayList(500);
   private final IntervalUtil interval = new IntervalUtil(0.5F, 0.5F);

   public void advance(float amount) {
         this.interval.advance(amount);
         if (this.interval.intervalElapsed()) {
            this.fleets.clear();
            this.fleets.addAll(Global.getSector().getHyperspace().getFleets());
            List<StarSystemAPI> systems = Global.getSector().getStarSystems();
            Iterator i$ = systems.iterator();

            while(i$.hasNext()) {
               StarSystemAPI system = (StarSystemAPI)i$.next();
               this.fleets.addAll(system.getFleets());
            }

            i$ = this.fleets.iterator();

            while(true) {
               CampaignFleetAPI fleet;
               do {
                  do {
                     if (!i$.hasNext()) {
                        return;
                     }

                     fleet = (CampaignFleetAPI)i$.next();
                  } while(fleet.isAIMode());
               } while(!fleet.isAlive());

               float bots = fleet.getCargo().getCommodityQuantity("maintenance_bots");
               float suppliesPerMonth = 0.0F;
               i$ = fleet.getFleetData().getMembersListCopy().iterator();

               while(i$.hasNext()) {
                  FleetMemberAPI member = (FleetMemberAPI)i$.next();
                  if (!member.isMothballed()) {
                     suppliesPerMonth += member.getStats().getSuppliesPerMonth().getModifiedValue();
                  }
               }

               float totalRecoveryBonus = bots / 2.0F / suppliesPerMonth;
               float recoveryPct = 0.0F;
               float supplyPct = 0.0F;
               if (totalRecoveryBonus > 1.0F) {
                  recoveryPct = 50.0F;
                  supplyPct = -20.0F;
               } else if (totalRecoveryBonus < 0.0F) {
                  recoveryPct = 0.0F;
                  supplyPct = 0.0F;
               } else {
                  recoveryPct = totalRecoveryBonus * 50.0F;
                  supplyPct = totalRecoveryBonus * -20.0F;
               }

               i$ = fleet.getFleetData().getMembersListCopy().iterator();

               while(i$.hasNext()) {
                  FleetMemberAPI member = (FleetMemberAPI)i$.next();
                  Buff buff = member.getBuffManager().getBuff("fds_maintenanceBotsBonus");
                  if (buff instanceof FDS_MaintenanceBotsBonus.FDS_MaintenanceBotsBuff) {
                     if (recoveryPct > 0.0F) {
                        FDS_MaintenanceBotsBonus.FDS_MaintenanceBotsBuff crewBonusBuff = (FDS_MaintenanceBotsBonus.FDS_MaintenanceBotsBuff)buff;
                        crewBonusBuff.setBuffAmount(recoveryPct, supplyPct, member);
                     } else {
                        member.getBuffManager().removeBuff("fds_maintenanceBotsBonus");
                     }
                  } else if (recoveryPct > 0.0F) {
                     member.getBuffManager().addBuff(new FDS_MaintenanceBotsBonus.FDS_MaintenanceBotsBuff(recoveryPct, supplyPct));
                  }
               }
            }
         }
   }

   public boolean isDone() {
      return false;
   }

   public boolean runWhilePaused() {
      return true;
   }

   private static class FDS_MaintenanceBotsBuff implements Buff {
      private float crBuffAmount;
      private float supplyBuffAmount;
      private boolean expired = false;
      private final IntervalUtil interval = new IntervalUtil(1.0F, 1.0F);
      private transient FleetMemberAPI lastMember = null;

      FDS_MaintenanceBotsBuff(float crBuffAmount, float supplyBuffAmount) {
         this.crBuffAmount = crBuffAmount;
         this.supplyBuffAmount = supplyBuffAmount;
      }

      public void advance(float days) {
         this.interval.advance(days);
         if (this.interval.intervalElapsed() && this.lastMember != null && (this.lastMember.getFleetData() == null || this.lastMember.getFleetData().getFleet() == null || this.lastMember.getFleetData().getFleet().isAIMode() || !this.lastMember.getFleetData().getFleet().isAlive())) {
            this.expired = true;
         }

      }

      public void apply(FleetMemberAPI member) {
         this.lastMember = member;
         member.getStats().getBaseCRRecoveryRatePercentPerDay().modifyPercent("fds_maintenanceBotsBonus", this.crBuffAmount);
         member.getStats().getRepairRatePercentPerDay().modifyPercent("fds_maintenanceBotsBonus", this.crBuffAmount);
         member.getStats().getSuppliesPerMonth().modifyPercent("fds_maintenanceBotsBonus", this.supplyBuffAmount);
      }

      public String getId() {
         return "fds_maintenanceBotsBonus";
      }

      public boolean isExpired() {
         return this.expired;
      }

      void setBuffAmount(float crBuffAmount, float supplyBuffAmount, FleetMemberAPI member) {
         if (crBuffAmount != this.crBuffAmount || supplyBuffAmount != this.supplyBuffAmount) {
            this.apply(member);
         }

         this.crBuffAmount = crBuffAmount;
         this.supplyBuffAmount = supplyBuffAmount;
      }
   }
}
