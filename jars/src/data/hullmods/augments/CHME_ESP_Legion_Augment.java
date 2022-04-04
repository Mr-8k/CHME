package data.hullmods.augments;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignUIAPI.CoreUITradeMode;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

import java.awt.*;

import static data.scripts.plugins.CHME_ESP_ModPlugin.*;

public class CHME_ESP_Legion_Augment extends BaseHullMod {

    public static int PER_DECK_PPL_PENALTY = 20;
    public static float PENALTY_MITIGATION_FACTOR = 0.5F;
    //public final int PER_DECK_MAINT_PENALTY = 5;

    private static float getPenaltyMult(MutableShipStatsAPI stats, boolean installed) {
        HullSize size = stats.getVariant().getHullSize();
        int original = Math.round(stats.getNumFighterBays().getBaseValue()); // - stats.getVariant().getHullSpec().getBuiltInWings().size();
        int increased = 0;
        if (size == HullSize.CAPITAL_SHIP) {
            increased = 2;
        } else if (size == HullSize.CRUISER) {
            increased = 1;
        }

        int current = Math.round(stats.getNumFighterBays().getModifiedValue());
        if (!installed) {
            current += increased;
        }

        return (((float)current / (float)original) - 1) * PENALTY_MITIGATION_FACTOR + 1 ;
    }

    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        HullSize size = stats.getVariant().getHullSize();
        // int original = Math.round(stats.getNumFighterBays().getBaseValue()); //- stats.getVariant().getHullSpec().getBuiltInWings().size();
        int increased = 0;
        if (size == HullSize.CAPITAL_SHIP) {
            increased = 2;
        } else if (size == HullSize.CRUISER) {
            increased = 1;
        }

        stats.getNumFighterBays().modifyFlat(id, increased);

        float penaltyMult = getPenaltyMult(stats, true);
                // (float)Math.round(stats.getNumFighterBays().getModifiedValue()) / (float)original;

        stats.getFighterRefitTimeMult().modifyMult(id, penaltyMult);

        if (stats.getVariant().getHullSpec().getMinCrew() > 0.0F) {
            stats.getMinCrewMod().modifyFlat(id, (float)(increased * PER_DECK_PPL_PENALTY));
        }

    }

    public String getDescriptionParam(int index, HullSize hullSize, ShipAPI ship) {
        switch(index) {
            case 0:
                return "1";
            case 1:
                return "2";
            case 3:
                return String.format("%d", PER_DECK_PPL_PENALTY);
            default:
                return null;
        }
    }

    public void addPostDescriptionSection(TooltipMakerAPI tooltip, HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
        if (ship != null && !isForModSpec) {
            super.addPostDescriptionSection(tooltip, hullSize, ship, width, isForModSpec);
            boolean installed = ship.getVariant().hasHullMod(this.getId());
            tooltip.addSpacer(6.0F);
            tooltip.addSectionHeading("Manufactory Stress Effect", Alignment.MID, 0.0F);
            tooltip.addSpacer(6.0F);
            float penalty = getPenaltyMult(ship.getMutableStats(), installed);
            tooltip.addPara("Increase replacement time by a factor of %s", 0.0F, Color.YELLOW, String.format("%.2f", penalty));
        }
    }

    public boolean isApplicableToShip(ShipAPI ship) {
        if (!super.isApplicableToShip(ship)) {
            return false;
        } else if (CHME_ESP_deckChecker(ship, true))  {
            return false;
        } else if (!CHME_ESP_AugmentChecker(ship, getId()) || !CHME_ESP_modernIncompatChecker(ship)) {
            return false;
        } else {
            if (!this.isInPlayerFleet(ship)) {
                ShipVariantAPI variant = Global.getSettings().getVariant(ship.getVariant().getOriginalVariant());
                if (variant != null && !variant.hasHullMod(this.getId())) {
                    return false;
                }
            }

            int bays = (int)ship.getMutableStats().getNumFighterBays().getBaseValue();
            if (ship.getHullSpec().getBuiltInWings().size() >= bays) {
                return false;
            } else if (ship.getHullSize() != HullSize.FRIGATE && ship.getHullSize() != HullSize.DESTROYER) {
                return !ship.isStationModule();
            } else {
                return false;
            }
        }
    }

    public String getUnapplicableReason(ShipAPI ship) {
        if (!super.isApplicableToShip(ship)) {
            return super.getUnapplicableReason(ship);
        } else if (CHME_ESP_deckChecker(ship, true))  {
            return ("Only applicable to non-makeshift carriers");
        } else if (!CHME_ESP_AugmentChecker(ship, getId())) {
            return ("Only one carrier augment allowed per ship");
        } else if (ship.getMutableStats().getNumFighterBays().getBaseValue() <= (float)ship.getHullSpec().getBuiltInWings().size()) {
            return "Cannot affect ships with only built in wings";
        } else if (ship.isStationModule()) {
            return "This mod is too complicated to fit into a sub-module";
        } else {
            return ship.getHullSize() != HullSize.DESTROYER && ship.getHullSize() != HullSize.FRIGATE ?
                    super.getUnapplicableReason(ship) : "This mod is too complicated to fit into hull of this size";
        }
    }

    public boolean canBeAddedOrRemovedNow(ShipAPI ship, MarketAPI marketOrNull, CoreUITradeMode mode) {
        if (!super.canBeAddedOrRemovedNow(ship, marketOrNull, mode)) {
            return false;
        } else if (ship == null) {
            return false;
        } else if (!ship.getVariant().hasHullMod(getId())) {
            return true;
        } else {
            int currentBayCount = ship.getMutableStats().getNumFighterBays().getModifiedInt();
            if (ship.isCapital()) {
                if (ship.getVariant().getWing(currentBayCount - 1) != null) {
                    return false;
                }

                if (ship.getVariant().getWing(currentBayCount - 2) != null) {
                    return false;
                }
            }

            return !ship.isCruiser() || ship.getVariant().getWing(currentBayCount - 1) == null;
        }
    }

    public String getCanNotBeInstalledNowReason(ShipAPI ship, MarketAPI marketOrNull, CoreUITradeMode mode) {
        return !super.canBeAddedOrRemovedNow(ship, marketOrNull, mode) ?
                super.getCanNotBeInstalledNowReason(ship, marketOrNull, mode) : "Can't be removed when bays added by this hullmod still have fighters installed";
    }

    public String getId() {
        return this.spec.getId();
    }
}
