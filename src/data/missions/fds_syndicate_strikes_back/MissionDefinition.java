package data.missions.fds_syndicate_strikes_back;

import com.fs.starfarer.api.fleet.FleetGoal;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.mission.FleetSide;
import com.fs.starfarer.api.mission.MissionDefinitionAPI;
import com.fs.starfarer.api.mission.MissionDefinitionPlugin;

public class MissionDefinition implements MissionDefinitionPlugin {
   public void defineMission(MissionDefinitionAPI api) {
      api.initFleet(FleetSide.PLAYER, "FDS", FleetGoal.ATTACK, false);
      api.initFleet(FleetSide.ENEMY, "HSS", FleetGoal.ATTACK, true);
      api.setFleetTagline(FleetSide.PLAYER, "Syndicate Attack Fleet");
      api.setFleetTagline(FleetSide.ENEMY, "Hegemony Patrol Fleet");
      api.addBriefingItem("Leave no survivors!");
      api.addToFleet(FleetSide.PLAYER, "fds_suffering_elite", FleetMemberType.SHIP, "Annihilator", true);
      api.addToFleet(FleetSide.PLAYER, "fds_hatred_assault", FleetMemberType.SHIP, "Destroyer", false);
      api.addToFleet(FleetSide.PLAYER, "fds_fear_assault", FleetMemberType.SHIP, "Conqueror", false);
      api.addToFleet(FleetSide.PLAYER, "fds_fear_assault", FleetMemberType.SHIP, "Victorious", false);
      api.addToFleet(FleetSide.PLAYER, "fds_fear_strike", FleetMemberType.SHIP, "Undefeated", false);
      api.addToFleet(FleetSide.PLAYER, "fds_revenge_standard", FleetMemberType.SHIP, "Vanguard", false);
      api.addToFleet(FleetSide.PLAYER, "fds_wrath_support", FleetMemberType.SHIP, "Infiltrator", false);
      api.addToFleet(FleetSide.PLAYER, "fds_wrath_elite", FleetMemberType.SHIP, "Righteous", false);
      api.addToFleet(FleetSide.PLAYER, "fds_atonement_support", FleetMemberType.SHIP, "Frost", false);
      api.addToFleet(FleetSide.PLAYER, "fds_atonement_assault", FleetMemberType.SHIP, "Ice", false);
      api.addToFleet(FleetSide.PLAYER, "fds_retaliation_standard", FleetMemberType.SHIP, "Avenger", false);
      api.addToFleet(FleetSide.PLAYER, "fds_pride_strike", FleetMemberType.SHIP, "Providence", false);
      api.addToFleet(FleetSide.PLAYER, "fds_affliction_assault", FleetMemberType.SHIP, "Afflictor", false);
      api.addToFleet(FleetSide.PLAYER, "fds_despair_standard", FleetMemberType.SHIP, "Desperate", false);
      api.addToFleet(FleetSide.PLAYER, "fds_despair_standard", FleetMemberType.SHIP, "Punisher", false);
      api.addToFleet(FleetSide.PLAYER, "fds_submission_standard", FleetMemberType.SHIP, "Barrage", false);
      api.addToFleet(FleetSide.PLAYER, "fds_grief_mk_ii_assault", FleetMemberType.SHIP, "Midas", false);
      api.addToFleet(FleetSide.PLAYER, "fds_grief_mk_ii_assault", FleetMemberType.SHIP, "Magnate", false);
      api.addToFleet(FleetSide.PLAYER, "fds_sorrow_elite", FleetMemberType.SHIP, "Fortuitous", false);
      api.addToFleet(FleetSide.PLAYER, "fds_sorrow_elite", FleetMemberType.SHIP, "Fair Trade", false);
      api.addToFleet(FleetSide.PLAYER, "fds_agony_assault", FleetMemberType.SHIP, "Big Break", false);
      api.addToFleet(FleetSide.PLAYER, "fds_agony_mk_ii_standard", FleetMemberType.SHIP, "Margin Call", false);
      api.addToFleet(FleetSide.PLAYER, "fds_disturbance_standard", FleetMemberType.SHIP, "Baron", false);
      api.addToFleet(FleetSide.ENEMY, "onslaught_Standard", FleetMemberType.SHIP, "Invulnerable", true);
      api.addToFleet(FleetSide.ENEMY, "onslaught_Standard", FleetMemberType.SHIP, "Unshakable", false);
      api.addToFleet(FleetSide.ENEMY, "onslaught_Outdated", FleetMemberType.SHIP, "Unconquerable", false);
      api.addToFleet(FleetSide.ENEMY, "dominator_Assault", FleetMemberType.SHIP, "Uprising", false);
      api.addToFleet(FleetSide.ENEMY, "dominator_Assault", FleetMemberType.SHIP, "Spartacus", false);
      api.addToFleet(FleetSide.ENEMY, "heron_Strike", FleetMemberType.SHIP, "Enterprise", false);
      api.addToFleet(FleetSide.ENEMY, "eagle_Balanced", FleetMemberType.SHIP, "Resolute", false);
      api.addToFleet(FleetSide.ENEMY, "eagle_Balanced", FleetMemberType.SHIP, "Faithful", false);
      api.addToFleet(FleetSide.ENEMY, "eagle_Assault", FleetMemberType.SHIP, "Devoted", false);
      api.addToFleet(FleetSide.ENEMY, "falcon_CS", FleetMemberType.SHIP, "Defiant", false);
      api.addToFleet(FleetSide.ENEMY, "falcon_Attack", FleetMemberType.SHIP, "Boxer", false);
      api.addToFleet(FleetSide.ENEMY, "hammerhead_Balanced", FleetMemberType.SHIP, "Vicious", false);
      api.addToFleet(FleetSide.ENEMY, "hammerhead_Support", FleetMemberType.SHIP, "Domitius", false);
      api.addToFleet(FleetSide.ENEMY, "enforcer_Assault", FleetMemberType.SHIP, "Pax", false);
      api.addToFleet(FleetSide.ENEMY, "enforcer_Balanced", FleetMemberType.SHIP, "Nox", false);
      api.addToFleet(FleetSide.ENEMY, "vigilance_FS", FleetMemberType.SHIP, "Fast", false);
      api.addToFleet(FleetSide.ENEMY, "vigilance_FS", FleetMemberType.SHIP, "Swift", false);
      api.addToFleet(FleetSide.ENEMY, "lasher_CS", FleetMemberType.SHIP, "Striker", false);
      api.addToFleet(FleetSide.ENEMY, "lasher_CS", FleetMemberType.SHIP, "Punisher", false);
      api.addToFleet(FleetSide.ENEMY, "lasher_PD", FleetMemberType.SHIP, "Terror", false);
      float width = 15000.0F;
      float height = 15000.0F;
      api.initMap(-width / 2.0F, width / 2.0F, -height / 2.0F, height / 2.0F);
      float minX = -width / 2.0F;
      float minY = -height / 2.0F;
      api.addNebula(minX + width * 0.2F, minY + height * 0.6F, 1200.0F);
      api.addNebula(minX + width * 0.3F, minY + height * 0.4F, 2000.0F);
      api.addNebula(minX + width * 0.6F, minY + height * 0.7F, 1000.0F);
      api.addNebula(minX + width * 0.5F, minY + height * 0.2F, 500.0F);

      for(int i = 0; i < 8; ++i) {
         float x = (float)Math.random() * width - width / 2.0F;
         float y = (float)Math.random() * height - height / 2.0F;
         float radius = 100.0F + (float)Math.random() * 400.0F;
         api.addNebula(x, y, radius);
      }

   }
}
