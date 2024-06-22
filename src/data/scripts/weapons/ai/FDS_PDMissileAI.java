//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package data.scripts.weapons.ai;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.GuidedMissileAI;
import com.fs.starfarer.api.combat.MissileAIPlugin;
import com.fs.starfarer.api.combat.MissileAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipCommand;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.WeightedRandomPicker;
import data.scripts.util.MagicRender;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.lazywizard.lazylib.FastTrig;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.AIUtils;
import org.lwjgl.util.vector.Vector2f;

public class FDS_PDMissileAI implements MissileAIPlugin, GuidedMissileAI {
    private CombatEngineAPI engine;
    private final MissileAPI missile;
    private CombatEntityAPI target;
    private Vector2f lead = new Vector2f();
    private IntervalUtil timer = new IntervalUtil(0.025F, 0.075F);
    private boolean targetOnce = false;
    private final float MAX_SPEED;
    private final float DAMPING = 0.05F;
    private final Color EXPLOSION_COLOR = new Color(255, 0, 0, 255);
    private final Color PARTICLE_COLOR = new Color(240, 200, 50, 255);
    private final int NUM_PARTICLES = 10;
    private static Map<MissileAPI, MissileAPI> ANTIMISSILES = new HashMap();
    private static final float N = 3.0F;
    private Vector2f oldDiff;
    private float oldVel = 0.0F;

    public FDS_PDMissileAI(MissileAPI missile, ShipAPI launchingShip) {
        this.missile = missile;
        this.MAX_SPEED = missile.getMaxSpeed();
        this.timer.randomize();
        this.oldDiff = missile.getLocation();
    }

    public void advance(float amount) {
        if (this.engine != Global.getCombatEngine()) {
            this.engine = Global.getCombatEngine();
        }

        if (!Global.getCombatEngine().isPaused() && !this.missile.isFading() && !this.missile.isFizzling()) {
            if (this.target != null && this.engine.isEntityInPlay(this.target)) {
                this.targetOnce = true;
                this.timer.advance(amount);
                float dist;
                if (this.timer.intervalElapsed()) {
                    dist = MathUtils.getDistanceSquared(this.missile.getLocation(), this.target.getLocation());
                    if (dist < 2500.0F) {
                        this.proximityFuse();
                        return;
                    }

                    this.lead = AIUtils.getBestInterceptPoint(this.missile.getLocation(), this.MAX_SPEED, this.target.getLocation(), this.target.getVelocity());
                    if (this.lead == null) {
                        this.lead = this.target.getLocation();
                    }
                }

                dist = VectorUtils.getAngle(this.missile.getLocation(), this.lead);
                float offCourseAngle = MathUtils.getShortestRotation(VectorUtils.getFacing(this.missile.getVelocity()), dist);
                float correction = MathUtils.getShortestRotation(dist, VectorUtils.getFacing(this.missile.getVelocity()) + 180.0F) * 0.5F * (float)FastTrig.sin((double)(0.034906585F * Math.min(Math.abs(offCourseAngle), 45.0F)));
                dist += correction;
                float aimAngle = MathUtils.getShortestRotation(this.missile.getFacing(), dist);
                if (aimAngle < 0.0F) {
                    this.missile.giveCommand(ShipCommand.TURN_RIGHT);
                } else {
                    this.missile.giveCommand(ShipCommand.TURN_LEFT);
                }

                if (Math.abs(aimAngle) < 45.0F) {
                    this.missile.giveCommand(ShipCommand.ACCELERATE);
                }

                if (Math.abs(aimAngle) < Math.abs(this.missile.getAngularVelocity()) * 0.05F) {
                    this.missile.setAngularVelocity(aimAngle / 0.05F);
                }

            } else {
                this.missile.giveCommand(ShipCommand.ACCELERATE);
                if (!this.targetOnce) {
                    this.setTarget(this.findRandomMissileWithinRange(this.missile));
                } else {
                    this.target = AIUtils.getNearestEnemyMissile(this.missile);
                    if (this.target != null) {
                        ANTIMISSILES.put(this.missile, (MissileAPI)this.target);
                    }
                }

            }
        }
    }

    private CombatEntityAPI findRandomMissileWithinRange(MissileAPI missile) {
        ShipAPI source = missile.getSource();
        WeightedRandomPicker<MissileAPI> targets = new WeightedRandomPicker();
        List<MissileAPI> TARGETTED = getAntimissiles();

        MissileAPI m;
        float danger;
        for(Iterator i$ = AIUtils.getNearbyEnemyMissiles(source, 1000.0F).iterator(); i$.hasNext(); targets.add(m, danger)) {
            m = (MissileAPI)i$.next();
            danger = 2.0F;
            if (MathUtils.isWithinRange(source, m, 333.0F)) {
                danger = 4.0F;
            } else if (MathUtils.isWithinRange(source, m, 666.0F)) {
                danger = 3.0F;
            }

            if (m.getDamageAmount() > 700.0F) {
                ++danger;
            } else if (m.getDamageAmount() < 150.0F) {
                --danger;
            }

            if (TARGETTED.contains(m)) {
                if (m.getHitpoints() * m.getHullLevel() <= missile.getDamageAmount()) {
                    danger -= 2.0F;
                } else {
                    --danger;
                }
            }
        }

        MissileAPI theTarget = (MissileAPI)targets.pick();
        if (theTarget != null) {
            ANTIMISSILES.put(missile, theTarget);
            theTarget.setShineBrightness(0.0F);
        }

        return theTarget;
    }

