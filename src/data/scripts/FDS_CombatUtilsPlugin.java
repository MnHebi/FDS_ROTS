package data.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseEveryFrameCombatPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.dark.shaders.distortion.DistortionShader;
import org.dark.shaders.distortion.RippleDistortion;
import org.lwjgl.util.vector.Vector2f;

public class FDS_CombatUtilsPlugin extends BaseEveryFrameCombatPlugin {
   private CombatEngineAPI engine;
   private static final Vector2f ZERO = new Vector2f();
   private String defaultMusicId = "music_combat";
   private boolean soundToggle = false;
   private float duration = 0.0F;

   public void init(CombatEngineAPI engine) {
      this.engine = engine;
      this.soundToggle = false;
      this.engine.getCustomData().put("fds_signature_dampener", new HashMap(10));
   }

   public void advance(float amount, List<InputEventAPI> events) {
      if (this.engine != null) {
         Map<ShipAPI, RippleDistortion> maps = (Map)Global.getCombatEngine().getCustomData().get("fds_signature_dampener");
         if (this.duration < 2.0F) {
            if (!this.engine.isPaused()) {
               this.duration += amount;
            }
         } else {
            List<ShipAPI> orisToRemove = new ArrayList();
            Iterator i$ = maps.keySet().iterator();

            ShipAPI ori;
            while(i$.hasNext()) {
               ori = (ShipAPI)i$.next();
               if (!ori.isAlive()) {
                  RippleDistortion halo = (RippleDistortion)maps.get(ori);
                  DistortionShader.removeDistortion(halo);
                  orisToRemove.add(ori);
               }
            }

            i$ = orisToRemove.iterator();

            while(i$.hasNext()) {
               ori = (ShipAPI)i$.next();
               maps.remove(ori);
            }
         }
      }

   }
}
