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

public class FDS_HypatiaSystem implements SectorGeneratorPlugin {
   public void generate(SectorAPI sector) {
      StarSystemAPI system = sector.createStarSystem("Hypatia");
      system.setBackgroundTextureFilename("graphics/backgrounds/background4.jpg");
      PlanetAPI star = system.initStar("hypatiaStar", "star_orange", 400.0F, 200.0F);
      star.setName("Hypatia");
      system.setLightColor(new Color(250, 255, 250));
      system.getLocation().set(7000.0F, 8000.0F);
      SectorEntityToken hypatiaNebula = Misc.addNebulaFromPNG("data/campaign/terrain/fds_hypatia_nebula.png", 0.0F, 0.0F, system, "terrain", "nebula", 4, 4, StarAge.AVERAGE);
      PlanetAPI inferna = system.addPlanet("infernaPlanet", star, "Inferna", "lava", (float)Math.random() * 360.0F, 100.0F, 2200.0F, 100.0F);
      Misc.initConditionMarket(inferna);
      inferna.getMarket().addCondition("very_hot");
      inferna.getMarket().addCondition("tectonic_activity");
      inferna.getMarket().addCondition("ore_ultrarich");
      PlanetAPI vulnere = system.addPlanet("vulnerePlanet", star, "Vulnere", "desert", (float)Math.random() * 360.0F, 150.0F, 2900.0F, 170.0F);
      Misc.initConditionMarket(vulnere);
      vulnere.getMarket().addCondition("arid");
      vulnere.getMarket().addCondition("rare_ore_moderate");
      vulnere.getMarket().addCondition("farmland_poor");
      vulnere.getMarket().addCondition("desert");
      vulnere.getMarket().addCondition("hot");
      system.addAsteroidBelt(star, 90, 3650.0F, 400.0F, 50.0F, 200.0F, "asteroid_belt", "Fury's Belt");
      system.addRingBand(star, "misc", "rings_dust0", 256.0F, 3, Color.white, 256.0F, 3600.0F, 205.0F, (String)null, (String)null);
      system.addRingBand(star, "misc", "rings_asteroids0", 256.0F, 3, Color.white, 256.0F, 3720.0F, 195.0F, (String)null, (String)null);
      PlanetAPI ira = system.addPlanet("iraPlanet", star, "Ira", "gas_giant", (float)Math.random() * 360.0F, 500.0F, 5100.0F, 225.0F);
      system.addRingBand(ira, "misc", "rings_ice0", 256.0F, 2, Color.white, 256.0F, 1200.0F, 45.0F, "ring", (String)null);
      PlanetAPI ultor = system.addPlanet("ultorPlanet", ira, "Ultor", "fds_tundra", (float)Math.random() * 360.0F, 120.0F, 800.0F, 30.0F);
      FDS_AddMarket.FDS_AddMarket("fringe_defence_syndicate", ultor, (ArrayList)null, "Ultor", 4, new ArrayList(Arrays.asList("habitable", "population_3", "ore_moderate", "organics_trace")), new ArrayList(Arrays.asList("grounddefenses", "spaceport", "mining", "refining", "lightindustry", "heavyindustry", "militarybase", "population")), new ArrayList(Arrays.asList("black_market", "open_market", "generic_military")), 0.35F);
      SectorEntityToken hypatiaRelay = system.addCustomEntity("hypatiaRelay", "Hypatia Relay", "comm_relay", "fringe_defence_syndicate");
      hypatiaRelay.setCircularOrbitPointingDown(star, 240.0F, 7600.0F, 310.0F);
      PlanetAPI arbitrium = system.addPlanet("arbitriumPlanet", star, "Arbitrium", "fds_frozen", (float)Math.random() * 360.0F, 116.0F, 9600.0F, 400.0F);
      arbitrium.setFaction("fringe_defence_syndicate");
      FDS_AddMarket.FDS_AddMarket("fringe_defence_syndicate", arbitrium, (ArrayList)null, "Arbitrium", 3, new ArrayList(Arrays.asList("volatiles_abundant", "cold", "population_1", "rare_ore_ultrarich", "ice", "fds_crystal_caves")), new ArrayList(Arrays.asList("grounddefenses", "patrolhq", "spaceport", "mining", "lightindustry", "population", "fds_fuel_converter")), new ArrayList(Arrays.asList("black_market", "storage", "open_market", "generic_military")), 0.35F);
      JumpPointAPI arbitriumJumpPoint = Global.getFactory().createJumpPoint("arbitriumJumpPoint", "Arbitrium Jump-Point");
      OrbitAPI orbit = Global.getFactory().createCircularOrbit(arbitrium, arbitrium.getCircularOrbitAngle() - 45.0F, 9000.0F, 400.0F);
      arbitriumJumpPoint.setOrbit(orbit);
      arbitriumJumpPoint.setRelatedPlanet(arbitrium);
      arbitriumJumpPoint.setStandardWormholeToHyperspaceVisual();
      system.addEntity(arbitriumJumpPoint);
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
