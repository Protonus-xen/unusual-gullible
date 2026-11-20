package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShieldAPI.*;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipSystemAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.scripts.util.UGH_MD;
import java.awt.Color;

public class UGH_InvertedDilator extends BaseHullMod {
        private static final String ugh_dilator_id = "UGH_InvertedDilator";
	public static final Color JITTER_COLOR = new Color(0,120,200,125);
	public static final Color JITTER_UNDER_COLOR = Misc.setAlpha(JITTER_COLOR, 25);
        
	public static final float PHASE_FLUX_ACTIVE_MULT = 4f;
	public static final float FLUX_DISS_MULT = 0.5f;
        
	public static final float PHASE_COOLDOWN_MULT = 5f;
	public static final float SECOND_PER_SECOND = 3f;
        
        @Override
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
                stats.getPhaseCloakUpkeepCostBonus().modifyMult(id, 0f);
                stats.getPhaseCloakCooldownBonus().modifyMult(id, PHASE_COOLDOWN_MULT);
                stats.getPhaseCloakActivationCostBonus().modifyMult(id, PHASE_FLUX_ACTIVE_MULT);
	}

        protected static final float LOAD_OF_BULL = 3f;
	@Override
	public void addPostDescriptionSection(TooltipMakerAPI tooltip, HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
		float opad = 10f;
		float pad = 3f;
		Color h = Misc.getHighlightColor();
                Color bad = Misc.getNegativeHighlightColor();
                Color good = Misc.getPositiveHighlightColor();
                
                LabelAPI bullet;
                tooltip.setBulletedListMode(" • ");
                
		tooltip.addSectionHeading(UGH_MD.base("feature"), Alignment.MID, opad);
                bullet = tooltip.addPara(UGH_MD.ipd("fe_phase"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), h,
                    "" + (int) PHASE_FLUX_ACTIVE_MULT + "x", "" + (int) PHASE_COOLDOWN_MULT + " times");
                bullet = tooltip.addPara(UGH_MD.ipd("fe_cooldown"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), h,
                    "" + (int) SECOND_PER_SECOND + " times");
                bullet = tooltip.addPara(UGH_MD.ipd("fe_flux"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), bad,
                    "" + (float) FLUX_DISS_MULT + "x");
                
		tooltip.addSectionHeading(UGH_MD.base("compat"), Alignment.MID, opad);
                bullet = tooltip.addPara(UGH_MD.ipd("comp_PAF"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), good,
                    "" + "" + "");
		bullet.setHighlight(UGH_MD.ipd("comp_PAF_h"));
		bullet.setHighlightColors(h);
                
            tooltip.setBulletedListMode(null);
	}
	
        @Override
	public String getDescriptionParam(int index, HullSize hullSize) {
            /*if (index == 0) return "prevents the ship from phasing";
            if (index == 1) return "4";
            if (index == 2) return "5";
            if (index == 3) return "50%";*/
            return null;
	}
	
        @Override
	public String getUnapplicableReason(ShipAPI ship) {
            if (ship.getHullSpec().getDefenseType() != ShieldType.PHASE)
                return "This modification is exclusive to Phase Ships";
            if (ship.getVariant().getHullMods().contains("ugh_phasearmor"))
                return "Incompatible with Phasic Armor Frame";
            return null;
	}
	
        @Override
	public boolean isApplicableToShip(ShipAPI ship) {
            return ship != null && ship.getHullSpec().getDefenseType() == ShieldType.PHASE &&
				!ship.getVariant().getHullMods().contains("ugh_phasearmor");
	}
	
        @Override
	public void advanceInCombat(ShipAPI ship, float amount) {
            if (!ship.isAlive()) return;
	
            MutableShipStatsAPI stats = ship.getMutableStats();
            ShipAPI playerShip = Global.getCombatEngine().getPlayerShip();
            
            ShipSystemAPI cloak = ship.getPhaseCloak();
            if (cloak == null) cloak = ship.getSystem();
            
            //if (ship.getPhaseCloak().isActive()){
            if (ship.isPhased() && cloak.isActive()) {
		cloak.deactivate();
            } else;
            float maxRangeBonus = 10f;
            
            if (ship.isAlive()){
                if (cloak.isCoolingDown()){
                    stats.getTimeMult().modifyMult(ugh_dilator_id, 3.0f);
                    stats.getFluxDissipation().modifyMult(ugh_dilator_id, FLUX_DISS_MULT);
                
                    ship.setJitterUnder(this, JITTER_UNDER_COLOR, 1, 7, 0f, 3f + maxRangeBonus);
                    ship.setJitter(this, JITTER_COLOR, 1, 2, 0f, 0 + maxRangeBonus);
                
                    if (ship == playerShip) {
                        Global.getCombatEngine().getTimeMult().modifyMult(ugh_dilator_id, 1f / 3f);
                        Global.getCombatEngine().maintainStatusForPlayerShip("ugh_dilator_ui1", "graphics/icons/hullsys/active_flare_launcher.png",
			"Time Dilation:", "3 seconds per second", false);
                    }
                } else {
                    stats.getTimeMult().unmodify(ugh_dilator_id);
                    stats.getFluxDissipation().unmodify(ugh_dilator_id);
                    Global.getCombatEngine().getTimeMult().unmodify(ugh_dilator_id);
                }
            } else { // Redundancies just in case...
                stats.getTimeMult().unmodify(ugh_dilator_id);
                stats.getFluxDissipation().unmodify(ugh_dilator_id);
                Global.getCombatEngine().getTimeMult().unmodify(ugh_dilator_id);
            }
	}
}