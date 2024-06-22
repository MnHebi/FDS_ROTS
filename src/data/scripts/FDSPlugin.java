package data.scripts;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.PluginPick;
import com.fs.starfarer.api.campaign.CampaignPlugin.PickPriority;
import com.fs.starfarer.api.combat.MissileAIPlugin;
import com.fs.starfarer.api.combat.MissileAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.campaign.procgen.ConditionGenDataSpec;
import com.thoughtworks.xstream.XStream;
import data.scripts.campaign.FDS_MaintenanceBotsBonus;
import data.scripts.weapons.ai.FDS_CustomMissileAI;
import data.scripts.weapons.ai.FDS_PDMissileAI;
import data.scripts.world.FDSGen;
import scripts.FDSLunaSettings;
import exerelin.campaign.SectorManager;
import java.io.IOException;
import java.util.Iterator;
import org.apache.log4j.Level;
import org.dark.shaders.light.LightData;
import org.dark.shaders.util.ShaderLib;
import org.json.JSONException;
import org.json.JSONObject;

import org.apache.log4j.Logger;

public class FDSPlugin extends BaseModPlugin {
   //public static boolean fdsStoryline = false;
   public Logger FDSlog = Logger.getLogger(this.getClass());

   public void configureXStream(XStream x) {
      x.alias("FDS_MaintenanceBotsBonus", FDS_MaintenanceBotsBonus.class);
   }

   public void onApplicationLoad() throws ClassNotFoundException {
      String message;
      try {
         Global.getSettings().getScriptClassLoader().loadClass("org.lazywizard.lazylib.ModUtils");
      } catch (ClassNotFoundException var8) {
         message = System.lineSeparator() + System.lineSeparator() + "LazyLib is required to run at least one of the mods you have installed." + System.lineSeparator() + System.lineSeparator() + "You can download LazyLib at http://fractalsoftworks.com/forum/index.php?topic=5444" + System.lineSeparator();
         throw new ClassNotFoundException(message);
      }

      try {
         Global.getSettings().getScriptClassLoader().loadClass("data.scripts.plugins.MagicTrailPlugin");
      } catch (ClassNotFoundException var7) {
         message = System.lineSeparator() + System.lineSeparator() + "MagicLib is required to run FDS." + System.lineSeparator() + System.lineSeparator() + "You can download MagicLib at http://fractalsoftworks.com/forum/index.php?topic=13718" + System.lineSeparator();
         throw new ClassNotFoundException(message);
      }

      try {
         Global.getSettings().getScriptClassLoader().loadClass("org.dark.shaders.util.ShaderLib");
         ShaderLib.init();
         LightData.readLightDataCSV("data/lights/FDS_Light.csv");
      } catch (ClassNotFoundException var3) {
         message = System.lineSeparator() + System.lineSeparator() + "GraphicsLib is required to run at least one of the mods you have installed." + System.lineSeparator() + System.lineSeparator() + "You can download GraphicsLib at http://fractalsoftworks.com/forum/index.php?topic=10982" + System.lineSeparator();
         throw new ClassNotFoundException(message);
      }

      this.generateCustomCategories();
   }

   public void onNewGame() {
      boolean hasNexerelin = Global.getSettings().getModManager().isModEnabled("nexerelin");
      if (!hasNexerelin || SectorManager.getManager().isCorvusMode()) {
         (new FDSGen()).generate(Global.getSector());
      }
   }

   public void onGameLoad(boolean WasEnabledBefore) {
      updateLunaSettings();
   }

   public void updateLunaSettings() {
      Boolean droidMechanics = FDSLunaSettings.DroidMechanicsToggle();
      //Boolean storyline = FDSLunaSettings.StorylineToggle();
      if (droidMechanics)
      {
         FDSlog.info("FDS Enabled Maintenace Droids");
         Global.getSector().addTransientScript(new FDS_MaintenanceBotsBonus());
      }
      if (!droidMechanics)
      {
         FDSlog.info("FDS Disabled Maintenace Droids");
         Global.getSector().removeTransientScript(new FDS_MaintenanceBotsBonus());
      }
      // Lets bury this for now, they never got anywhere with this, just a copy of the storyline mission with deprecated code. Deleted the module.
      //if (storyline)
      //{
      //   FDSlog.info("FDS Enabled Storyline");
      //   fdsStoryline = true;
      //}
      //if (!storyline)
      //{
      //   FDSlog.info("FDS Disabled Storyline");
      //   fdsStoryline = false;
      //}
   }

