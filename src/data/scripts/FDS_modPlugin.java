//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

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
import exerelin.campaign.SectorManager;
import java.io.IOException;
import java.util.Iterator;
import org.apache.log4j.Level;
import org.dark.shaders.light.LightData;
import org.dark.shaders.util.ShaderLib;
import org.json.JSONException;
import org.json.JSONObject;

public class FDS_modPlugin extends BaseModPlugin {
    private static final String SETTINGS_FILE = "FDS_OPTIONS.ini";
    public static boolean fdsStoryline = false;
    public static boolean droidMechanics = false;

    public FDS_modPlugin() {
    }

    private static void loadSettings() throws IOException, JSONException {
        JSONObject settings = Global.getSettings().loadJSON("FDS_OPTIONS.ini");
        fdsStoryline = false;
        droidMechanics = settings.getBoolean("droidMechanics");
    }

    private static void loadDefaultSettings() {
        fdsStoryline = false;
        droidMechanics = false;
    }

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
            loadSettings();
        } catch (JSONException | IOException var4) {
            loadDefaultSettings();
            Global.getLogger(FDS_modPlugin.class).log(Level.ERROR, "Settings loading failed, using default values");
        } catch (RuntimeException var5) {
            loadDefaultSettings();
            Global.getLogger(FDS_modPlugin.class).log(Level.WARN, "Settings file not found, using default values");
        } catch (Exception var6) {
            loadDefaultSettings();
            Global.getLogger(FDS_modPlugin.class).log(Level.ERROR, "Settings file failed to load due to unknown reasons, using default values");
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
        if (!hasNexerelin || SectorManager.getCorvusMode()) {
            (new FDSGen()).generate(Global.getSector());
        }
    }

    public void onGameLoad(boolean newGame) {
        if (droidMechanics) {
            Global.getSector().addTransientScript(new FDS_MaintenanceBotsBonus());
        }

    }

    public PluginPick<MissileAIPlugin> pickMissileAI(MissileAPI missile, ShipAPI launchingShip) {
        switch (missile.getProjectileSpecId()) {
            case "fds_barrage_missile":
                return new PluginPick(new FDS_CustomMissileAI(missile, launchingShip), PickPriority.MOD_SET);
            case "fds_pd_missile":
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
