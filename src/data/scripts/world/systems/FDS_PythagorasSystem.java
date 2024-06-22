package data.scripts.world.systems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.JumpPointAPI;
import com.fs.starfarer.api.campaign.OrbitAPI;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.SectorGeneratorPlugin;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.impl.campaign.DerelictShipEntityPlugin.DerelictShipData;
import com.fs.starfarer.api.impl.campaign.DerelictShipEntityPlugin.DerelictType;
import com.fs.starfarer.api.impl.campaign.procgen.NebulaEditor;
import com.fs.starfarer.api.impl.campaign.procgen.StarAge;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator.StarSystemType;
import com.fs.starfarer.api.impl.campaign.procgen.themes.BaseThemeGenerator;
import com.fs.starfarer.api.impl.campaign.procgen.themes.SalvageSpecialAssigner.ShipRecoverySpecialCreator;
import com.fs.starfarer.api.impl.campaign.procgen.themes.SalvageSpecialAssigner.SpecialCreationContext;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.special.ShipRecoverySpecial.PerShipData;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.special.ShipRecoverySpecial.ShipCondition;
import com.fs.starfarer.api.impl.campaign.terrain.HyperspaceTerrainPlugin;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.WeightedRandomPicker;
import data.scripts.world.FDS_AddMarket;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class FDS_PythagorasSystem implements SectorGeneratorPlugin {
   public void generate(SectorAPI sector) {
      StarSystemAPI system = sector.createStarSystem("Pythagoras");
      system.setBackgroundTextureFilename("graphics/backgrounds/background4.jpg");
      system.setType(StarSystemType.BINARY_CLOSE);
      PlanetAPI pythagoras1 = system.initStar("pythagoras1Star", "star_yellow", 780.0F, 500.0F, 6.0F, 1.0F, 1.0F);
      pythagoras1.setName("Pythagoras I");
      PlanetAPI pythagoras2 = system.addPlanet("pythagoras2Star", pythagoras1, "Pythagoras II", "star_orange", 10.0F, 500.0F, 2000.0F, 1000.0F);
      system.setSecondary(pythagoras2);
      system.addCorona(pythagoras2, 300.0F, 6.0F, 0.0F, 1.0F);
      system.setLightColor(new Color(255, 250, 250));
      system.getLocation().set(10000.0F, 12000.0F);
      PlanetAPI odium = system.addPlanet("odiumPlanet", pythagoras1, "Odium", "fds_desert", 25.0F, 104.0F, 4000.0F, 100.0F);
      odium.setFaction("independent");
      FDS_AddMarket.FDS_AddMarket("independent", odium, (ArrayList)null, "Odium", 3, new ArrayList(Arrays.asList("farmland_poor", "rare_ore_moderate", "volatiles_plentiful", "population_4", "vice_demand", "hot", "organized_crime", "arid")), new ArrayList(Arrays.asList("refining", "spaceport", "farming", "fuelprod", "mining", "population")), new ArrayList(Arrays.asList("storage", "black_market", "open_market")), 0.25F);

      JumpPointAPI odiumJumpPoint = Global.getFactory().createJumpPoint("odiumJumpPoint", "Odium Jump-Point");
      OrbitAPI orbit = Global.getFactory().createCircularOrbit(pythagoras1, 70.0F, 4000.0F, 100.0F);
      odiumJumpPoint.setOrbit(orbit);
      odiumJumpPoint.setRelatedPlanet(odium);
      odiumJumpPoint.setStandardWormholeToHyperspaceVisual();
      system.addEntity(odiumJumpPoint);
      PlanetAPI timor = system.addPlanet("timorPlanet", pythagoras1, "Timor", "gas_giant", 130.0F, 280.0F, 5500.0F, 150.0F);
      SectorEntityToken timorStation = system.addCustomEntity("timorStation", "Timor Pirate Base", "station_pirate_type", "pirates");
      timorStation.setCircularOrbitWithSpin(timor, 135.0F, 350.0F, 250.0F, 3.0F, 5.0F);
      timorStation.setInteractionImage("illustrations", "orbital");
      FDS_AddMarket.FDS_AddMarket("pirates", timorStation, (ArrayList)null, "Timor Pirate Base", 4, new ArrayList(Arrays.asList("population_4", "free_market", "vice_demand", "organized_crime")), new ArrayList(Arrays.asList("orbitalstation", "spaceport", "militarybase", "population")), new ArrayList(Arrays.asList("black_market", "open_market", "storage")), 0.3F);
      system.addPlanet("patiensPlanet", pythagoras1, "Patiens", "ice_giant", 290.0F, 200.0F, 8200.0F, 210.0F);
      StarSystemGenerator.addSystemwideNebula(system, StarAge.OLD);
      system.autogenerateHyperspaceJumpPoints(true, true, true);
      this.cleanup(system);
   }

   void cleanup(StarSystemAPI system) {
      HyperspaceTerrainPlugin plugin = (HyperspaceTerrainPlugin)Misc.getHyperspaceTerrain().getPlugin();
      NebulaEditor editor = new NebulaEditor(plugin);
      float minRadius = plugin.getTileSize() * 2.0F;
      float radius = system.getMaxRadiusInHyperspace();
      editor.clearArc(system.getLocation().x, system.getLocation().y, 0.0F, radius + minRadius * 0.5F, 0.0F, 360.0F);
      editor.clearArc(system.getLocation().x, system.getLocation().y, 0.0F, radius + minRadius, 0.0F, 360.0F, 0.25F);
   }

   protected void addDerelict(StarSystemAPI system, SectorEntityToken focus, String variantId, ShipCondition condition, float orbitRadius, boolean recoverable) {
      DerelictShipData params = new DerelictShipData(new PerShipData(variantId, condition), false);
      SectorEntityToken ship = BaseThemeGenerator.addSalvageEntity(system, "wreck", "neutral", params);
      ship.setDiscoverable(true);
      float orbitDays = orbitRadius / (10.0F + (float)Math.random() * 5.0F);
      ship.setCircularOrbit(focus, (float)Math.random() * 360.0F, orbitRadius, orbitDays);
      if (recoverable) {
         ShipRecoverySpecialCreator creator = new ShipRecoverySpecialCreator((Random)null, 0, 0, false, (DerelictType)null, (WeightedRandomPicker)null);
         Misc.setSalvageSpecial(ship, creator.createSpecial(ship, (SpecialCreationContext)null));
      }

   }
}
