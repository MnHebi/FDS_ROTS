package data.scripts.world.systems;

import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.SectorGeneratorPlugin;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.impl.campaign.DerelictShipEntityPlugin.DerelictShipData;
import com.fs.starfarer.api.impl.campaign.DerelictShipEntityPlugin.DerelictType;
import com.fs.starfarer.api.impl.campaign.procgen.NebulaEditor;
import com.fs.starfarer.api.impl.campaign.procgen.StarAge;
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

public class FDS_EuclidSystem implements SectorGeneratorPlugin {
   public void generate(SectorAPI sector) {
      StarSystemAPI system = sector.createStarSystem("Euclid");
      system.setBackgroundTextureFilename("graphics/backgrounds/background4.jpg");
      PlanetAPI star = system.initStar("euclidStar", "star_blue_supergiant", 1200.0F, 900.0F, 10.0F, 1.0F, 5.0F);
      system.setLightColor(new Color(240, 240, 255));
      star.setName("Euclid");
      system.getLocation().set(3000.0F, 7000.0F);
      SectorEntityToken euclidNebula = Misc.addNebulaFromPNG("data/campaign/terrain/fds_euclid_nebula.png", 0.0F, 0.0F, system, "terrain", "nebula_blue", 4, 4, StarAge.AVERAGE);
      PlanetAPI dolor = system.addPlanet("dolorPlanet", star, "Dolor", "fds_lava", 300.0F, 50.0F, 3000.0F, 100.0F);
      Misc.initConditionMarket(dolor);
      dolor.getMarket().addCondition("extreme_tectonic_activity");
      dolor.getMarket().addCondition("very_hot");
      dolor.getMarket().addCondition("meteor_impacts");
      dolor.getMarket().addCondition("ore_abundant");
      dolor.getMarket().addCondition("volatiles_diffuse");
      PlanetAPI angor = system.addPlanet("angorPlanet", star, "Angor", "toxic", 240.0F, 30.0F, 4000.0F, 150.0F);
      Misc.initConditionMarket(angor);
      angor.getMarket().addCondition("hot");
      angor.getMarket().addCondition("irradiated");
      angor.getMarket().addCondition("toxic_atmosphere");
      angor.getMarket().addCondition("inimical_biosphere");
      angor.getMarket().addCondition("dense_atmosphere");
      angor.getMarket().addCondition("extreme_weather");
      angor.getMarket().addCondition("volatiles_plentiful");
      angor.getMarket().addCondition("organics_plentiful");
      PlanetAPI rabidus = system.addPlanet("rabidusPlanet", star, "Rabidus", "rocky_metallic", 120.0F, 40.0F, 7000.0F, 200.0F);
      Misc.initConditionMarket(rabidus);
      rabidus.getMarket().addCondition("hot");
      rabidus.getMarket().addCondition("volatiles_trace");
      rabidus.getMarket().addCondition("ore_ultrarich");
      rabidus.getMarket().addCondition("rare_ore_abundant");
      rabidus.getMarket().addCondition("no_atmosphere");
      PlanetAPI insanus = system.addPlanet("insanusPlanet", star, "Insanus", "fds_gas_giant", 180.0F, 320.0F, 9000.0F, 300.0F);
      system.addRingBand(insanus, "misc", "rings_ice0", 256.0F, 1, Color.white, 256.0F, 700.0F, 45.0F, "ring", (String)null);
      PlanetAPI insanus1 = system.addPlanet("insanus1Planet", insanus, "Insanus 1", "lava_minor", 120.0F, 30.0F, 450.0F, 30.0F);
      Misc.initConditionMarket(insanus1);
      insanus1.getMarket().addCondition("very_hot");
      insanus1.getMarket().addCondition("tectonic_activity");
      insanus1.getMarket().addCondition("ore_rich");
      insanus1.getMarket().addCondition("rare_ore_sparse");
      insanus1.getMarket().addCondition("dense_atmosphere");
      PlanetAPI insanus2 = system.addPlanet("insanus2Planet", insanus, "Insanus 2", "barren-bombarded", 10.0F, 40.0F, 600.0F, 40.0F);
      Misc.initConditionMarket(insanus2);
      insanus2.getMarket().addCondition("ruins_scattered");
      insanus2.getMarket().addCondition("meteor_impacts");
      insanus2.getMarket().addCondition("rare_ore_rich");
      insanus2.getMarket().addCondition("rare_ore_moderate");
      insanus2.getMarket().addCondition("no_atmosphere");
      PlanetAPI insanus3 = system.addPlanet("insanus3Planet", insanus, "Insanus 3", "tundra", 90.0F, 38.0F, 1000.0F, 70.0F);
      Misc.initConditionMarket(insanus3);
      insanus3.getMarket().addCondition("ruins_vast");
      insanus3.getMarket().addCondition("cold");
      insanus3.getMarket().addCondition("volatiles_abundant");
      insanus3.getMarket().addCondition("farmland_poor");
      insanus3.getMarket().addCondition("habitable");
      PlanetAPI delirus = system.addPlanet("delirusPlanet", star, "Delirus", "ice_giant", 0.0F, 250.0F, 12000.0F, 400.0F);
      system.addRingBand(delirus, "misc", "rings_ice0", 256.0F, 3, Color.white, 300.0F, 800.0F, 45.0F, "ring", (String)null);
      PlanetAPI vindicta = system.addPlanet("vindictaPlanet", delirus, "Vindicta", "fds_tundra", 25.0F, 72.0F, 1200.0F, 60.0F);
      system.addAsteroidBelt(vindicta, 20, 600.0F, 100.0F, 500.0F, 600.0F);
      system.addRingBand(vindicta, "misc", "rings_asteroids0", 256.0F, 0, Color.white, 120.0F, 600.0F, 550.0F);
      vindicta.setFaction("fringe_defence_syndicate");
      FDS_AddMarket.FDS_AddMarket("fringe_defence_syndicate", vindicta, (ArrayList)null, "Vindicta", 5, new ArrayList(Arrays.asList("farmland_poor", "habitable", "population_3", "cold", "ore_moderate", "poor_light", "outpost")), new ArrayList(Arrays.asList("lightindustry", "heavyindustry", "spaceport", "farming", "mining", "patrolhq", "population", "fds_repair_yards")), new ArrayList(Arrays.asList("storage", "black_market", "open_market", "generic_military")), 0.3F);
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
