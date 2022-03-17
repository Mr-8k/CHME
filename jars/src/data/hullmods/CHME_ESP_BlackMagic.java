package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignUIAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.loading.FighterWingSpecAPI;

import java.util.List;

public class CHME_ESP_BlackMagic extends BaseHullMod {

/*    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.addListener(new DoNothing());
    }

    public boolean affectsOPCosts() {
        return true;
    }*/

    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {

            if (ship.getVariant().hasHullMod("CHME_ESP_LPCDroneConversion")) {

                List<FighterWingSpecAPI> allFighterWings = Global.getSettings().getAllFighterWingSpecs();

                for (FighterWingSpecAPI fighter : allFighterWings
                ) {
                    if (!fighter.hasTag("auto_fighter")) {
                        fighter.addTag("auto_fighter");
                        fighter.addTag("CHME_ESP_auto_fighterTemp");
                    }
                }
            } else {

                List<FighterWingSpecAPI> allFighterWings = Global.getSettings().getAllFighterWingSpecs();

                for (FighterWingSpecAPI fighter : allFighterWings
                ) {
                    if (fighter.hasTag("CHME_ESP_auto_fighterTemp") && fighter.hasTag("auto_fighter")) {
                        fighter.getTags().remove("auto_fighter");
                        fighter.getTags().remove("CHME_ESP_auto_fighterTemp");

                    }
                }
                ship.getVariant().removeMod("CHME_ESP_BlackMagic");
                //ship.getMutableStats().removeListener(DoNothing.class);
            }
    }

/*    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        List<FighterWingSpecAPI> allFighterWings = Global.getSettings().getAllFighterWingSpecs();

        for (FighterWingSpecAPI fighter : allFighterWings
        ) {
            if (fighter.hasTag("CHME_ESP_auto_fighterTemp")) {
                fighter.getTags().remove("auto_fighter");
                fighter.getTags().remove("CHME_ESP_auto_fighterTemp");

            }
        }
    }*/

    public boolean canBeAddedOrRemovedNow(ShipAPI ship, MarketAPI marketOrNull, CampaignUIAPI.CoreUITradeMode mode) {
        return false;
    }


/*    private static class DoNothing implements FighterOPCostModifier {

        public int getFighterOPCost(MutableShipStatsAPI stats, FighterWingSpecAPI fighter, int currCost) {
            //haha, ha one!
            return 1;
        }
    }*/
}
