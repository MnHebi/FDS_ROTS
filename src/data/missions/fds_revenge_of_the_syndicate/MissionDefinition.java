package data.missions.fds_revenge_of_the_syndicate;

import com.fs.starfarer.api.fleet.FleetGoal;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.mission.FleetSide;
import com.fs.starfarer.api.mission.MissionDefinitionAPI;
import com.fs.starfarer.api.mission.MissionDefinitionPlugin;

public class MissionDefinition implements MissionDefinitionPlugin {
   public void defineMission(MissionDefinitionAPI api) {
      api.initFleet(FleetSide.PLAYER, "FDS", FleetGoal.ATTACK, false);
      api.initFleet(FleetSide.ENEMY, "HSS", FleetGoal.ATTACK, true);
      api.setFleetTagline(FleetSide.PLAYER, "Syndicate Strike Group");
      api.setFleetTagline(FleetSide.ENEMY, "Hegemony Assault Fleet");
      api.addBriefingItem("Leave no survivors!");
      api.addToFleet(FleetSide.PLAYER, "fds_terror_assault", FleetMemberType.SHIP, "Syndicate's Horror", true);
      api.addToFleet(FleetSide.PLAYER, "fds_torture_assault", FleetMemberType.SHIP, "Syndicate's Might", false);
      api.addToFleet(FleetSide.PLAYER, "fds_hatred_assault", FleetMemberType.SHIP, "Suffering", false);
      api.addToFleet(FleetSide.PLAYER, "fds_suffering_strike", FleetMemberType.SHIP, "Twin Fury I", false);
      api.addToFleet(FleetSide.PLAYER, "fds_suffering_elite", FleetMemberType.SHIP, "Twin Fury II", false);
      api.addToFleet(FleetSide.PLAYER, "fds_hatred_assault", FleetMemberType.SHIP, "Hate", false);
      api.addToFleet(FleetSide.PLAYER, "fds_fear_standard", FleetMemberType.SHIP, "Anger", false);
      api.addToFleet(FleetSide.PLAYER, "fds_fear_standard", FleetMemberType.SHIP, "Fear", false);
      api.addToFleet(FleetSide.PLAYER, "fds_atonement_standard", FleetMemberType.SHIP, "Ravager", false);
      api.addToFleet(FleetSide.PLAYER, "fds_revenge_strike", FleetMemberType.SHIP, "No Quarter", false);
      api.addToFleet(FleetSide.PLAYER, "fds_wrath_elite", FleetMemberType.SHIP, "Bismark", false);
      api.addToFleet(FleetSide.PLAYER, "fds_rancour_elite", FleetMemberType.SHIP, "Luke", false);
      api.addToFleet(FleetSide.PLAYER, "fds_rancour_assault", FleetMemberType.SHIP, "Leia", false);
      api.addToFleet(FleetSide.PLAYER, "fds_affliction_assault", FleetMemberType.SHIP, "Council", false);
      api.addToFleet(FleetSide.PLAYER, "fds_agony_mk_ii_standard", FleetMemberType.SHIP, "Misery", false);
      api.addToFleet(FleetSide.PLAYER, "fds_agony_mk_ii_standard", FleetMemberType.SHIP, "Pain", false);
      api.addToFleet(FleetSide.PLAYER, "fds_agony_mk_ii_standard", FleetMemberType.SHIP, "Wound", false);
      api.addToFleet(FleetSide.PLAYER, "fds_sorrow_elite", FleetMemberType.SHIP, "Forgotten", false);
      api.addToFleet(FleetSide.PLAYER, "fds_disturbance_standard", FleetMemberType.SHIP, "Nightmare", false);
      api.addToFleet(FleetSide.PLAYER, "fds_disturbance_standard", FleetMemberType.SHIP, "Dream", false);
      api.addToFleet(FleetSide.PLAYER, "fds_grief_mk_ii_assault", FleetMemberType.SHIP, "Death", false);
      api.addToFleet(FleetSide.PLAYER, "fds_melancholy_mk_ii_assault", FleetMemberType.SHIP, "Guilt", false);
      api.addToFleet(FleetSide.ENEMY, "onslaught_Elite", FleetMemberType.SHIP, "Strength", true);
      api.addToFleet(FleetSide.ENEMY, "onslaught_Standard", FleetMemberType.SHIP, "Power", false);
      api.addToFleet(FleetSide.ENEMY, "legion_Escort", FleetMemberType.SHIP, "Legionnaire", false);
      api.addToFleet(FleetSide.ENEMY, "dominator_AntiCV", FleetMemberType.SHIP, "Bull", false);
      api.addToFleet(FleetSide.ENEMY, "eagle_Balanced", FleetMemberType.SHIP, "Unity", false);
      api.addToFleet(FleetSide.ENEMY, "eagle_Balanced", FleetMemberType.SHIP, "Union", false);
      api.addToFleet(FleetSide.ENEMY, "falcon_CS", FleetMemberType.SHIP, "Bird of Prey", false);
      api.addToFleet(FleetSide.ENEMY, "mora_Support", FleetMemberType.SHIP, "Meteor", false);
      api.addToFleet(FleetSide.ENEMY, "hammerhead_Elite", FleetMemberType.SHIP, "Shark", false);
      api.addToFleet(FleetSide.ENEMY, "hammerhead_Elite", FleetMemberType.SHIP, "Killer Whale", false);
      api.addToFleet(FleetSide.ENEMY, "hammerhead_Balanced", FleetMemberType.SHIP, "Hammer", false);
      api.addToFleet(FleetSide.ENEMY, "hammerhead_Balanced", FleetMemberType.SHIP, "Nail", false);
      api.addToFleet(FleetSide.ENEMY, "sunder_Assault", FleetMemberType.SHIP, "Lasher", false);
      api.addToFleet(FleetSide.ENEMY, "sunder_CS", FleetMemberType.SHIP, "Cutter", false);
      api.addToFleet(FleetSide.ENEMY, "wolf_hegemony_Assault", FleetMemberType.SHIP, "Pax", false);
      api.addToFleet(FleetSide.ENEMY, "wolf_hegemony_Assault", FleetMemberType.SHIP, "Nox", false);
      api.addToFleet(FleetSide.ENEMY, "lasher_Strike", FleetMemberType.SHIP, "Slash", false);
      api.addToFleet(FleetSide.ENEMY, "lasher_Strike", FleetMemberType.SHIP, "Maul", false);
      api.addToFleet(FleetSide.ENEMY, "lasher_Strike", FleetMemberType.SHIP, "Grind", false);
      api.addToFleet(FleetSide.ENEMY, "hound_hegemony_Standard", FleetMemberType.SHIP, "Bulldog", false);
      api.addToFleet(FleetSide.ENEMY, "hound_hegemony_Standard", FleetMemberType.SHIP, "Pitbull", false);
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
