package data.scripts.world;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.LocationAPI;
import com.fs.starfarer.api.campaign.RepLevel;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.SectorGeneratorPlugin;
import com.fs.starfarer.api.impl.campaign.shared.SharedData;
import data.scripts.world.systems.FDS_ArchimedesSystem;
import data.scripts.world.systems.FDS_EuclidSystem;
import data.scripts.world.systems.FDS_HypatiaSystem;
import data.scripts.world.systems.FDS_PythagorasSystem;

public class FDSGen implements SectorGeneratorPlugin {
   public void generate(SectorAPI sector) {
      (new FDS_ArchimedesSystem()).generate(sector);
      (new FDS_EuclidSystem()).generate(sector);
      (new FDS_HypatiaSystem()).generate(sector);
      (new FDS_PythagorasSystem()).generate(sector);
      LocationAPI hyper = Global.getSector().getHyperspace();
      SectorEntityToken fringeLabel = hyper.addCustomEntity("fds_fringe_label", "The Fringe Worlds", "fds_fringe_label", (String)null);
      fringeLabel.setFixedLocation(8000.0F, 20000.0F);
      SharedData.getData().getPersonBountyEventData().addParticipatingFaction("fringe_defence_syndicate");
      this.setVanillaRelationships(sector);
   }

   private void setVanillaRelationships(SectorAPI sector) {
      FactionAPI syndicate = sector.getFaction("fringe_defence_syndicate");
      FactionAPI player = sector.getFaction("player");
      FactionAPI hegemony = sector.getFaction("hegemony");
      FactionAPI tritachyon = sector.getFaction("tritachyon");
      FactionAPI pirates = sector.getFaction("pirates");
      FactionAPI independent = sector.getFaction("independent");
      FactionAPI church = sector.getFaction("luddic_church");
      FactionAPI path = sector.getFaction("luddic_path");
      FactionAPI kol = sector.getFaction("knights_of_ludd");
      FactionAPI diktat = sector.getFaction("sindrian_diktat");
      FactionAPI persean = sector.getFaction("persean");
      FactionAPI lion = sector.getFaction("lions_guard");
      FactionAPI remnant = sector.getFaction("remnant");
      syndicate.setRelationship(player.getId(), RepLevel.NEUTRAL);
      syndicate.setRelationship(hegemony.getId(), RepLevel.VENGEFUL);
      syndicate.setRelationship(tritachyon.getId(), RepLevel.FAVORABLE);
      syndicate.setRelationship(pirates.getId(), RepLevel.VENGEFUL);
      syndicate.setRelationship(independent.getId(), RepLevel.FRIENDLY);
      syndicate.setRelationship(church.getId(), RepLevel.INHOSPITABLE);
      syndicate.setRelationship(path.getId(), RepLevel.VENGEFUL);
      syndicate.setRelationship(kol.getId(), RepLevel.INHOSPITABLE);
      syndicate.setRelationship(diktat.getId(), RepLevel.INHOSPITABLE);
      syndicate.setRelationship(persean.getId(), RepLevel.INHOSPITABLE);
      syndicate.setRelationship(lion.getId(), RepLevel.INHOSPITABLE);
      syndicate.setRelationship(remnant.getId(), RepLevel.HOSTILE);
      this.setModRelationships(syndicate);
   }

   private void setModRelationships(FactionAPI faction) {
      faction.setRelationship("approlight", -0.8F);
      faction.setRelationship("blackrock_driveyards", 0.05F);
      faction.setRelationship("cabal", -0.6F);
      faction.setRelationship("citadeldefenders", 0.1F);
      faction.setRelationship("corvus_scavengers", 0.0F);
      faction.setRelationship("darkspire", -0.1F);
      faction.setRelationship("dassault_mikoyan", 0.15F);
      faction.setRelationship("diableavionics", 0.01F);
      faction.setRelationship("exigency", -0.2F);
      faction.setRelationship("exipirated", 0.0F);
      faction.setRelationship("famous_bounty", 0.0F);
      faction.setRelationship("immortallight", 0.0F);
      faction.setRelationship("interstellarimperium", -0.4F);
      faction.setRelationship("junk_pirates", -0.2F);
      faction.setRelationship("Lte", 0.0F);
      faction.setRelationship("mayorate", -0.3F);
      faction.setRelationship("metelson", 0.3F);
      faction.setRelationship("neutrinocorp", -0.1F);
      faction.setRelationship("noir", 0.0F);
      faction.setRelationship("nomads", 0.0F);
      faction.setRelationship("ORA", -0.25F);
      faction.setRelationship("pack", 0.0F);
      faction.setRelationship("pbc", 0.0F);
      faction.setRelationship("scavengers", 0.0F);
      faction.setRelationship("SCY", 0.1F);
      faction.setRelationship("spire", 0.0F);
      faction.setRelationship("shadow_industry", -0.7F);
      faction.setRelationship("syndicate_asp", -0.2F);
      faction.setRelationship("sun_ice", 0.0F);
      faction.setRelationship("sun_ici", 0.0F);
      faction.setRelationship("templars", -0.4F);
      faction.setRelationship("the_deserter", 0.0F);
      faction.setRelationship("tiandong", 0.2F);
   }
}
