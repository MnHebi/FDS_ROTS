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

public class FDS_ArchimedesSystem implements SectorGeneratorPlugin {
   public void generate(SectorAPI sector) {
      StarSystemAPI system = sector.createStarSystem("Archimedes");
      system.setBackgroundTextureFilename("graphics/backgrounds/background4.jpg");
      PlanetAPI star = system.initStar("archimedesStar", "star_blue_giant", 900.0F, 700.0F, 10.0F, 1.0F, 4.0F);
      star.setName("Archimedes");
      system.setLightColor(new Color(240, 240, 255));
      system.getLocation().set(12000.0F, 7000.0F);
      SectorEntityToken archimedesNebula = Misc.addNebulaFromPNG("data/campaign/terrain/fds_archimedes_nebula.png", 0.0F, 0.0F, system, "terrain", "nebula_amber", 4, 4, StarAge.OLD);
      PlanetAPI exitium = system.addPlanet("exitiumPlanet", star, "Exitium", "fds_lava", 0.0F, 60.0F, 2300.0F, 60.0F);
      Misc.initConditionMarket(exitium);
      exitium.getMarket().addCondition("tectonic_activity");
      exitium.getMarket().addCondition("dense_atmosphere");
      exitium.getMarket().addCondition("very_hot");
      exitium.getMarket().addCondition("ore_rich");
      exitium.getMarket().addCondition("rare_ore_sparse");
      PlanetAPI desolatio = system.addPlanet("desolatioPlanet", star, "Desolatio", "fds_desert", 90.0F, 50.0F, 3200.0F, 130.0F);
      Misc.initConditionMarket(desolatio);
      desolatio.getMarket().addCondition("arid");
      desolatio.getMarket().addCondition("volatiles_trace");
      desolatio.getMarket().addCondition("farmland_poor");
      desolatio.getMarket().addCondition("desert");
      PlanetAPI caries = system.addPlanet("cariesPlanet", star, "Caries", "fds_cryovolcanic", 270.0F, 65.0F, 4000.0F, 200.0F);
      Misc.initConditionMarket(caries);
      caries.getMarket().addCondition("irradiated");
      caries.getMarket().addCondition("toxic_atmosphere");
      caries.getMarket().addCondition("rare_ore_abundant");
      caries.getMarket().addCondition("organics_common");
      system.addAsteroidBelt(star, 100, 5075.0F, 400.0F, 75.0F, 125.0F, "asteroid_belt", "Edison's Belt");
      system.addRingBand(star, "misc", "rings_dust0", 256.0F, 3, Color.white, 256.0F, 5000.0F, 190.0F, (String)null, (String)null);
      system.addRingBand(star, "misc", "rings_asteroids0", 256.0F, 3, Color.white, 256.0F, 5100.0F, 210.0F, (String)null, (String)null);
      PlanetAPI novaeSpes = system.addPlanet("spesPlanet", star, "Novae Spes", "terran", 25.0F, 122.0F, 5800.0F, 250.0F);
      novaeSpes.setFaction("fringe_defence_syndicate");
      SectorEntityToken archimedes_relay = system.addCustomEntity("archimedesRelay", "Archimedes Relay", "comm_relay", "fringe_defence_syndicate");
      archimedes_relay.setCircularOrbitPointingDown(star, 120.0F, 6750.0F, 300.0F);
      SectorEntityToken newtonStation = system.addCustomEntity("newtonStation", "Newton Space Station", "station_side00", "fringe_defence_syndicate");
      newtonStation.setCircularOrbitPointingDown(novaeSpes, 0.0F, 260.0F, 100.0F);
      FDS_AddMarket.FDS_AddMarket("fringe_defence_syndicate", novaeSpes, new ArrayList(Arrays.asList(newtonStation)), "Novae Spes", 7, new ArrayList(Arrays.asList("mild_climate", "farmland_bountiful", "habitable", "organics_common", "regional_capital", "population_8", "urbanized_polity")), new ArrayList(Arrays.asList("highcommand", "lightindustry", "orbitalworks", "megaport", "starfortress_mid", "farming", "mining", "fuelprod", "heavybatteries", "population", "fds_repair_yards", "fds_maintenance_bots_factory")), new ArrayList(Arrays.asList("storage", "black_market", "open_market", "generic_military")), 0.35F);
      JumpPointAPI spesJumpPoint = Global.getFactory().createJumpPoint("spesJumpPoint", "Spes Jump-Point");
      OrbitAPI orbit = Global.getFactory().createCircularOrbit(star, 42.0F, 6200.0F, 250.0F);
      spesJumpPoint.setOrbit(orbit);
      spesJumpPoint.setRelatedPlanet(novaeSpes);
      spesJumpPoint.setStandardWormholeToHyperspaceVisual();
      system.addEntity(spesJumpPoint);
      SectorEntityToken gate = system.addCustomEntity("archimedesGate", "Archimedes Gate", "inactive_gate", (String)null);
      gate.setCircularOrbit(star, 0.0F, 7000.0F, 310.0F);
      PlanetAPI tribulatio = system.addPlanet("tribulatioPlanet", star, "Tribulatio", "fds_gas_giant", (float)Math.random() * 360.0F, 460.0F, 9100.0F, 225.0F);
      system.addRingBand(tribulatio, "misc", "rings_ice0", 256.0F, 2, Color.white, 200.0F, 1000.0F, 50.0F, "ring", (String)null);
      PlanetAPI luctus = system.addPlanet("luctusPlanet", tribulatio, "Luctus", "fds_tundra", 120.0F, 50.0F, 700.0F, 42.0F);
      Misc.initConditionMarket(luctus);
      luctus.getMarket().addCondition("cold");
      luctus.getMarket().addCondition("thin_atmosphere");
      luctus.getMarket().addCondition("ore_rich");
      system.addAsteroidBelt(star, 250, 12000.0F, 500.0F, 500.0F, 600.0F, "asteroid_belt", "Aristotle's Belt");
      system.addRingBand(star, "misc", "rings_asteroids0", 256.0F, 0, Color.white, 256.0F, 12000.0F, 550.0F);
      PlanetAPI tristitia = system.addPlanet("tristitiaPlanet", star, "Tristitia", "fds_frozen", 12.0F, 60.0F, 15000.0F, 600.0F);
      Misc.initConditionMarket(tristitia);
      tristitia.getMarket().addCondition("very_cold");
      tristitia.getMarket().addCondition("no_atmosphere");
      tristitia.getMarket().addCondition("volatiles_plentiful");
      tristitia.getMarket().addCondition("dark");
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
