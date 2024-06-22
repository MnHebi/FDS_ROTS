package scripts;

import com.fs.starfarer.api.Global;
import lunalib.lunaSettings.LunaSettings;
public class FDSLunaSettings {

    public static Boolean DroidMechanicsToggle(){
        Boolean droidmechanics = false;
        if (Global.getSettings().getModManager().isModEnabled("lunalib"))
        {
            droidmechanics = LunaSettings.getBoolean("FDS","fds_droidmechanics");
        }
        return droidmechanics;
    }

    public static Boolean StorylineToggle(){
        Boolean storyline = false;
        if (Global.getSettings().getModManager().isModEnabled("lunalib"))
        {
            storyline = LunaSettings.getBoolean("FDS","fds_storyline");
        }
        return storyline;
    }

}
