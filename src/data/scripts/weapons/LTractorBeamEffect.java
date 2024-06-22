package data.scripts.weapons;

import com.fs.starfarer.api.combat.BeamAPI;
import com.fs.starfarer.api.combat.BeamEffectPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.CombatUtils;

public class LTractorBeamEffect implements BeamEffectPlugin {
   private static final float PULL_STRENGTH = 200.0F;

   public void advance(float amount, CombatEngineAPI engine, BeamAPI beam) {
      CombatEntityAPI target = beam.getDamageTarget();
      if (target != null) {
         CombatUtils.applyForce(target, VectorUtils.getDirectionalVector(beam.getTo(), beam.getFrom()), 200.0F * amount);
      }

   }
}
