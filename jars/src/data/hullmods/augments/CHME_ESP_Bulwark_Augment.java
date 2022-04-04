package data.hullmods.augments;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

import static data.scripts.plugins.CHME_ESP_ModPlugin.*;

public class CHME_ESP_Bulwark_Augment extends BaseHullMod {

    public static float REMAIN_RANGE = 600.0F;
    public static float DAMAGE_MULT = 0.5F;
    public static float INCREASE_PERCENT = 50.0F;

    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getFighterWingRange().modifyPercent(id, -100.0F);
        stats.getFighterWingRange().modifyFlat(id, REMAIN_RANGE);
    }

    public void applyEffectsToFighterSpawnedByShip(ShipAPI fighter, ShipAPI ship, String id) {
        MutableShipStatsAPI stats = fighter.getMutableStats();

        stats.getArmorDamageTakenMult().modifyMult(id, DAMAGE_MULT);
        stats.getShieldDamageTakenMult().modifyMult(id, DAMAGE_MULT);
        stats.getHullDamageTakenMult().modifyMult(id, DAMAGE_MULT);
        stats.getEmpDamageTakenMult().modifyMult(id, DAMAGE_MULT);

        stats.getBallisticWeaponRangeBonus().modifyPercent(id, INCREASE_PERCENT);
        stats.getEnergyWeaponRangeBonus().modifyPercent(id, INCREASE_PERCENT);
        stats.getProjectileSpeedMult().modifyPercent(id, INCREASE_PERCENT);
        stats.getMissileMaxSpeedBonus().modifyPercent(id, INCREASE_PERCENT);
        stats.getMissileWeaponRangeBonus().modifyPercent(id, INCREASE_PERCENT);

        super.applyEffectsToFighterSpawnedByShip(fighter, ship, id);
    }

    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize, ShipAPI ship) {
        switch(index) {
            case 0:
                return String.valueOf(Math.round(REMAIN_RANGE));
            default:
                return Math.round(INCREASE_PERCENT) + "%";
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
        } else if (CHME_ESP_deckChecker(ship, false))  {
            return false;
        }
        return true;
    }

    public String getUnapplicableReason(ShipAPI ship) {
        if (!super.isApplicableToShip(ship)) {
            return super.getUnapplicableReason(ship);
        } else if (!CHME_ESP_AugmentChecker(ship, getId())) {
            return ("Only one carrier augment allowed per ship");
        } else if (CHME_ESP_deckChecker(ship, false))  {
            return ("Only applicable to carriers");
        }
        return super.getUnapplicableReason(ship);
    }
}
