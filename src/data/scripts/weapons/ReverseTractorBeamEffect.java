package data.scripts.weapons;

import com.fs.starfarer.api.combat.BeamAPI;
import com.fs.starfarer.api.combat.BeamEffectPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.CombatUtils;

public class ReverseTractorBeamEffect implements BeamEffectPlugin {
   private static final float PULL_STRENGTH = 4500.0F;

   public void advance(float amount, CombatEngineAPI engine, BeamAPI beam) {
      CombatEntityAPI target = beam.getDamageTarget();
      if (target != null && (target.getShield() == null || !target.getShield().isWithinArc(beam.getTo()))) {
         CombatUtils.applyForce(target, VectorUtils.getDirectionalVector(beam.getFrom(), beam.getTo()), 4500.0F * amount);
      }

   }
}
