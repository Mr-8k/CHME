package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class CHME_ESP_automated extends BaseHullMod {

    public static float MAX_CR_PENALTY = 1f;

    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getMinCrewMod().modifyMult(id, 1);
        stats.getMaxCrewMod().modifyMult(id, 1);

        float cargo = stats.getVariant().getHullSpec().getCargo();
        stats.getMaxCrewMod().modifyFlat(id, cargo);

        if (isInPlayerFleet(stats)) {
            stats.getMaxCombatReadiness().modifyFlat(id, -MAX_CR_PENALTY, "Automated ship penalty");
        }
    }

    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        ship.setInvalidTransferCommandTarget(true);

        ShipVariantAPI variant = ship.getVariant();

        if (!variant.hasHullMod("CHME_ESP_PartialRemanualization")) {
            variant.removeSuppressedMod("automated");
            variant.removePermaMod("CHME_ESP_automated");
        }
    }



    public String getDescriptionParam(int index, HullSize hullSize) {
        if (index == 0) return "" + (int)Math.round(MAX_CR_PENALTY * 100f);
        return null;
    }

    public void addPostDescriptionSection(TooltipMakerAPI tooltip, HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
        if (isInPlayerFleet(ship)) {
            float opad = 10f;
            tooltip.addPara("Automated ships require specialized equipment and expertise to maintain. In a " +
                            "fleet lacking these, they're virtually useless, with their maximum combat " +
                            "readiness being reduced by %s.", opad, Misc.getHighlightColor(),
                    "" + (int)Math.round(MAX_CR_PENALTY * 100f) + "%");
        }
    }
}
