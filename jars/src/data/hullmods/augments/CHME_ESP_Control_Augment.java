package data.hullmods.augments;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;

import static data.scripts.plugins.CHME_ESP_ModPlugin.CHME_ESP_AugmentChecker;
import static data.scripts.plugins.CHME_ESP_ModPlugin.CHME_ESP_modernIncompatChecker;

public class CHME_ESP_Control_Augment extends BaseHullMod {

    public static float FIGHTER_RANGE_BONUS_PERCENT = 50.0F;
    public static float SIGHT_BONUS_FLAT = 2000.0F;
    public static float COMMAND_BONUS_PERCENT = 100.0F;

    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {

        stats.getFighterWingRange().modifyPercent(id, FIGHTER_RANGE_BONUS_PERCENT);
        stats.getSightRadiusMod().modifyFlat(id, SIGHT_BONUS_FLAT);

    }

    public void advanceInCombat(ShipAPI ship, float amount) {
        if (ship.isAlive() && !ship.isHulk()) {
            CombatEngineAPI engine = Global.getCombatEngine();
            if (engine != null) {
                CombatFleetManagerAPI manager = engine.getFleetManager(ship.getOriginalOwner());
                if (manager != null) {
                    DeployedFleetMemberAPI member = manager.getDeployedFleetMember(ship);
                    if (member != null) {
                        boolean apply = ship == engine.getPlayerShip();
                        //PersonAPI commander = null;
                        if (member.getMember() != null) {
                            member.getMember().getFleetCommander();
                        }

                        //apply |= commander != null && ship.getCaptain() == commander;

                        if (apply) {
                            ship.getMutableStats().getDynamic().getMod("command_point_rate_flat").modifyFlat(getId(), COMMAND_BONUS_PERCENT / 100);
                        } else {
                            ship.getMutableStats().getDynamic().getMod("command_point_rate_flat").unmodify(getId());
                        }

                    }
                }
            }
        }
    }

    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize, ShipAPI ship) {
        switch (index) {
            case 0:
                return Math.round(FIGHTER_RANGE_BONUS_PERCENT) + "%";
            case 1:
                return String.valueOf(Math.round(SIGHT_BONUS_FLAT));
            case 2:
                return Math.round(COMMAND_BONUS_PERCENT) + "%";
            default:
                return null;
        }
    }


    public String getId() {
        return this.spec.getId();
    }

    public boolean isApplicableToShip(ShipAPI ship) {
        if (!super.isApplicableToShip(ship)) {
            return false;
        } else if (!CHME_ESP_AugmentChecker(ship, getId()) || !CHME_ESP_modernIncompatChecker(ship)) {
            return false;
        } else if (ship.getMutableStats().getNumFighterBays().getBaseValue() < 1) {
            return false;
        }
        return true;
    }

    public String getUnapplicableReason(ShipAPI ship) {
        if (!super.isApplicableToShip(ship)) {
            return super.getUnapplicableReason(ship);
        } else if (!CHME_ESP_AugmentChecker(ship, getId())) {
            return ("Only one carrier augment allowed per ship");
        } else if (ship.getMutableStats().getNumFighterBays().getBaseValue() < 1) {
            return ("Only applicable to carriers");
        }
        return super.getUnapplicableReason(ship);
    }
}

