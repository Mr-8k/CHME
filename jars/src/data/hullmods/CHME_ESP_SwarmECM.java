package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.FighterWingAPI;
import com.fs.starfarer.api.combat.ShipAPI;

import java.util.List;

import static data.scripts.plugins.CHME_ESP_ModPlugin.CHME_ESP_deckChecker;
import static data.scripts.plugins.CHME_ESP_ModPlugin.CHME_ESP_modernIncompatChecker;

public class CHME_ESP_SwarmECM extends BaseHullMod {

    public static float ECM_BONUS = 0.25F;

    public void advanceInCombat(ShipAPI ship, float amount) {
        float bonusECM = 0.0F;

        List<FighterWingAPI> allWings = ship.getAllWings();

        for (FighterWingAPI wing : allWings
             ) {
            List<ShipAPI> wingMembers = wing.getWingMembers();
            for (ShipAPI member : wingMembers
                 ) {
                if (member.isAlive())
                    bonusECM += ECM_BONUS;
            }
        }
        ship.getMutableStats().getDynamic().getMod("electronic_warfare_flat").modifyFlat(getId(), bonusECM);

    }

    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize, ShipAPI ship) {
        return String.format("%.2f", ECM_BONUS) + "%";
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

    public String getId() {
        return this.spec.getId();
    }
}
