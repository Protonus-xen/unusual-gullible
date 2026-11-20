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

public class UGH_PhaseArmor extends BaseHullMod {
        private static final String ugh_phasealloy_id = "UGH_PhaseAlloy";
	public static final Color JITTER_COLOR = new Color(120,60,120,155);
	public static final Color JITTER_UNDER_COLOR = Misc.setAlpha(JITTER_COLOR, 55);
        
	public static final float PHASE_FLUX_ACTIVE_MULT = 5f;
	public static final float FIRE_RATE_PENALTY = 33f;
        
	public static final float PHASE_COOLDOWN_MULT = 7f;
	public static final float DAMAGE_RESISTANCE = 50f;
        
        @Override
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
                stats.getPhaseCloakUpkeepCostBonus().modifyMult(id, 0f);
                stats.getPhaseCloakCooldownBonus().modifyMult(id, PHASE_COOLDOWN_MULT);
                stats.getPhaseCloakActivationCostBonus().modifyMult(id, PHASE_FLUX_ACTIVE_MULT);
	}

	@Override
	public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
		ship.setShield(ShieldType.NONE, 0f, 1f, 1f);
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
                bullet = tooltip.addPara(UGH_MD.paf("fe_phase"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), h,
                    "" + (int) PHASE_FLUX_ACTIVE_MULT + "x", "" + (int) PHASE_COOLDOWN_MULT + " times");
                bullet = tooltip.addPara(UGH_MD.paf("fe_cooldown"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), good,
                    "" + (int) DAMAGE_RESISTANCE + "%");
                bullet = tooltip.addPara(UGH_MD.paf("fe_weapon"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), bad,
                    "" + (int) FIRE_RATE_PENALTY + "%");
                
		tooltip.addSectionHeading(UGH_MD.base("compat"), Alignment.MID, opad);
                bullet = tooltip.addPara(UGH_MD.paf("comp_IPD"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), good,
                    "" + "" + "");
		bullet.setHighlight(UGH_MD.paf("comp_IPD_h"));
		bullet.setHighlightColors(h);
                
            tooltip.setBulletedListMode(null);
	}
        
        @Override
	public String getDescriptionParam(int index, HullSize hullSize) {
            /*if (index == 0) return "prevents ship from phasing";
            if (index == 1) return "50%";
            if (index == 2) return "33%";
            if (index == 3) return "5";
            if (index == 4) return "7";*/
            return null;
	}
	
        @Override
	public String getUnapplicableReason(ShipAPI ship) {
            if (ship.getHullSpec().getDefenseType() != ShieldType.PHASE)
                return "This modification is exclusive to Phase Ships";
            if (ship.getVariant().getHullMods().contains("ugh_inverteddilator"))
                return "Incompatible with Inverted Phase Dilator";
            return null;
	}
	
        @Override
	public boolean isApplicableToShip(ShipAPI ship) {
            return ship != null && ship.getHullSpec().getDefenseType() == ShieldType.PHASE &&
				!ship.getVariant().getHullMods().contains("ugh_inverteddilator");
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
            
            if (cloak.isCoolingDown() && ship.isAlive()){
		stats.getBallisticRoFMult().modifyMult(ugh_phasealloy_id, FIRE_RATE_PENALTY * 0.01f);
		stats.getEnergyRoFMult().modifyMult(ugh_phasealloy_id, FIRE_RATE_PENALTY * 0.01f);
		stats.getMissileRoFMult().modifyMult(ugh_phasealloy_id, FIRE_RATE_PENALTY * 0.01f);
		stats.getArmorDamageTakenMult().modifyMult(ugh_phasealloy_id, 1f - DAMAGE_RESISTANCE * 0.01f);
		stats.getHullDamageTakenMult().modifyMult(ugh_phasealloy_id, 1f - DAMAGE_RESISTANCE * 0.01f);
		stats.getEmpDamageTakenMult().modifyMult(ugh_phasealloy_id, 1f - DAMAGE_RESISTANCE * 0.01f);
                
		ship.setJitterUnder(this, JITTER_UNDER_COLOR, 1, 5, 0f, 3f + maxRangeBonus);
		ship.setJitter(this, JITTER_COLOR, 1, 3, 0f, 0 + maxRangeBonus);
                
                if (ship == playerShip) {
                    Global.getCombatEngine().maintainStatusForPlayerShip("ugh_phasealloy_ui1", "graphics/icons/hullsys/active_flare_launcher.png",
			"Phased Damper:", "50% less damage taken", false);
                    Global.getCombatEngine().maintainStatusForPlayerShip("ugh_phasealloy_ui2", "graphics/icons/hullsys/active_flare_launcher.png",
			"Phased Damper:", "33% firing speed", true);
                }
            } else {
                stats.getBallisticRoFMult().unmodify(ugh_phasealloy_id);
                stats.getEnergyRoFMult().unmodify(ugh_phasealloy_id);
                stats.getMissileRoFMult().unmodify(ugh_phasealloy_id);
                stats.getArmorDamageTakenMult().unmodify(ugh_phasealloy_id);
                stats.getHullDamageTakenMult().unmodify(ugh_phasealloy_id);
                stats.getEmpDamageTakenMult().unmodify(ugh_phasealloy_id);
            }
	}
}