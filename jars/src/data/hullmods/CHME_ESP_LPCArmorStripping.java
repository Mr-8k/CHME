package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

import java.awt.*;
import java.util.List;

import static data.scripts.plugins.CHME_ESP_ModPlugin.CHME_ESP_modernIncompatChecker;

public class CHME_ESP_LPCArmorStripping extends BaseHullMod {

    public static float ARMOR_DIV = 5.0F;
    public static float SPD_BONUS_FLAT = 3.0F;
    public static float AGI_BONUS_PERCENT = 3.0F;
    public static float FLUX_CAP_BONUS_FLAT = 5.0F;
    public static float FLUX_DIS_BONUS_FLAT = 2.0F;

    public void applyEffectsToFighterSpawnedByShip(ShipAPI fighter, ShipAPI ship, String id) {
        MutableShipStatsAPI stats = fighter.getMutableStats();

        float armor = fighter.getVariant().getHullSpec().getArmorRating();

/*        stats.getArmorBonus().modifyMult(id, 0.0F);
        int xCount = fighter.getArmorGrid().getLeftOf() + fighter.getArmorGrid().getRightOf() + 1;
        int yCount = fighter.getArmorGrid().getAbove() + fighter.getArmorGrid().getBelow() + 1;

        for(int i = 0; i < xCount; ++i) {
            for(int j = 0; j < yCount; ++j) {
                fighter.getArmorGrid().setArmorValue(i, j, 0.0F);
            }
        }*/

        float bonusMult = armor / ARMOR_DIV;

        stats.getArmorBonus().modifyFlat(id, -armor);

        stats.getMaxSpeed().modifyFlat(id, bonusMult * SPD_BONUS_FLAT);

        stats.getAcceleration().modifyPercent(id, bonusMult * AGI_BONUS_PERCENT);
        stats.getDeceleration().modifyPercent(id, bonusMult * AGI_BONUS_PERCENT);
        stats.getMaxTurnRate().modifyPercent(id, bonusMult * AGI_BONUS_PERCENT);
        stats.getTurnAcceleration().modifyPercent(id, bonusMult * AGI_BONUS_PERCENT);

        stats.getFluxDissipation().modifyFlat(id, bonusMult * FLUX_DIS_BONUS_FLAT);
        stats.getFluxCapacity().modifyFlat(id, bonusMult * FLUX_CAP_BONUS_FLAT);

        super.applyEffectsToFighterSpawnedByShip(fighter, ship, id);
    }

    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize, ShipAPI ship) {
        switch(index) {
            case 0:
                return String.valueOf(Math.round(ARMOR_DIV));
            case 1:
                return String.valueOf(Math.round(SPD_BONUS_FLAT));
            case 2:
                return Math.round(AGI_BONUS_PERCENT) + "%";
            case 3:
                return String.valueOf(Math.round(FLUX_CAP_BONUS_FLAT));
            case 4:
                return String.valueOf(Math.round(FLUX_DIS_BONUS_FLAT));
            default:
                return null;
        }
    }

    public void addPostDescriptionSection(TooltipMakerAPI tooltip, ShipAPI.HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
        if (ship != null && !isForModSpec) {
            super.addPostDescriptionSection(tooltip, hullSize, ship, width, false);
            if (this.isApplicableToShip(ship) && ship.getVariant().getFittedWings() != null && !ship.getVariant().getFittedWings().isEmpty()) {
                tooltip.addSpacer(6.0F);
                tooltip.addSectionHeading("Current Bonus", Alignment.MID, 0.0F);
                List<String> fighters = ship.getVariant().getFittedWings();
                for (String fighter: fighters
                     ) {
                    ShipVariantAPI fighterVariant = Global.getSettings().getFighterWingSpec(fighter).getVariant();
                    String fighterName = fighterVariant.getFullDesignationWithHullName();
                    ShipHullSpecAPI fighterHullspec = fighterVariant.getHullSpec();

                    float fighterArmor = fighterHullspec.getArmorRating();
                    //float fighterSpeed = fighterHullspec.getEngineSpec().getMaxSpeed();
                    float fighterCap = fighterHullspec.getFluxCapacity();
                    float fighterDiss = fighterHullspec.getFluxDissipation();

                    float bonusMult =  fighterArmor / ARMOR_DIV;

                    tooltip.addSpacer(6.0F);
                    tooltip.addPara("%s: ", 0, Color.ORANGE, fighterName);
                    tooltip.addPara("Max speed %s.", 0, Color.ORANGE, String.valueOf(Math.round(SPD_BONUS_FLAT*bonusMult)));
                    tooltip.addPara("Maneuverability %s.", 0, Color.ORANGE, (Math.round(AGI_BONUS_PERCENT * bonusMult)) + "%");

                    tooltip.addPara("Flux capacity %s (base %s).", 0, Color.ORANGE, String.valueOf(Math.round(FLUX_CAP_BONUS_FLAT*bonusMult)),
                            String.valueOf(Math.round(fighterCap)));
                    tooltip.addPara("Flux dissipation %s (base %s).", 0, Color.ORANGE, String.valueOf(Math.round(FLUX_DIS_BONUS_FLAT*bonusMult)),
                            String.valueOf(Math.round(fighterDiss)));
                }
            }
        }
    }

    public String getId() {
        return this.spec.getId();
    }

    public boolean isApplicableToShip(ShipAPI ship) {
        if (!super.isApplicableToShip(ship)) {
            return false;

        } else if (!CHME_ESP_modernIncompatChecker(ship)) {
            return false;

        } else return !(ship.getMutableStats().getNumFighterBays().getBaseValue() < 1);
    }


    public String getUnapplicableReason(ShipAPI ship) {
        if (!super.isApplicableToShip(ship)) {
            return super.getUnapplicableReason(ship);
        } else if (ship.getMutableStats().getNumFighterBays().getBaseValue() < 1) {
            return ("Only applicable to carriers");
        }
        return super.getUnapplicableReason(ship);
    }
}
