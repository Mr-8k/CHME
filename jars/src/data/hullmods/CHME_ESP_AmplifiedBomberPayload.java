package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;

import static data.scripts.plugins.CHME_ESP_ModPlugin.CHME_ESP_deckChecker;
import static data.scripts.plugins.CHME_ESP_ModPlugin.CHME_ESP_modernIncompatChecker;

public class CHME_ESP_AmplifiedBomberPayload extends BaseHullMod {

    public static float DAMAGE_PERCENT = 30.0F;
    public static float AGILITY_MULT = 0.8F;
    public static float MISSILE_AGILITY_MULT = 0.8F;

    public void applyEffectsToFighterSpawnedByShip(ShipAPI fighter, ShipAPI ship, String id) {
        if (fighter.getWing().getSpec().isBomber()) {

            MutableShipStatsAPI stats = fighter.getMutableStats();

            stats.getMaxSpeed().modifyMult(id, AGILITY_MULT);
            stats.getAcceleration().modifyMult(id, AGILITY_MULT);
            stats.getDeceleration().modifyMult(id, AGILITY_MULT);
            stats.getMaxTurnRate().modifyMult(id, AGILITY_MULT);
            stats.getTurnAcceleration().modifyMult(id, AGILITY_MULT);

            stats.getBallisticWeaponDamageMult().modifyPercent(id, DAMAGE_PERCENT);
            stats.getEnergyWeaponDamageMult().modifyPercent(id, DAMAGE_PERCENT);
            stats.getMissileWeaponDamageMult().modifyPercent(id, DAMAGE_PERCENT);

            stats.getMissileMaxTurnRateBonus().modifyMult(id, MISSILE_AGILITY_MULT);
            stats.getMissileTurnAccelerationBonus().modifyMult(id, MISSILE_AGILITY_MULT);

        }


        super.applyEffectsToFighterSpawnedByShip(fighter, ship, id);
    }

    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize, ShipAPI ship) {
        switch(index) {
            case 0:
                return Math.round(DAMAGE_PERCENT) + "%";
            case 1:
                return Math.round((AGILITY_MULT * 100) * -1 + 100) + "%";
            case 2:
                return Math.round((MISSILE_AGILITY_MULT * 100) * -1 + 100) + "%";
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

        } else if (!CHME_ESP_modernIncompatChecker(ship)) {
            return false;

        } else if (CHME_ESP_deckChecker(ship, false)) {
            return false;
        }
        else return true;
    }


    public String getUnapplicableReason(ShipAPI ship) {
        if (!super.isApplicableToShip(ship)) {
            return super.getUnapplicableReason(ship);
        } else if (CHME_ESP_deckChecker(ship, false))  {
            return ("Only applicable to carriers");
        }
        return super.getUnapplicableReason(ship);
    }
}
