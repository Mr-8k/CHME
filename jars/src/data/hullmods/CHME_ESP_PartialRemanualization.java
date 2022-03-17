package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;

import static data.scripts.plugins.CHME_ESP_ModPlugin.CHME_ESP_modernIncompatChecker;

public class CHME_ESP_PartialRemanualization extends BaseHullMod {

/*    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {

        ShipVariantAPI variant = ship.getVariant();

        if (variant.hasHullMod("automated") && (variant.hasHullMod("CHME_ESP_PartialRemanualization"))) {
            variant.addSuppressedMod("automated");
            variant.addPermaMod("CHME_ESP_automated");
        }
    }*/

    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {

        ShipVariantAPI variant = stats.getVariant();

        if (variant.hasHullMod("automated") && (variant.hasHullMod("CHME_ESP_PartialRemanualization"))) {
            variant.addSuppressedMod("automated");
            variant.addPermaMod("CHME_ESP_automated");
        }
    }
        


    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
        if (index == 0) return "one to one ratio";
        if (index == 1) return "NOT";
        return null;
    }

    public boolean isApplicableToShip(ShipAPI ship) {
        if (!super.isApplicableToShip(ship)) {
            return false;

        } else if (!CHME_ESP_modernIncompatChecker(ship)) {
            return false;

        } else if (!ship.getMutableStats().getVariant().hasHullMod("automated") && !ship.getMutableStats().getVariant().hasHullMod("CHME_ESP_automated")) {
            return false;
        }
        else return true;
    }


    public String getUnapplicableReason(ShipAPI ship) {
        if (!super.isApplicableToShip(ship)) {
            return super.getUnapplicableReason(ship);
        } else if (!ship.getMutableStats().getVariant().hasHullMod("automated")) {
            return ("Only applicable to automated ships");
        }
        return super.getUnapplicableReason(ship);
    }

    public String getId() {
        return this.spec.getId();
    }
}