    void proximityFuse() {
        this.engine.applyDamage(this.target, this.target.getLocation(), this.missile.getDamageAmount(), DamageType.FRAGMENTATION, 0.0F, false, false, this.missile.getSource());
        List<MissileAPI> closeMissiles = AIUtils.getNearbyEnemyMissiles(this.missile, 100.0F);
        Iterator i$ = closeMissiles.iterator();

        while(i$.hasNext()) {
            MissileAPI cm = (MissileAPI)i$.next();
            if (cm != this.target) {
                this.engine.applyDamage(cm, cm.getLocation(), 2.0F * this.missile.getDamageAmount() / 3.0F - this.missile.getDamageAmount() / 3.0F * ((float)Math.cos((double)(3000.0F / (MathUtils.getDistanceSquared(this.missile.getLocation(), this.target.getLocation()) + 1000.0F))) + 1.0F), DamageType.FRAGMENTATION, 0.0F, false, true, this.missile.getSource());
            }
        }

        if (MagicRender.screenCheck(0.5F, this.missile.getLocation())) {
            this.engine.addHitParticle(this.missile.getLocation(), new Vector2f(), 100.0F, 1.0F, 0.25F, this.EXPLOSION_COLOR);

            for(int i = 0; i < 10; ++i) {
                float axis = (float)Math.random() * 360.0F;
                float range = (float)Math.random() * 100.0F;
                this.engine.addHitParticle(MathUtils.getPoint(this.missile.getLocation(), range / 5.0F, axis), MathUtils.getPoint(new Vector2f(), range, axis), 2.0F + (float)Math.random() * 2.0F, 1.0F, 1.0F + (float)Math.random(), this.PARTICLE_COLOR);
            }
        }

        this.engine.applyDamage(this.missile, this.missile.getLocation(), this.missile.getHitpoints() * 2.0F, DamageType.FRAGMENTATION, 0.0F, false, false, this.missile);
    }

    public CombatEntityAPI getTarget() {
        return this.target;
    }

    public void setTarget(CombatEntityAPI target) {
        this.target = target;
    }

    public void init(CombatEngineAPI engine) {
    }

    public static List<MissileAPI> getAntimissiles() {
        List<MissileAPI> missiles = new ArrayList();
        Iterator i$ = ANTIMISSILES.keySet().iterator();

        while(i$.hasNext()) {
            MissileAPI m = (MissileAPI)i$.next();
            if (ANTIMISSILES.get(m) != null) {
                missiles.add(ANTIMISSILES.get(m));
            }
        }

        return missiles;
    }

    private Vector2f getAPNPoint(Vector2f missileLoc, Vector2f missileVel, Vector2f targetLoc, Vector2f targetVel) {
        Vector2f diff = new Vector2f(targetLoc.x - missileLoc.x, targetLoc.y - missileLoc.y);
        Vector2f normalNew = this.normaliseVector(diff);
        Vector2f normalOld = this.normaliseVector(this.oldDiff);
        if (normalNew != null && normalOld != null) {
            Vector2f LOS = new Vector2f(normalNew.x - normalOld.x, normalNew.y - normalOld.y);
            float rate = LOS.length();
            float vcAux = Vector2f.dot(diff, new Vector2f(targetVel.x - missileVel.x, targetVel.y - missileVel.y));
            float vc = vcAux / diff.length();
            float tVelDotLOS = Vector2f.dot(normalNew, targetVel);
            Vector2f scalar = new Vector2f(normalNew.x * tVelDotLOS, normalNew.y * tVelDotLOS);
            Vector2f cleanVel = new Vector2f(targetVel.x - scalar.x, targetVel.y - scalar.y);
            float vel = cleanVel.length();
            float nt = (vel - this.oldVel) / this.engine.getElapsedInLastFrame();
            float a = 3.0F * vc * rate;
            float b = 3.0F * vc * rate + 3.0F * nt / 2.0F;
            Vector2f temp = new Vector2f(0.0F, b);
            Vector2f targetAux = VectorUtils.rotate(temp, VectorUtils.getFacing(diff));
            Vector2f target = new Vector2f(missileLoc.x + missileVel.x + targetAux.x, missileLoc.y + missileVel.y + targetAux.y);
            this.oldDiff = diff;
            this.oldVel = vel;
            return target;
        } else {
            return null;
        }
    }

    private Vector2f normaliseVector(Vector2f vector) {
        double n = (double)vector.length();
        return n > 0.0 ? new Vector2f((float)((double)vector.y / n), (float)((double)vector.y / n)) : null;
    }
}
