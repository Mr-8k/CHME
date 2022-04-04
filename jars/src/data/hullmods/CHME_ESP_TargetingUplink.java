package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static data.scripts.plugins.CHME_ESP_ModPlugin.CHME_ESP_deckChecker;
import static data.scripts.plugins.CHME_ESP_ModPlugin.CHME_ESP_modernIncompatChecker;

public class CHME_ESP_TargetingUplink extends BaseHullMod {

    private static final Map<HullSize, Float> mag = new HashMap<>();
    static {
        mag.put(HullSize.FIGHTER, 10f);
        mag.put(HullSize.FRIGATE, 10f);
        mag.put(HullSize.DESTROYER, 20f);
        mag.put(HullSize.CRUISER, 40f);
        mag.put(HullSize.CAPITAL_SHIP, 60f);
    }

    private static final Map<HullSize, Float> mag2 = new HashMap<>();
    static {
        mag2.put(HullSize.FIGHTER, 0f);
        mag2.put(HullSize.FRIGATE, 0f);
        mag2.put(HullSize.DESTROYER, 0f);
        mag2.put(HullSize.CRUISER, 35f);
        mag2.put(HullSize.CAPITAL_SHIP, 50f);
    }

    public void applyEffectsToFighterSpawnedByShip(ShipAPI fighter, ShipAPI ship, String id) {

       MutableShipStatsAPI stats = fighter.getMutableStats();
       HullSize hullSize = ship.getHullSize();

       if (ship.getVariant().hasHullMod("targetingunit")) {
           stats.getBallisticWeaponRangeBonus().modifyPercent(id, mag.get(hullSize));
           stats.getEnergyWeaponRangeBonus().modifyPercent(id, mag.get(hullSize));
       }

       if (ship.getVariant().hasHullMod("dedicated_targeting_core")){
           stats.getBallisticWeaponRangeBonus().modifyPercent(id, mag2.get(hullSize));
           stats.getEnergyWeaponRangeBonus().modifyPercent(id, mag2.get(hullSize));
       }
    }

/*    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize, ShipAPI ship) {
        return "";
    }*/

    public void addPostDescriptionSection(TooltipMakerAPI tooltip, ShipAPI.HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
        if (ship != null && !isForModSpec) {
            super.addPostDescriptionSection(tooltip, hullSize, ship, width, false);
            if (this.isApplicableToShip(ship) && ship.getVariant().getFittedWings() != null && !ship.getVariant().getFittedWings().isEmpty()) {
                tooltip.addSpacer(6.0F);
                tooltip.addSectionHeading("Current Bonus", Alignment.MID, 0.0F);
                tooltip.addSpacer(6.0F);
                if (!ship.getVariant().hasHullMod("dedicated_targeting_core") && !ship.getVariant().hasHullMod("targetingunit"))
                    tooltip.addPara("No bonus.", 0);
                else if (ship.getVariant().hasHullMod("targetingunit")) {
                    tooltip.addPara("%s " + "extra ballistic and energy weapon range.", 0, Color.ORANGE,
                            (mag.get(hullSize)).intValue() + "%");
                } else if (ship.getVariant().hasHullMod("dedicated_targeting_core")) {
                    tooltip.addPara("%s " + "extra ballistic and energy weapon range.", 0, Color.ORANGE,
                            (mag2.get(hullSize)).intValue() + "%");
                }
            }
        }
    }

    public boolean isApplicableToShip(ShipAPI ship) {
        if (!super.isApplicableToShip(ship)) {
            return false;

        } else if (!CHME_ESP_modernIncompatChecker(ship)) {
            return false;

        } else return (CHME_ESP_deckChecker(ship, false)) ;
    }


    public String getUnapplicableReason(ShipAPI ship) {
        if (!super.isApplicableToShip(ship)) {
            return super.getUnapplicableReason(ship);
        } else if (CHME_ESP_deckChecker(ship, false))  {
            return ("Only applicable to carriers");
        }
        return super.getUnapplicableReason(ship);
    }

    public String getId() {
        return this.spec.getId();
    }
}
