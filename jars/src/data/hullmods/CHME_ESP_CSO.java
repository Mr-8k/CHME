package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

import java.awt.*;

import static data.scripts.plugins.CHME_ESP_ModPlugin.CHME_ESP_deckChecker;
import static data.scripts.plugins.CHME_ESP_ModPlugin.CHME_ESP_modernIncompatChecker;

public class CHME_ESP_CSO extends BaseHullMod {


/*
    private static final Map<HullSize, Float> speed = new HashMap();
    static {
        speed.put(HullSize.FRIGATE, 50f);
        speed.put(HullSize.DESTROYER, 30f);
        speed.put(HullSize.CRUISER, 20f);
        speed.put(HullSize.CAPITAL_SHIP, 10f);
    }*/

    public static float PEAK_FACTOR = 3.0F;
    public static float RANGE_REDUCE_MULT = 0.6F;
    public static float REPLACEMENT_TIME_MULT = 3.0F;


    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {

/*        stats.getMaxSpeed().modifyFlat(id, speed.get(hullSize));
        stats.getAcceleration().modifyFlat(id, speed.get(hullSize) * 2f);
        stats.getDeceleration().modifyFlat(id, speed.get(hullSize) * 2f);*/

        stats.getZeroFluxMinimumFluxLevel().modifyFlat(id, 2.0F);

        stats.getPeakCRDuration().modifyMult(id, 1/PEAK_FACTOR);
        stats.getFighterWingRange().modifyMult(id, 1 - RANGE_REDUCE_MULT);
        stats.getFighterRefitTimeMult().modifyMult(id, 1/REPLACEMENT_TIME_MULT);

        stats.getDynamic().getStat("replacement_rate_decrease_mult").modifyMult(id, 0.0F);
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
        } else if ((CHME_ESP_deckChecker(ship, false)) ) {
            return ("Only applicable to carriers");
        }
        return super.getUnapplicableReason(ship);
    }

    public String getDescriptionParam(int index, HullSize hullSize, ShipAPI ship) {
        switch(index) {
            case 0:
                return Math.round(100 - 1/REPLACEMENT_TIME_MULT * 100) + "%";
            case 1:
                return Math.round(PEAK_FACTOR) + "";
            case 2:
                return Math.round(RANGE_REDUCE_MULT * 100) + "%";
            default:
                return null;
        }
    }


    public String getId() {
        return this.spec.getId();
    }

    private final Color color = new Color(255,100,255,255);
    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        ship.getEngineController().fadeToOtherColor(this, color, null, 1f, 0.4f);
        ship.getEngineController().extendFlame(this, 0.25f, 0.25f, 0.25f);
		}
}
