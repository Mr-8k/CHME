package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

import static data.scripts.plugins.CHME_ESP_ModPlugin.CHME_ESP_modernIncompatChecker;

public class CHME_ESP_LPCDroneConversion extends BaseHullMod {

    public static int LOSSES = 0;

    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getDynamic().getStat(Stats.FIGHTER_CREW_LOSS_MULT).modifyMult(id, LOSSES);
    }

    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        if (ship.getVariant().hasHullMod("automated"))
            ship.getVariant().addMod("CHME_ESP_BlackMagic");
    }


    public String getDescriptionParam(int index, HullSize hullSize) {
        if (index == 0) return (100-LOSSES)+ "%";
        return null;
    }

    public boolean isApplicableToShip(ShipAPI ship) {
        if (!super.isApplicableToShip(ship)) {
            return false;

        } else if (!CHME_ESP_modernIncompatChecker(ship)) {
            return false;

        } else if (ship.getMutableStats().getNumFighterBays().getBaseValue() < 1) {
            return false;
        }
        else return true;
    }


    public String getUnapplicableReason(ShipAPI ship) {
        if (!super.isApplicableToShip(ship)) {
            return super.getUnapplicableReason(ship);
        } else if (ship.getMutableStats().getNumFighterBays().getBaseValue() < 1) {
            return ("Only applicable to carriers");
        }
        return super.getUnapplicableReason(ship);
    }

    public String getId() {
        return this.spec.getId();
    }
}