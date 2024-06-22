package data.scripts.world;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.econ.EconomyAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import java.util.ArrayList;
import java.util.Iterator;

public class FDS_AddMarket {
   public static MarketAPI FDS_AddMarket(String factionID, SectorEntityToken primaryEntity, ArrayList<SectorEntityToken> connectedEntities, String name, int size, ArrayList<String> marketConditions, ArrayList<String> Industries, ArrayList<String> submarkets, float tariff) {
      EconomyAPI globalEconomy = Global.getSector().getEconomy();
      String planetID = primaryEntity.getId();
      MarketAPI newMarket = Global.getFactory().createMarket(planetID, name, size);
      newMarket.setFactionId(factionID);
      newMarket.setPrimaryEntity(primaryEntity);
      newMarket.getTariff().modifyFlat("generator", tariff);
      Iterator i$;
      String industry;
      if (null != submarkets) {
         i$ = submarkets.iterator();

         while(i$.hasNext()) {
            industry = (String)i$.next();
            newMarket.addSubmarket(industry);
         }
      }

      if (null != marketConditions) {
         i$ = marketConditions.iterator();

         while(i$.hasNext()) {
            industry = (String)i$.next();
            newMarket.addCondition(industry);
         }
      }

      if (null != Industries) {
         i$ = Industries.iterator();

         while(i$.hasNext()) {
            industry = (String)i$.next();
            newMarket.addIndustry(industry);
         }
      }

      SectorEntityToken entity;
      if (null != connectedEntities) {
         i$ = connectedEntities.iterator();

         while(i$.hasNext()) {
            entity = (SectorEntityToken)i$.next();
            newMarket.getConnectedEntities().add(entity);
         }
      }

      globalEconomy.addMarket(newMarket, true);
      primaryEntity.setMarket(newMarket);
      primaryEntity.setFaction(factionID);
      if (null != connectedEntities) {
         i$ = connectedEntities.iterator();

         while(i$.hasNext()) {
            entity = (SectorEntityToken)i$.next();
            entity.setMarket(newMarket);
            entity.setFaction(factionID);
         }
      }

      return newMarket;
   }
}
