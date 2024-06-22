package data.missions.fds_familiar_grounds;

import com.fs.starfarer.api.fleet.FleetGoal;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.mission.FleetSide;
import com.fs.starfarer.api.mission.MissionDefinitionAPI;
import com.fs.starfarer.api.mission.MissionDefinitionPlugin;

public class MissionDefinition implements MissionDefinitionPlugin {
   public void defineMission(MissionDefinitionAPI api) {
      api.initFleet(FleetSide.PLAYER, "FDS", FleetGoal.ATTACK, false);
      api.initFleet(FleetSide.ENEMY, "HSS", FleetGoal.ATTACK, true);
      api.setFleetTagline(FleetSide.PLAYER, "Syndicate Infiltration Squad");
      api.setFleetTagline(FleetSide.ENEMY, "Hegemony Fast Response Fleet");
      api.addBriefingItem("Leave no survivors!");
      api.addToFleet(FleetSide.PLAYER, "fds_retaliation_assault", FleetMemberType.SHIP, "Blue Leader", true);
      api.addToFleet(FleetSide.PLAYER, "fds_despair_standard", FleetMemberType.SHIP, "Blue I", false);
      api.addToFleet(FleetSide.PLAYER, "fds_rancour_support", FleetMemberType.SHIP, "Blue II", false);
      api.addToFleet(FleetSide.PLAYER, "fds_disturbance_standard", FleetMemberType.SHIP, "Red Leader", true);
      api.addToFleet(FleetSide.PLAYER, "fds_grief_mk_ii_assault", FleetMemberType.SHIP, "Red I", false);
      api.addToFleet(FleetSide.PLAYER, "fds_grief_mk_ii_strike", FleetMemberType.SHIP, "Red II", false);
      api.addToFleet(FleetSide.PLAYER, "fds_sorrow_standard", FleetMemberType.SHIP, "Green Leader", true);
      api.addToFleet(FleetSide.PLAYER, "fds_melancholy_assault", FleetMemberType.SHIP, "Green I", false);
      api.addToFleet(FleetSide.PLAYER, "fds_melancholy_assault", FleetMemberType.SHIP, "Green II", false);
      api.addToFleet(FleetSide.PLAYER, "fds_agony_assault", FleetMemberType.SHIP, "Green III", false);
      api.addToFleet(FleetSide.PLAYER, "fds_agony_assault", FleetMemberType.SHIP, "Green IV", false);
      api.addToFleet(FleetSide.PLAYER, "fds_agony_mk_ii_standard", FleetMemberType.SHIP, "Green V", false);
      api.addToFleet(FleetSide.ENEMY, "heron_Attack", FleetMemberType.SHIP, "Valiant", true);
      api.addToFleet(FleetSide.ENEMY, "falcon_xiv_Elite", FleetMemberType.SHIP, "Hunter", false);
      api.addToFleet(FleetSide.ENEMY, "hammerhead_Balanced", FleetMemberType.SHIP, "Hammer", false);
      api.addToFleet(FleetSide.ENEMY, "hammerhead_Support", FleetMemberType.SHIP, "Anvil", false);
      api.addToFleet(FleetSide.ENEMY, "sunder_Assault", FleetMemberType.SHIP, "Piercer", false);
      api.addToFleet(FleetSide.ENEMY, "enforcer_Balanced", FleetMemberType.SHIP, "Executioner", false);
      api.addToFleet(FleetSide.ENEMY, "wolf_hegemony_Assault", FleetMemberType.SHIP, "Pack leader", true);
      api.addToFleet(FleetSide.ENEMY, "lasher_Strike", FleetMemberType.SHIP, "Slasher", false);
      api.addToFleet(FleetSide.ENEMY, "vigilance_Strike", FleetMemberType.SHIP, "Guardian", false);
      api.addToFleet(FleetSide.ENEMY, "vigilance_Strike", FleetMemberType.SHIP, "Watchman", false);
      float width = 10000.0F;
      float height = 15000.0F;
      api.initMap(-width / 2.0F, width / 2.0F, -height / 2.0F, height / 2.0F);
      api.addAsteroidField(0.0F, 0.0F, 0.0F, width, 10.0F, 100.0F, 1000);
      float minX = -width / 2.0F;
      float minY = -height / 2.0F;
      api.addNebula(width * 0.5F, height * 0.5F, 20000.0F);
   }
}
