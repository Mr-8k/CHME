package data.hullmods.augments;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;

import static data.scripts.plugins.CHME_ESP_ModPlugin.CHME_ESP_AugmentChecker;
import static data.scripts.plugins.CHME_ESP_ModPlugin.CHME_ESP_modernIncompatChecker;

public class CHME_ESP_Alacrity_Augment extends BaseHullMod {

    public static final float SPEED_BONUS_PERCENT = 50.0F;
    public static final float CARRIER_ZERO_FLUX_PERCENT = 0.02F;

    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getZeroFluxMinimumFluxLevel().modifyFlat(id, CARRIER_ZERO_FLUX_PERCENT);
    }

    public void applyEffectsToFighterSpawnedByShip(ShipAPI fighter, ShipAPI ship, String id) {

        MutableShipStatsAPI stats = fighter.getMutableStats();

        stats.getAcceleration().modifyPercent(id, SPEED_BONUS_PERCENT);
        stats.getMaxSpeed().modifyPercent(id, SPEED_BONUS_PERCENT);
        stats.getDeceleration().modifyPercent(id, SPEED_BONUS_PERCENT);

        super.applyEffectsToFighterSpawnedByShip(fighter, ship, id);
    }

    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize, ShipAPI ship) {
        switch (index) {
            case 0:
                return Math.round(SPEED_BONUS_PERCENT) + "%";
            case 1:
                return Math.round(CARRIER_ZERO_FLUX_PERCENT * 100) + "%";
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
