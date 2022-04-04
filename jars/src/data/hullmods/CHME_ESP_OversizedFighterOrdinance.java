package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;

import static data.scripts.plugins.CHME_ESP_ModPlugin.CHME_ESP_deckChecker;
import static data.scripts.plugins.CHME_ESP_ModPlugin.CHME_ESP_modernIncompatChecker;

public class CHME_ESP_OversizedFighterOrdinance extends BaseHullMod {

    public static float ROF_BONUS_PERCENT = 20.0F;
    public static float FLUX_BONUS_PERCENT = 20.0F;
    public static float MAX_AGILITY_PENALTY_MULT = 0.2F;

    public void applyEffectsToFighterSpawnedByShip(ShipAPI fighter, ShipAPI ship, String id) {
        if (!fighter.getWing().getSpec().isBomber()) {

            MutableShipStatsAPI stats = fighter.getMutableStats();

            float agilityMult = 1 - MAX_AGILITY_PENALTY_MULT;

            stats.getBallisticRoFMult().modifyPercent(id, ROF_BONUS_PERCENT);
            stats.getEnergyRoFMult().modifyPercent(id, ROF_BONUS_PERCENT);
            stats.getMissileRoFMult().modifyPercent(id, ROF_BONUS_PERCENT);

            stats.getBallisticAmmoRegenMult().modifyPercent(id, ROF_BONUS_PERCENT);
            stats.getEnergyAmmoRegenMult().modifyPercent(id, ROF_BONUS_PERCENT);
            stats.getMissileAmmoRegenMult().modifyPercent(id, ROF_BONUS_PERCENT);


            stats.getFluxDissipation().modifyPercent(id, FLUX_BONUS_PERCENT);

            stats.getMaxSpeed().modifyMult(id, agilityMult);
            stats.getAcceleration().modifyMult(id, agilityMult);
            stats.getDeceleration().modifyMult(id, agilityMult);
            stats.getMaxTurnRate().modifyMult(id, agilityMult);
            stats.getTurnAcceleration().modifyMult(id, agilityMult);
        }

        super.applyEffectsToFighterSpawnedByShip(fighter, ship, id);
    }

    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize, ShipAPI ship) {
        switch(index) {
            case 0:
                return Math.round(ROF_BONUS_PERCENT) + "%";
            case 1:
                return Math.round(FLUX_BONUS_PERCENT) + "%";
            default:
                return Math.round(MAX_AGILITY_PENALTY_MULT * 100) + "%";
        }
    }

    public String getId() {
        return this.spec.getId();
    }

    public boolean isApplicableToShip(ShipAPI ship) {
        if (!super.isApplicableToShip(ship)) {
            return false;

        } else if (!CHME_ESP_modernIncompatChecker(ship)) {
            return false;

        } else if ((CHME_ESP_deckChecker(ship, false)) ) {
            return false;
        }
            else return true;
    }


    public String getUnapplicableReason(ShipAPI ship) {
        if (!super.isApplicableToShip(ship)) {
            return super.getUnapplicableReason(ship);
        } else if ((CHME_ESP_deckChecker(ship, false)) ) {
            return ("Only applicable to carriers");
        }
        return super.getUnapplicableReason(ship);
    }
}
