package data.scripts.listeners;

import com.fs.starfarer.api.GameState;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.listeners.AdvanceableListener;
import java.awt.Color;

public class CHME_ESP_Focus_AugmentGlowingEffectListener implements AdvanceableListener {
    public static final Color JITTER_COLOR = new Color(255, 216, 64, 55);
    public static final Color JITTER_UNDER_COLOR = new Color(255, 216, 64, 192);
    private final ShipAPI _fighter;

    public CHME_ESP_Focus_AugmentGlowingEffectListener(ShipAPI fighter) {
        this._fighter = fighter;
    }

    public void advance(float amount) {
        if (this._fighter.isAlive() && !this._fighter.isHulk()) {
            if (Global.getCurrentState() == GameState.COMBAT && Global.getCombatEngine() != null) {
                float jitterRangeBonus = 3.0F;
                this._fighter.setJitter(this, JITTER_COLOR, 1.0F, 3, 0.0F, jitterRangeBonus);
                this._fighter.setJitterUnder(this, JITTER_UNDER_COLOR, 1.0F, 3, 0.0F, 3.0F + jitterRangeBonus);
            }
        } else {
            this._fighter.removeListener(this);
        }

    }
}
