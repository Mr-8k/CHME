package data.hullmods.augments;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignUIAPI.CoreUITradeMode;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.listeners.FighterOPCostModifier;
import com.fs.starfarer.api.loading.FighterWingSpecAPI;
import com.fs.starfarer.api.loading.WingRole;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.scripts.listeners.CHME_ESP_Focus_AugmentGlowingEffectListener;

import java.awt.*;

import static data.scripts.plugins.CHME_ESP_ModPlugin.CHME_ESP_AugmentChecker;
import static data.scripts.plugins.CHME_ESP_ModPlugin.CHME_ESP_modernIncompatChecker;

public class CHME_ESP_Focus_Augment extends BaseHullMod {

    public static int PER_DECK_DP_COMPENSATION = 2;

    public static float FIGHTER_COST_INCREASE_PERCENT = 100.0F;

    public static float FIGHTER_DAMAGE_TAKEN_MULT = 0.5F;
    public static float FIGHTER_TIME_MULT = 1.5F;
    public static float FIGHTER_DAMAGE_BONUS_PERCENT = 30.0F;

    public static float INTERCEPTOR_SPAWN_RATE_PERCENT = 100.0F;
    public static float INTERCEPTOR_TIME_MULT = 1.5F;
    //public static float INTERCEPTOR_DAMAGE_PERCENT = 50.0F;
    public static float INTERCEPTOR_BONUS_AA_PERCENT = 50.0F;

    public static float BOMBER_AMMO_HP_BONUS_PERCENT = 200.0F;
    public static float BOMBER_MISSILE_DAMAGE_PERCENT = 50.0F;
    public static float BOMBER_DAMAGE_TAKEN_MULT = 0.5F;

    public static float SUPPORT_SPAWN_RATE_PERCENT = 50.0F;
    public static float SUPPORT_DAMAGE_PERCENT = 50.0F;
    public static float SUPPORT_AA_DAMAGE_PERCENT = 50.0F;


