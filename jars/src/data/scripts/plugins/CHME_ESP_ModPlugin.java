package data.scripts.plugins;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.loading.FighterWingSpecAPI;
import com.fs.starfarer.api.loading.HullModSpecAPI;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CHME_ESP_ModPlugin extends BaseModPlugin {

    public static List<HullModSpecAPI> allHullmods = new ArrayList<>();
    public static List<FighterWingSpecAPI> allFighterWings = new ArrayList<>();
    public static List<String> carrierAugments = new ArrayList<>();
    public static List<String> modernHullmods = new ArrayList<>();
    public static Logger log = Global.getLogger(CHME_ESP_ModPlugin.class);

    @Override
    public void onApplicationLoad() {
// populate list with hullmods containing tag
        if (allHullmods == null || allHullmods.isEmpty())
            allHullmods = Global.getSettings().getAllHullModSpecs();

        for (HullModSpecAPI hullmod : allHullmods
             ) {
            if (hullmod.hasTag("CHME_ESP_carrier_augment") || hullmod.hasTag("carrier_core")) {
                //log.info("added " + hullmod.getId() + " to list");
                carrierAugments.add(hullmod.getId());
            }
            if (hullmod.getManufacturer().equalsIgnoreCase("Modern") || hullmod.getManufacturer().equalsIgnoreCase("Core Carrier")) {
                //log.info("added " + hullmod.getId() + " to list");
                modernHullmods.add(hullmod.getId());
            }

        }

// add auto_fighter to any 0 crew fighter on startup (for remnant ships)

        if (allFighterWings == null || allFighterWings.isEmpty())
            allFighterWings = Global.getSettings().getAllFighterWingSpecs();

        for (FighterWingSpecAPI fighter: allFighterWings
             ) {
            if (!fighter.hasTag("auto_fighter") && fighter.getVariant().getHullSpec().getMinCrew() == 0)
                fighter.addTag("auto_fighter");
        }
    }

// used to check for multiple augments
    public static boolean CHME_ESP_AugmentChecker(ShipAPI ship, String id) {

        Collection<String> hullmods = ship.getVariant().getHullMods();
        boolean applicable = true;

        for (String hullmod: hullmods
             ) {
            if (carrierAugments.contains(hullmod) && !hullmod.equals(id))
                {
                //log.info("blocked installation due to " + hullmod);
                    applicable = false;
                    break;
            }
        }

        return applicable;
    }

// used to check for cancer
    public static boolean CHME_ESP_modernIncompatChecker(ShipAPI ship) {

        Collection<String> hullmods = ship.getVariant().getHullMods();
        boolean applicable = true;

        for (String hullmod: hullmods
        ) {
            if (modernHullmods.contains(hullmod))
            {
                //log.info("blocked installation due to " + hullmod);
                applicable = false;
                break;
            }
        }

        return applicable;
    }

    public static boolean CHME_ESP_deckChecker (ShipAPI ship, boolean base) {

        boolean applicable = false;

        if (base) {
            if (ship.getMutableStats().getNumFighterBays().getBaseValue() < 1)
                applicable = true;
        } else
            if (ship.getMutableStats().getNumFighterBays().getModifiedValue() < 1)
                applicable = true;

        return applicable;
    }
}