   public PluginPick<MissileAIPlugin> pickMissileAI(MissileAPI missile, ShipAPI launchingShip) {
      String var3 = missile.getProjectileSpecId();
      byte var4 = -1;
      switch(var3.hashCode()) {
      case 1246635261:
         if (var3.equals("fds_barrage_missile")) {
            var4 = 0;
         }
         break;
      case 1986232549:
         if (var3.equals("fds_pd_missile")) {
            var4 = 1;
         }
      }

      switch(var4) {
      case 0:
         return new PluginPick(new FDS_CustomMissileAI(missile, launchingShip), PickPriority.MOD_SET);
      case 1:
         return new PluginPick(new FDS_PDMissileAI(missile, launchingShip), PickPriority.MOD_SET);
      default:
         return null;
      }
   }

   public void generateCustomCategories() {
      Iterator i$ = Global.getSettings().getAllSpecs(ConditionGenDataSpec.class).iterator();

      while(i$.hasNext()) {
         Object o = i$.next();
         ConditionGenDataSpec spec = (ConditionGenDataSpec)o;
         if (spec.getId().equals("extreme_tectonic_activity")) {
            spec.getMultipliers().put("fds_lava", 100.0F);
            spec.getMultipliers().put("fds_cryovolcanic", 1.0F);
         }

         if (spec.getId().equals("tectonic_activity")) {
            spec.getMultipliers().put("fds_cryovolcanic", 10.0F);
         }

         if (spec.getId().equals("very_cold")) {
            spec.getMultipliers().put("fds_cryovolcanic", 1.0F);
         }

         if (spec.getId().equals("very_hot")) {
            spec.getMultipliers().put("fds_lava", 100.0F);
         }

         if (spec.getId().equals("atmosphere_no_pick")) {
            spec.getMultipliers().put("fds_cryovolcanic", 15.0F);
         }

         if (spec.getId().equals("thin_atmosphere")) {
            spec.getMultipliers().put("fds_cryovolcanic", 1.0F);
         }

         if (spec.getId().equals("toxic_atmosphere")) {
            spec.getMultipliers().put("fds_lava", 100.0F);
            spec.getMultipliers().put("fds_cryovolcanic", 1.0F);
         }

         if (spec.getId().equals("dense_atmosphere")) {
            spec.getMultipliers().put("fds_lava", 100.0F);
            spec.getMultipliers().put("fds_cryovolcanic", 1.0F);
         }

         if (spec.getId().equals("biosphere_no_pick")) {
            spec.getMultipliers().put("fds_lava", 100.0F);
            spec.getMultipliers().put("fds_cryovolcanic", 100.0F);
         }

         if (spec.getId().equals("inimical_biosphere")) {
            spec.getMultipliers().put("fds_lava", 1.0F);
            spec.getMultipliers().put("fds_cryovolcanic", 1.0F);
         }

         if (spec.getId().equals("ruins_no_pick")) {
            spec.getMultipliers().put("fds_lava", 1000.0F);
            spec.getMultipliers().put("fds_cryovolcanic", 200.0F);
         }

         if (spec.getId().equals("ruins_scattered")) {
            spec.getMultipliers().put("fds_lava", 3.0F);
            spec.getMultipliers().put("fds_cryovolcanic", 15.0F);
         }

         if (spec.getId().equals("ruins_widespread")) {
            spec.getMultipliers().put("fds_lava", 1.0F);
            spec.getMultipliers().put("fds_cryovolcanic", 10.0F);
         }

         if (spec.getId().equals("ruins_extensive")) {
            spec.getMultipliers().put("fds_cryovolcanic", 5.0F);
         }

         if (spec.getId().equals("ruins_vast")) {
            spec.getMultipliers().put("fds_cryovolcanic", 1.0F);
         }

         if (spec.getId().equals("decivilized_no_pick")) {
            spec.getMultipliers().put("fds_lava", 100.0F);
            spec.getMultipliers().put("fds_cryovolcanic", 100.0F);
         }

         if (spec.getId().equals("decivilized")) {
            spec.getMultipliers().put("fds_lava", 10.0F);
            spec.getMultipliers().put("fds_cryovolcanic", 1.0F);
         }

         if (spec.getId().equals("weather_no_pick")) {
            spec.getMultipliers().put("fds_cryovolcanic", 10.0F);
         }

         if (spec.getId().equals("extreme_weather")) {
            spec.getMultipliers().put("fds_cryovolcanic", 1.0F);
         }

         if (spec.getId().equals("pollution_no_pick")) {
            spec.getMultipliers().put("fds_cryovolcanic", 15.0F);
         }

         if (spec.getId().equals("pollution")) {
            spec.getMultipliers().put("fds_cryovolcanic", 1.0F);
         }

         if (spec.getId().equals("ore_no_pick")) {
            spec.getMultipliers().put("fds_lava", 0.0F);
            spec.getMultipliers().put("fds_cryovolcanic", 0.0F);
         }

         if (spec.getId().equals("ore_sparse")) {
            spec.getMultipliers().put("fds_lava", 0.0F);
            spec.getMultipliers().put("fds_cryovolcanic", 4.0F);
         }

         if (spec.getId().equals("ore_moderate")) {
            spec.getMultipliers().put("fds_lava", 10.0F);
            spec.getMultipliers().put("fds_cryovolcanic", 20.0F);
         }

         if (spec.getId().equals("ore_abundant")) {
            spec.getMultipliers().put("fds_lava", 30.0F);
            spec.getMultipliers().put("fds_cryovolcanic", 10.0F);
         }

         if (spec.getId().equals("ore_rich")) {
            spec.getMultipliers().put("fds_lava", 30.0F);
            spec.getMultipliers().put("fds_cryovolcanic", 3.0F);
         }

         if (spec.getId().equals("ore_ultrarich")) {
            spec.getMultipliers().put("fds_lava", 20.0F);
            spec.getMultipliers().put("fds_cryovolcanic", 1.0F);
         }

         if (spec.getId().equals("rare_ore_no_pick")) {
            spec.getMultipliers().put("fds_lava", 5.0F);
            spec.getMultipliers().put("fds_cryovolcanic", 20.0F);
         }

         if (spec.getId().equals("rare_ore_sparse")) {
            spec.getMultipliers().put("fds_lava", 10.0F);
            spec.getMultipliers().put("fds_cryovolcanic", 10.0F);
         }

         if (spec.getId().equals("rare_ore_moderate")) {
            spec.getMultipliers().put("fds_lava", 10.0F);
            spec.getMultipliers().put("fds_cryovolcanic", 10.0F);
         }

         if (spec.getId().equals("rare_ore_abundant")) {
            spec.getMultipliers().put("fds_lava", 30.0F);
            spec.getMultipliers().put("fds_cryovolcanic", 10.0F);
         }

         if (spec.getId().equals("rare_ore_rich")) {
            spec.getMultipliers().put("fds_lava", 25.0F);
            spec.getMultipliers().put("fds_cryovolcanic", 5.0F);
         }

         if (spec.getId().equals("rare_ore_ultrarich")) {
            spec.getMultipliers().put("fds_lava", 15.0F);
            spec.getMultipliers().put("fds_cryovolcanic", 3.0F);
         }

         if (spec.getId().equals("volatiles_trace")) {
            spec.getMultipliers().put("fds_cryovolcanic", 10.0F);
         }

         if (spec.getId().equals("volatiles_diffuse")) {
            spec.getMultipliers().put("fds_cryovolcanic", 15.0F);
         }

         if (spec.getId().equals("volatiles_abundant")) {
            spec.getMultipliers().put("fds_cryovolcanic", 15.0F);
         }

         if (spec.getId().equals("volatiles_plentiful")) {
            spec.getMultipliers().put("fds_cryovolcanic", 5.0F);
         }

         if (spec.getId().equals("fds_crystal_caves_no_pick")) {
            spec.getMultipliers().put("fds_cryovolcanic", 50.0F);
         }

         if (spec.getId().equals("fds_crystal_caves")) {
            spec.getMultipliers().put("fds_cryovolcanic", 1.0F);
         }
      }

   }
}