    private static WingRole getRole(ShipAPI ship) {
        if (ship.getVariant().getFittedWings().size() == 0) {
            return null;
        } else {
            String wingId = ship.getVariant().getFittedWings().get(0);
            if (wingId == null) {
                return null;
            } else {
                FighterWingSpecAPI spec = Global.getSettings().getFighterWingSpec(wingId);
                return spec == null ? null : spec.getRole();
            }
        }
    }

    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {

        stats.getNumFighterBays().modifyFlat(id, 99.0F);
        stats.getNumFighterBays().modifyMult(id, 0.01F);

        stats.addListener(new FighterOPCostModifier() {
            public int getFighterOPCost(MutableShipStatsAPI stats, FighterWingSpecAPI fighter, int currCost) {
                return currCost + (int)fighter.getOpCost(null);
            }
        });

        int reduced = (int)stats.getNumFighterBays().getBaseValue() - 1;

        stats.getDynamic().getMod("deployment_points_mod").modifyFlat(this.getId(), (float)(-PER_DECK_DP_COMPENSATION * Math.max(reduced, 0)));
    }

    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        super.applyEffectsAfterShipCreation(ship, id);
        if (ship.getVariant().getFittedWings().size() != 0) {
            if (ship.getVariant().getWing(0) == null) {
                for(int i = 0; i < ship.getVariant().getWings().size(); ++i) {
                    if (ship.getVariant().getWing(i) != null) {
                        ship.getVariant().setWingId(0, ship.getVariant().getWing(i).getId());
                        ship.getVariant().setWingId(i, "");
                    }
                }
            }

            WingRole role = getRole(ship);

            MutableShipStatsAPI stats = ship.getMutableStats();

            if (role != WingRole.ASSAULT && role != WingRole.FIGHTER && role != WingRole.BOMBER) {
                if (role == WingRole.INTERCEPTOR) {
                    stats.getFighterRefitTimeMult().modifyMult(id, 1/((INTERCEPTOR_SPAWN_RATE_PERCENT/100) + 1));
                } else if (role == WingRole.SUPPORT) {
                    stats.getFighterRefitTimeMult().modifyMult(id, 1/((SUPPORT_SPAWN_RATE_PERCENT/100) + 1));
                }
            }

        }
    }

    public void applyEffectsToFighterSpawnedByShip(ShipAPI fighter, ShipAPI ship, String id) {
        if (fighter.getWing() != null && fighter.getWing().getSourceShip() == ship) {

            MutableShipStatsAPI stats = fighter.getMutableStats();

            stats.getEccmChance().modifyFlat(id, 1.0F);

            stats.getDynamic().getMod("pd_ignores_flares").modifyFlat(id, 1.0F);

            stats.getEmpDamageTakenMult().modifyMult(id, 0.0F);

            stats.getBreakProb().modifyFlat(id, 100.0F);

            WingRole wingRole = getRole(ship);

            fighter.addListener(new CHME_ESP_Focus_AugmentGlowingEffectListener(fighter));

            if (wingRole != WingRole.ASSAULT && wingRole != WingRole.FIGHTER) {

                if (wingRole == WingRole.BOMBER) {
                    stats.getShieldDamageTakenMult().modifyMult(id, BOMBER_DAMAGE_TAKEN_MULT);
                    stats.getArmorDamageTakenMult().modifyMult(id, BOMBER_DAMAGE_TAKEN_MULT);
                    stats.getHullDamageTakenMult().modifyMult(id, BOMBER_DAMAGE_TAKEN_MULT);

                    stats.getMissileHealthBonus().modifyPercent(id, BOMBER_AMMO_HP_BONUS_PERCENT);
                    stats.getMissileWeaponDamageMult().modifyPercent(id, BOMBER_MISSILE_DAMAGE_PERCENT);
                }

                else if (wingRole == WingRole.INTERCEPTOR) {
                    //float timeBonus = 2.0F;
                    //float timeCounter = 1.0F / timeBonus;

                    stats.getTimeMult().modifyMult(id, INTERCEPTOR_TIME_MULT);
                    stats.getMaxSpeed().modifyMult(id, 1/INTERCEPTOR_TIME_MULT);
                    stats.getAcceleration().modifyMult(id, 1/INTERCEPTOR_TIME_MULT);
                    stats.getDeceleration().modifyMult(id, 1/INTERCEPTOR_TIME_MULT);
                    stats.getMaxTurnRate().modifyMult(id, 1/INTERCEPTOR_TIME_MULT);
                    stats.getTurnAcceleration().modifyMult(id, 1/INTERCEPTOR_TIME_MULT);

/*                    stats.getBallisticWeaponDamageMult().modifyPercent(id, INTERCEPTOR_DAMAGE_PERCENT);
                    stats.getEnergyWeaponDamageMult().modifyPercent(id, INTERCEPTOR_DAMAGE_PERCENT);
                    stats.getMissileWeaponDamageMult().modifyPercent(id, INTERCEPTOR_DAMAGE_PERCENT);*/

                    stats.getDamageToFighters().modifyPercent(id, INTERCEPTOR_BONUS_AA_PERCENT);
                    stats.getDamageToMissiles().modifyPercent(id, INTERCEPTOR_BONUS_AA_PERCENT);
                }

                else if (wingRole == WingRole.SUPPORT) {
                    stats.getBallisticWeaponDamageMult().modifyPercent(id, SUPPORT_DAMAGE_PERCENT);
                    stats.getEnergyWeaponDamageMult().modifyPercent(id, SUPPORT_DAMAGE_PERCENT);
                    stats.getMissileWeaponDamageMult().modifyPercent(id, SUPPORT_DAMAGE_PERCENT);

                    stats.getDamageToFighters().modifyPercent(id, SUPPORT_AA_DAMAGE_PERCENT);
                    stats.getDamageToMissiles().modifyPercent(id, SUPPORT_AA_DAMAGE_PERCENT);
                }
            } else {
                stats.getShieldDamageTakenMult().modifyMult(id, FIGHTER_DAMAGE_TAKEN_MULT);
                stats.getArmorDamageTakenMult().modifyMult(id, FIGHTER_DAMAGE_TAKEN_MULT);
                stats.getHullDamageTakenMult().modifyMult(id, FIGHTER_DAMAGE_TAKEN_MULT);

                stats.getTimeMult().modifyMult(id, FIGHTER_TIME_MULT);

                stats.getBallisticWeaponDamageMult().modifyPercent(id, FIGHTER_DAMAGE_BONUS_PERCENT);
                stats.getEnergyWeaponDamageMult().modifyPercent(id, FIGHTER_DAMAGE_BONUS_PERCENT);
                stats.getMissileWeaponDamageMult().modifyPercent(id, FIGHTER_DAMAGE_BONUS_PERCENT);
            }

            super.applyEffectsToFighterSpawnedByShip(fighter, ship, id);
        }
    }

    public void addPostDescriptionSection(TooltipMakerAPI tooltip, HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
        if (ship != null && !isForModSpec) {
            super.addPostDescriptionSection(tooltip, hullSize, ship, width, isForModSpec);
            if (this.isApplicableToShip(ship)) {
                tooltip.addSpacer(6.0F);
                tooltip.addSectionHeading("Current Bonus", Alignment.MID, 0.0F);
                tooltip.addSpacer(6.0F);
                int reduced = (int)ship.getMutableStats().getNumFighterBays().getBaseValue() - 1;
                if (reduced > 0) {
                    tooltip.addPara("Reduce deployment point cost by %s\n", 0.0F, Color.ORANGE, String.format("%d", PER_DECK_DP_COMPENSATION * reduced));
                }

                if (ship.getVariant().getFittedWings().size() == 0) {
                    tooltip.addPara("Bonus Type: %s\n", 0.0F, Color.ORANGE, "No bonus");
                    tooltip.addPara("There is no fighter LPC installed", Color.ORANGE, 0.0F);
                } else {
                    WingRole wingRole = getRole(ship);
                    if (wingRole == WingRole.ASSAULT) {
                        tooltip.addPara("Bonus Type: %s\n", 0.0F, Color.ORANGE, "Assault Fighter");
                        tooltip.addPara("Reduce damage taken by %s", 0.0F, Color.YELLOW, String.format("%d%%", Math.round(FIGHTER_DAMAGE_TAKEN_MULT * 100)));
                        tooltip.addPara("Increase movement and rate of fire by %s", 0.0F, Color.YELLOW, String.format("%d%%", Math.round((FIGHTER_TIME_MULT - 1) * 100)));
                        tooltip.addPara("Increase weapon damage by %s", 0.0F, Color.YELLOW, String.format("%d%%", Math.round(FIGHTER_DAMAGE_BONUS_PERCENT)));
                    }

                    if (wingRole == WingRole.FIGHTER) {
                        tooltip.addPara("Bonus Type: %s\n", 0.0F, Color.ORANGE, "General Fighter");
                        tooltip.addPara("Reduce damage taken by %s", 0.0F, Color.YELLOW, String.format("%d%%", Math.round(FIGHTER_DAMAGE_TAKEN_MULT * 100)));
                        tooltip.addPara("Increase movement and rate of fire by %s", 0.0F, Color.YELLOW, String.format("%d%%", Math.round((FIGHTER_TIME_MULT - 1) * 100)));
                        tooltip.addPara("Increase weapon damage by %s", 0.0F, Color.YELLOW, String.format("%d%%", Math.round(FIGHTER_DAMAGE_BONUS_PERCENT)));
                    }

                    if (wingRole == WingRole.BOMBER) {
                        tooltip.addPara("Bonus Type: %s\n", 0.0F, Color.ORANGE, "Bomber");
                        tooltip.addPara("Reduce damage taken by %s", 0.0F, Color.YELLOW, String.format("%d%%", Math.round(BOMBER_MISSILE_DAMAGE_PERCENT)));
                        tooltip.addPara("Increase missile health by %s", 0.0F, Color.YELLOW, String.format("%d%%", Math.round(BOMBER_AMMO_HP_BONUS_PERCENT)));
                        tooltip.addPara("Increase missile damage by %s", 0.0F, Color.YELLOW, String.format("%d%%", Math.round(BOMBER_DAMAGE_TAKEN_MULT * 100)));
                    }

                    if (wingRole == WingRole.INTERCEPTOR) {
                        tooltip.addPara("Bonus Type: %s\n", 0.0F, Color.ORANGE, "Interceptor");
                        tooltip.addPara("Increase replacement speed by %s", 0.0F, Color.YELLOW, String.format("%d%%", Math.round(INTERCEPTOR_SPAWN_RATE_PERCENT)));
                        tooltip.addPara("Increase target lock speed and rate of fire by %s", 0.0F, Color.YELLOW, String.format("%d%%",
                                Math.round((INTERCEPTOR_TIME_MULT - 1) * 100)));
                        //tooltip.addPara("Reduce weapon damage by %s", 0.0F, Color.YELLOW, String.format("%d%%", Math.round(INTERCEPTOR_DAMAGE_PERCENT)));
                        tooltip.addPara("Increase damage to fighter and missile by %s", 0.0F, Color.YELLOW, String.format("%d%%", Math.round(INTERCEPTOR_BONUS_AA_PERCENT)));
                    }

                    if (wingRole == WingRole.SUPPORT) {
                        tooltip.addPara("Bonus Type: %s\n", 0.0F, Color.ORANGE, "Support Fighter");
                        tooltip.addPara("Increase weapon damage by %s", 0.0F, Color.YELLOW, String.format("%d%%", Math.round(SUPPORT_DAMAGE_PERCENT)));
                        tooltip.addPara("Increase damage to fighter and missile by additional %s", 0.0F, Color.YELLOW, String.format("%d%%", Math.round(SUPPORT_AA_DAMAGE_PERCENT)));
                        tooltip.addPara("Increase replacement speed by %s", 0.0F, Color.YELLOW, String.format("%d%%", Math.round(SUPPORT_SPAWN_RATE_PERCENT)));
                    }

                    tooltip.addPara("\nImmune to EMP", Color.ORANGE, 0.0F);
                    tooltip.addPara("Weaponry ignores flares", Color.ORANGE, 0.0F);
                }
            }
        }
    }

    public boolean isApplicableToShip(ShipAPI ship) {
        if (!super.isApplicableToShip(ship)) {
            return false;

        } else if (ship.getMutableStats().getNumFighterBays().getBaseValue() < 1) {
            return false;

        } else if (!CHME_ESP_AugmentChecker(ship, getId()) || !CHME_ESP_modernIncompatChecker(ship)) {
            return false;

        } else if (ship.getHullSpec().getBuiltInWings().size() > 0) {
            return false;

        } else if (ship.getVariant().getFittedWings().size() > 1 && !ship.getVariant().hasHullMod(getId())) {
            return false;
        }

            return true;
    }


    public String getUnapplicableReason(ShipAPI ship) {
        if (!super.isApplicableToShip(ship)) {
            return super.getUnapplicableReason(ship);

        } else if (ship.getMutableStats().getNumFighterBays().getBaseValue() < 1) {
            return ("Only applicable to carriers");

        } else if (!CHME_ESP_AugmentChecker(ship, getId()) || !CHME_ESP_modernIncompatChecker(ship)) {
            return ("Only one carrier augment allowed per ship");
        }
          else if (ship.getHullSpec().getBuiltInWings().size() > 0) {
            return "The hullmod cannot modify built in wings";

        } else if (ship.getVariant().getFittedWings().size() > 1 && !ship.getVariant().hasHullMod(getId())) {
            return "Can only affect one wing";
            }

            return super.getUnapplicableReason(ship);
        }


    public boolean canBeAddedOrRemovedNow(ShipAPI ship, MarketAPI marketOrNull, CoreUITradeMode mode) {
        if (!super.canBeAddedOrRemovedNow(ship, marketOrNull, mode)) {
            return false;
        } else if (ship == null) {
            return false;
        } else if (ship.getVariant().hasHullMod("CHME_ESP_Focus_Augment")) {
            return true;
        } else {
            return ship.getVariant().getFittedWings().size() == 0;
        }
    }

    public String getCanNotBeInstalledNowReason(ShipAPI ship, MarketAPI marketOrNull, CoreUITradeMode mode) {
        return !super.canBeAddedOrRemovedNow(ship, marketOrNull, mode) ? super.getCanNotBeInstalledNowReason(ship, marketOrNull, mode) : "Can only be installed when no fighter LPC is installed";
    }

    public String getDescriptionParam(int index, HullSize hullSize, ShipAPI ship) {
        if (index == 0) return "" + (int) FIGHTER_COST_INCREASE_PERCENT + "%";
        return null;
    }

    public String getId() {
        return this.spec.getId();
    }

    public boolean affectsOPCosts() {
        return true;
    }
}
