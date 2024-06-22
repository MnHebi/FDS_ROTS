package data.scripts.campaign;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.BaseOnMessageDeliveryScript;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.OnMessageDeliveryScript;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.comm.CommMessageAPI;
import com.fs.starfarer.api.campaign.comm.MessagePriority;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.events.CampaignEventManagerAPI;
import com.fs.starfarer.api.campaign.events.CampaignEventTarget;
import com.fs.starfarer.api.campaign.events.CampaignEventPlugin.CampaignEventCategory;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.characters.FullName.Gender;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.impl.campaign.events.BaseEventPlugin;
import com.fs.starfarer.api.impl.campaign.ids.Ranks;
import com.fs.starfarer.api.impl.campaign.rulecmd.AddRemoveCommodity;
import com.fs.starfarer.api.impl.campaign.submarkets.StoragePlugin;
import com.fs.starfarer.api.impl.campaign.tutorial.RogueMinerMiscFleetManager;
import com.fs.starfarer.api.impl.campaign.tutorial.SaveNagScript;
import com.fs.starfarer.api.impl.campaign.tutorial.TutorialLeashAssignmentAI;
import com.fs.starfarer.api.loading.WeaponSlotAPI;
import com.fs.starfarer.api.ui.HintPanelAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Misc.Token;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class FDS_Storyline extends BaseEventPlugin {
   public static final String FDS_STAGE = "$fds_stage";
   public static final String REASON = "FDS_Story";
   protected float elapsedDays = 0.0F;
   protected boolean ended = false;
   protected StarSystemAPI tatooineSystem;
   protected StarSystemAPI yavinSystem;
   protected PlanetAPI tatooine;
   protected PlanetAPI yavin;
   protected PlanetAPI pontus;
   protected PlanetAPI tetra;
   protected SectorEntityToken derinkuyu;
   protected SectorEntityToken probe;
   protected SectorEntityToken inner;
   protected SectorEntityToken fringe;
   protected SectorEntityToken detachment;
   protected SectorEntityToken relay;
   protected PersonAPI mainContact;
   protected PersonAPI rebelLeaderContact;
   protected PersonAPI imperialLeaderContact;
   protected PersonAPI masterYodaContact;
   protected PersonAPI darthVaderContact;
   protected FDS_Storyline.FDSStorylineStage stage;

   public FDS_Storyline() {
      this.stage = FDS_Storyline.FDSStorylineStage.CONTACT;
   }

   public void init(String type, CampaignEventTarget eventTarget) {
      super.init(type, eventTarget, false);
   }

   public void setParam(Object param) {
   }

   public void startEvent() {
      super.startEvent();
      this.tatooineSystem = Global.getSector().getStarSystem("Tatooine");
      this.yavinSystem = Global.getSector().getStarSystem("Yavin");
      this.tatooine = (PlanetAPI)this.tatooineSystem.getEntityById("TatooinePlanet");
      this.yavin = (PlanetAPI)this.yavinSystem.getEntityById("Yavin4");
      this.mainContact = createMainContact(this.tatooine);
      this.rebelLeaderContact = createRebelLeaderContact(this.yavin);
      String stageId = "start";
      Global.getSector().reportEventStage(this, stageId, Global.getSector().getPlayerFleet(), MessagePriority.DELIVER_IMMEDIATELY, this.createSetMessageLocationScript(this.tatooine));
      this.mainContact.getMemoryWithoutUpdate().set("$fds_mainContact", true);
      this.mainContact.getMemoryWithoutUpdate().set("$fds_eventRef", this);
      Misc.makeImportant(this.mainContact, "FDS_Story");
      this.updateStage(FDS_Storyline.FDSStorylineStage.CONTACT);
   }

   public static PersonAPI createMainContact(PlanetAPI planet) {
      PersonAPI contact = planet.getFaction().createRandomPerson();
      FullName name = new FullName("Han", "Solo", Gender.MALE);
      contact.setName(name);
      contact.setFaction(planet.getFaction().getId());
      contact.setRankId(Ranks.SPACE_CAPTAIN);
      contact.setPostId(Ranks.POST_SMUGGLER);
      contact.setPortraitSprite("graphics/FDS/portraits/han_solo.png");
      planet.getMarket().getCommDirectory().addPerson(contact);
      planet.getMarket().addPerson(contact);
      return contact;
   }

   public PersonAPI getMainContact() {
      return this.mainContact;
   }

   public static PersonAPI createRebelLeaderContact(PlanetAPI planet) {
      PersonAPI contact = planet.getFaction().createRandomPerson();
      FullName name = new FullName("Mon", "Mothma", Gender.FEMALE);
      contact.setName(name);
      contact.setFaction(planet.getFaction().getId());
      contact.setRankId(Ranks.CITIZEN);
      contact.setPostId(Ranks.POST_BASE_COMMANDER);
      contact.setPortraitSprite("graphics/FDS/portraits/mon_mothma.png");
      planet.getMarket().getCommDirectory().addPerson(contact);
      return contact;
   }

   public PersonAPI getRebelLeaderContact() {
      return this.rebelLeaderContact;
   }

   public static PersonAPI createMasterYodaContact(PlanetAPI planet) {
      PersonAPI contact = planet.getFaction().createRandomPerson();
      FullName name = new FullName("Master", "Yoda", Gender.MALE);
      contact.setName(name);
      contact.setFaction(planet.getFaction().getId());
      contact.setRankId(Ranks.CITIZEN);
      contact.setPostId(Ranks.POST_CITIZEN);
      contact.setPortraitSprite("graphics/FDS/portraits/master_yoda.png");
      planet.getMarket().getCommDirectory().addPerson(contact);
      return contact;
   }

   public PersonAPI getMasterYodaContact() {
      return this.masterYodaContact;
   }

   public static PersonAPI createImperialLeaderContact(PlanetAPI planet) {
      PersonAPI contact = planet.getFaction().createRandomPerson();
      FullName name = new FullName("Darth", "Sidious", Gender.MALE);
      contact.setName(name);
      contact.setFaction(planet.getFaction().getId());
      contact.setRankId(Ranks.CITIZEN);
      contact.setPostId(Ranks.POST_STATION_COMMANDER);
      contact.setPortraitSprite("graphics/FDS/portraits/darth_sidious.png");
      planet.getMarket().getCommDirectory().addPerson(contact);
      return contact;
   }

   public PersonAPI getImperialLeaderContact() {
      return this.imperialLeaderContact;
   }

   public static PersonAPI createDarthVaderContact(PlanetAPI planet) {
      PersonAPI contact = planet.getFaction().createRandomPerson();
      FullName name = new FullName("Darth", "Vader", Gender.MALE);
      contact.setName(name);
      contact.setFaction(planet.getFaction().getId());
      contact.setRankId(Ranks.CITIZEN);
      contact.setPostId(Ranks.POST_FLEET_COMMANDER);
      contact.setPortraitSprite("graphics/FDS/portraits/darth_vader.png");
      planet.getMarket().getCommDirectory().addPerson(contact);
      return contact;
   }

   public PersonAPI getDarthVaderContact() {
      return this.darthVaderContact;
   }

   protected void updateStage(FDS_Storyline.FDSStorylineStage stage) {
      this.stage = stage;
      Global.getSector().getMemoryWithoutUpdate().set("$fds_stage", stage.name());
   }

   protected void endEvent() {
      this.ended = true;
      Global.getSector().getMemoryWithoutUpdate().unset("$fds_stage");
   }

   public void advance(float amount) {
      if (this.isEventStarted()) {
         if (!this.isDone()) {
            float days = Global.getSector().getClock().convertToDays(amount);
            CampaignFleetAPI player = Global.getSector().getPlayerFleet();
            if (player != null) {
               this.elapsedDays += days;
               if (this.probe == null) {
                  this.probe = this.tatooineSystem.getEntityById("galatia_probe");
               }

               if (this.tetra == null) {
                  this.tetra = (PlanetAPI)this.tatooineSystem.getEntityById("tetra");
               }

               if (this.derinkuyu == null) {
                  this.derinkuyu = this.tatooineSystem.getEntityById("derinkuyu_station");
               }

               if (this.inner == null) {
                  this.inner = this.tatooineSystem.getEntityById("galatia_jump_point_alpha");
               }

               if (this.fringe == null) {
                  this.fringe = this.tatooineSystem.getEntityById("galatia_jump_point_fringe");
               }

               if (this.detachment == null) {
                  this.detachment = this.tatooineSystem.getEntityById("tutorial_security_detachment");
               }

               int count;
               if (this.stage == FDS_Storyline.FDSStorylineStage.GO_GET_AI_CORE) {
                  count = (int)player.getCargo().getCommodityQuantity("gamma_core");
                  float distToProbe = Misc.getDistance(player.getLocation(), this.probe.getLocation());
                  if (count > 0 && (!this.probe.isAlive() || distToProbe < 300.0F)) {
                     Global.getSector().reportEventStage(this, "salvage_core_end", Global.getSector().getPlayerFleet(), MessagePriority.DELIVER_IMMEDIATELY, this.createSetMessageLocationScript(this.tatooine));
                     Misc.makeImportant(this.mainContact, "FDS_Story");
                     this.updateStage(FDS_Storyline.FDSStorylineStage.GOT_AI_CORE);
                  }
               }

               if (this.stage == FDS_Storyline.FDSStorylineStage.GO_RECOVER_SHIPS) {
                  count = 0;

                  for(Iterator i$ = player.getFleetData().getMembersListCopy().iterator(); i$.hasNext(); ++count) {
                     FleetMemberAPI member = (FleetMemberAPI)i$.next();
                  }

                  int wrecks = 0;
                  Iterator i$ = this.tatooineSystem.getEntitiesWithTag("salvageable").iterator();

                  while(i$.hasNext()) {
                     SectorEntityToken entity = (SectorEntityToken)i$.next();
                     String id = entity.getCustomEntityType();
                     if (id != null && "wreck".equals(id)) {
                        ++wrecks;
                     }
                  }

                  if (count >= 5 || wrecks < 3) {
                     Global.getSector().reportEventStage(this, "ship_recovery_end", Global.getSector().getPlayerFleet(), MessagePriority.DELIVER_IMMEDIATELY, this.createSetMessageLocationScript(this.tatooine));
                     Misc.makeImportant(this.mainContact, "FDS_Story");
                     Misc.makeUnimportant(this.tetra, "FDS_Story");
                     this.updateStage(FDS_Storyline.FDSStorylineStage.RECOVERED_SHIPS);
                  }
               }

               if (this.stage == FDS_Storyline.FDSStorylineStage.GO_STABILIZE) {
                  boolean innerStable = this.inner.getMemoryWithoutUpdate().getExpire("$unstable") > 0.0F;
                  boolean fringeStable = this.fringe.getMemoryWithoutUpdate().getExpire("$unstable") > 0.0F;
                  if (innerStable || fringeStable) {
                     Global.getSector().reportEventStage(this, "stabilize_jump_point_done", Global.getSector().getPlayerFleet(), MessagePriority.DELIVER_IMMEDIATELY, this.createSetMessageLocationScript(this.tatooine));
                     Misc.makeImportant(this.mainContact, "FDS_Story");
                     Misc.makeUnimportant(this.inner, "FDS_Story");
                     this.updateStage(FDS_Storyline.FDSStorylineStage.STABILIZED);
                  }
               }

            }
         }
      }
   }

   public boolean callEvent(String ruleId, InteractionDialogAPI dialog, List<Token> params, Map<String, MemoryAPI> memoryMap) {
      String action = ((Token)params.get(0)).getString(memoryMap);
      CampaignFleetAPI playerFleet = Global.getSector().getPlayerFleet();
      CargoAPI cargo = playerFleet.getCargo();
      if (action.equals("startGetData")) {
         Global.getSector().reportEventStage(this, "sneak_start", Global.getSector().getPlayerFleet(), MessagePriority.DELIVER_IMMEDIATELY, this.createSetMessageLocationScript(this.derinkuyu));
         this.rebelLeaderContact.getMemoryWithoutUpdate().set("$fds_rebelContact", true);
         this.rebelLeaderContact.getMemoryWithoutUpdate().set("$fds_eventRef", this);
         Misc.makeImportant(this.rebelLeaderContact, "FDS_Story");
         Misc.makeUnimportant(this.mainContact, "FDS_Story");
         this.detachment.getMemoryWithoutUpdate().set("$patrolAllowTOff", true);
         this.updateStage(FDS_Storyline.FDSStorylineStage.GO_GET_DATA);
         this.saveNag();
      } else if (action.equals("endGetData")) {
         Global.getSector().reportEventStage(this, "sneak_end", Global.getSector().getPlayerFleet(), MessagePriority.DELIVER_IMMEDIATELY, this.createSetMessageLocationScript(this.tatooine));
         Misc.cleanUpMissionMemory(this.rebelLeaderContact.getMemoryWithoutUpdate(), "tut_");
         Misc.makeUnimportant(this.rebelLeaderContact, "FDS_Story");
         Misc.makeImportant(this.mainContact, "FDS_Story");
         this.updateStage(FDS_Storyline.FDSStorylineStage.GOT_DATA);
      } else if (action.equals("goSalvage")) {
         Global.getSector().reportEventStage(this, "salvage_core_start", Global.getSector().getPlayerFleet(), MessagePriority.DELIVER_IMMEDIATELY, this.createSetMessageLocationScript(this.pontus));
         Misc.makeUnimportant(this.mainContact, "FDS_Story");
         Misc.makeImportant(this.probe, "FDS_Story");
         this.updateStage(FDS_Storyline.FDSStorylineStage.GO_GET_AI_CORE);
         this.saveNag();
      } else if (action.equals("goRecover")) {
         Global.getSector().reportEventStage(this, "ship_recovery_start", Global.getSector().getPlayerFleet(), MessagePriority.DELIVER_IMMEDIATELY, this.createSetMessageLocationScript(this.tetra));
         Misc.makeUnimportant(this.mainContact, "FDS_Story");
         Misc.makeImportant(this.tetra, "FDS_Story");
         FleetMemberAPI member = Global.getFactory().createFleetMember(FleetMemberType.SHIP, "mudskipper_Standard");
         playerFleet.getFleetData().addFleetMember(member);
         AddRemoveCommodity.addFleetMemberGainText(member, dialog.getTextPanel());
         this.updateStage(FDS_Storyline.FDSStorylineStage.GO_RECOVER_SHIPS);
      } else if (action.equals("goStabilize")) {
         Global.getSector().reportEventStage(this, "stabilize_jump_point", Global.getSector().getPlayerFleet(), MessagePriority.DELIVER_IMMEDIATELY, this.createSetMessageLocationScript(this.inner));
         Misc.makeUnimportant(this.mainContact, "FDS_Story");
         Misc.makeImportant(this.inner, "FDS_Story");
         this.addWeaponsToStorage();
         this.inner.getMemoryWithoutUpdate().set("$canStabilize", true);
         this.fringe.getMemoryWithoutUpdate().set("$canStabilize", true);
         this.updateStage(FDS_Storyline.FDSStorylineStage.GO_STABILIZE);
         this.saveNag();
      } else if (action.equals("reportDelivered")) {
         Global.getSector().reportEventStage(this, "end", Global.getSector().getPlayerFleet(), MessagePriority.DELIVER_IMMEDIATELY, this.createSetMessageLocationScript(this.tatooine));
         Misc.makeUnimportant(this.mainContact, "FDS_Story");
         Misc.cleanUpMissionMemory(this.mainContact.getMemoryWithoutUpdate(), "FDS_Story_");
         this.updateStage(FDS_Storyline.FDSStorylineStage.DONE);
         CampaignEventManagerAPI eventManager = Global.getSector().getEventManager();
         MarketAPI jangala = Global.getSector().getEconomy().getMarket("jangala");
         if (jangala != null) {
         }

         this.endEvent();
      } else if (action.equals("printRefitHint")) {
         String refit = Global.getSettings().getControlStringForEnumName("CORE_REFIT");
         String autofit = Global.getSettings().getControlStringForEnumName("REFIT_MANAGE_VARIANTS");
         String transponder = "";
         if (!playerFleet.isTransponderOn()) {
            transponder = "\n\nAlso: you'll need to re-dock with your transponder turned on to take advantage of Ancyra's facilities.";
         }

         dialog.getTextPanel().addPara("(Once this conversation is over, press %s to open the refit screen. After selecting a specific ship, you can press %s to %s - pick a desired loadout, and the ship will be automatically refitted to match it, using what weapons are available." + transponder + ")", Misc.getHighlightColor(), new String[]{refit, autofit, "\"autofit\""});
      }

      return true;
   }

   public static void endGalatiaPortionOfMission() {
      StarSystemAPI system = Global.getSector().getStarSystem("galatia");
      PlanetAPI ancyra = (PlanetAPI)system.getEntityById("ancyra");
      PlanetAPI pontus = (PlanetAPI)system.getEntityById("pontus");
      PlanetAPI tetra = (PlanetAPI)system.getEntityById("tetra");
      SectorEntityToken derinkuyu = system.getEntityById("derinkuyu_station");
      SectorEntityToken probe = system.getEntityById("galatia_probe");
      SectorEntityToken inner = system.getEntityById("galatia_jump_point_alpha");
      SectorEntityToken fringe = system.getEntityById("galatia_jump_point_fringe");
      SectorEntityToken relay = system.getEntityById("ancyra_relay");
      Global.getSector().getCharacterData().addAbility("transponder");
      Global.getSector().getCharacterData().addAbility("go_dark");
      Global.getSector().getCharacterData().addAbility("sensor_burst");
      Global.getSector().getCharacterData().addAbility("emergency_burn");
      Global.getSector().getCharacterData().addAbility("sustained_burn");
      Global.getSector().getCharacterData().addAbility("scavenge");
      Global.getSector().getCharacterData().addAbility("distress_call");
      FactionAPI hegemony = Global.getSector().getFaction("hegemony");
      if (hegemony.getRelToPlayer().getRel() < 0.0F) {
         hegemony.getRelToPlayer().setRel(0.0F);
      }

      Global.getSector().getEconomy().addMarket(ancyra.getMarket(), true);
      Global.getSector().getEconomy().addMarket(derinkuyu.getMarket(), true);
      HintPanelAPI hints = Global.getSector().getCampaignUI().getHintPanel();
      if (hints != null) {
         hints.clearHints(false);
      }

      CampaignEventManagerAPI eventManager = Global.getSector().getEventManager();
      RogueMinerMiscFleetManager script = new RogueMinerMiscFleetManager(derinkuyu);

      for(int i = 0; i < 20; ++i) {
         script.advance(1.0F);
      }

      system.addScript(script);
      Iterator i$ = system.getFleets().iterator();

      while(i$.hasNext()) {
         CampaignFleetAPI fleet = (CampaignFleetAPI)i$.next();
         if ("pirates".equals(fleet.getFaction().getId())) {
            fleet.removeScriptsOfClass(TutorialLeashAssignmentAI.class);
         }
      }

      inner.getMemoryWithoutUpdate().unset("$unstable");
      inner.getMemoryWithoutUpdate().unset("$canStabilize");
      fringe.getMemoryWithoutUpdate().unset("$unstable");
      fringe.getMemoryWithoutUpdate().unset("$canStabilize");
   }

   protected void saveNag() {
      if (!Global.getSector().hasScript(SaveNagScript.class)) {
         Global.getSector().addScript(new SaveNagScript(10.0F));
      }

   }

   public void addWeaponsToStorage() {
      StoragePlugin plugin = (StoragePlugin)this.tatooine.getMarket().getSubmarket("storage").getPlugin();
      plugin.setPlayerPaidToUnlock(true);
      CargoAPI cargo = plugin.getCargo();
      CampaignFleetAPI player = Global.getSector().getPlayerFleet();
      Iterator i$ = player.getFleetData().getMembersListCopy().iterator();

      while(i$.hasNext()) {
         FleetMemberAPI member = (FleetMemberAPI)i$.next();
         Iterator i$ = member.getVariant().getHullSpec().getAllWeaponSlotsCopy().iterator();

         while(i$.hasNext()) {
            WeaponSlotAPI slot = (WeaponSlotAPI)i$.next();
            String weaponId = this.getWeaponForSlot(slot);
            if (weaponId != null) {
               cargo.addWeapons(weaponId, 1);
            }
         }
      }

      cargo.addFighters("broadsword_wing", 1);
      cargo.addFighters("piranha_wing", 1);
      cargo.addSupplies(50.0F);
      cargo.sort();
   }

   public String getWeaponForSlot(WeaponSlotAPI slot) {
      switch(slot.getWeaponType()) {
      case BALLISTIC:
      case COMPOSITE:
      case HYBRID:
      case UNIVERSAL:
         switch(slot.getSlotSize()) {
         case LARGE:
            return this.pick("mark9", "hephag", "hellbore");
         case MEDIUM:
            return this.pick("arbalest", "heavymortar", "shredder");
         case SMALL:
            return this.pick("lightmg", "lightac", "lightmortar");
         default:
            return null;
         }
      case MISSILE:
      case SYNERGY:
         switch(slot.getSlotSize()) {
         case LARGE:
            return this.pick("hammerrack");
         case MEDIUM:
            return this.pick("pilum", "annihilatorpod");
         case SMALL:
            return this.pick("harpoon", "sabot", "annihilator");
         default:
            return null;
         }
      case ENERGY:
         switch(slot.getSlotSize()) {
         case LARGE:
            return this.pick("autopulse", "hil");
         case MEDIUM:
            return this.pick("miningblaster", "gravitonbeam", "pulselaser");
         case SMALL:
            return this.pick("mininglaser", "taclaser", "pdlaser", "ioncannon");
         }
      }

      return null;
   }

   public String pick(String... strings) {
      return strings[(new Random()).nextInt(strings.length)];
   }

   public OnMessageDeliveryScript createSetMessageLocationScript(final SectorEntityToken entity) {
      return new BaseOnMessageDeliveryScript() {
         public void beforeDelivery(CommMessageAPI message) {
            if (entity != null && entity.getContainingLocation() instanceof StarSystemAPI) {
               message.setStarSystemId(entity.getContainingLocation().getId());
            } else {
               message.setStarSystemId(FDS_Storyline.this.tatooineSystem.getId());
            }

            message.setCenterMapOnEntity(entity);
         }
      };
   }

   public Map<String, String> getTokenReplacements() {
      Map<String, String> map = super.getTokenReplacements();
      addPersonTokens(map, "mainContact", this.mainContact);
      if (this.rebelLeaderContact != null) {
         addPersonTokens(map, "rebelLeaderContact", this.rebelLeaderContact);
      }

      if (this.mainContact != null) {
         addPersonTokens(map, "mainContact", this.mainContact);
      }

      map.put("$systemName", this.tatooineSystem.getNameWithLowercaseType());
      return map;
   }

   public String[] getHighlights(String stageId) {
      List<String> result = new ArrayList();
      if (!"posting".equals(stageId) && "success".equals(stageId)) {
      }

      return (String[])result.toArray(new String[0]);
   }

   public Color[] getHighlightColors(String stageId) {
      return super.getHighlightColors(stageId);
   }

   public boolean isDone() {
      return this.ended;
   }

   public String getEventName() {
      if (this.stage == FDS_Storyline.FDSStorylineStage.CONTACT) {
         return "Contact " + this.mainContact.getPost() + " " + this.mainContact.getName().getLast();
      } else if (this.stage == FDS_Storyline.FDSStorylineStage.DELIVER_REPORT) {
         return "Deliver Report to Jangala";
      } else {
         return this.stage == FDS_Storyline.FDSStorylineStage.DONE ? "Deliver Report to Jangala - completed" : "Stabilize the Jump-points";
      }
   }

   public CampaignEventCategory getEventCategory() {
      return CampaignEventCategory.MISSION;
   }

   public String getEventIcon() {
      return Global.getSettings().getSpriteName("campaignMissions", "FDS_StoryIcon");
   }

   public String getCurrentImage() {
      return this.tatooine.getFaction().getLogo();
   }

   public static enum FDSStorylineStage {
      CONTACT,
      GO_GET_DATA,
      GOT_DATA,
      GO_GET_AI_CORE,
      GOT_AI_CORE,
      GO_RECOVER_SHIPS,
      RECOVERED_SHIPS,
      GO_STABILIZE,
      STABILIZED,
      DELIVER_REPORT,
      DONE;
   }
}
